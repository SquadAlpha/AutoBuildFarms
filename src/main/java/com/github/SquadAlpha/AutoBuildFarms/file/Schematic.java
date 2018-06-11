package com.github.SquadAlpha.AutoBuildFarms.file;


import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

@Getter
@Setter
public class Schematic {
    private final String name;
    private final Vector size;
    private final AutoBuildFarms plugin;
    private Vector origin;
    private Vector offset;

    private HashMap<BlockVector, BaseBlock> blocks;

    private Schematic(AutoBuildFarms plugin, String schematicName, Vector size) {
        this.plugin = plugin;
        this.name = schematicName;
        this.size = size;
        this.blocks = new HashMap<>();
    }

    public static Schematic chest(AutoBuildFarms plugin, String name) {
        Schematic schematic = new Schematic(plugin, name, new Vector(1, 1, 1));
        //noinspection deprecation
        schematic.setBlock(new BlockVector(0, 0, 0), new BaseBlock(Material.CHEST.getId()));
        plugin.getLog().warning("Loaded default chest:" + schematic.getName() + " with " + schematic.getBlocks().size() + " blocks");
        return schematic;
    }

    public static Schematic load(AutoBuildFarms plugin, String name, InputStream stream) throws IOException {
        NBTInputStream nbtStream = new NBTInputStream(
                new GZIPInputStream(stream));

        // Schematic tag
        Tag rootTag = nbtStream.readTag();
        nbtStream.close();
        if (!rootTag.getName().equals("Schematic")) {
            throw new InvalidObjectException("Tag \"Schematic\" does not exist or is not first");
        }

        CompoundTag schematicTag = (CompoundTag) rootTag.getValue();

        // Check
        CompoundMap nbtMap = schematicTag.getValue();
        if (!nbtMap.containsKey("Blocks")) {
            throw new InvalidObjectException("Schematic file is missing a \"Blocks\" tag");
        }

        // Get information
        short width = getChildTag(nbtMap, "Width", ShortTag.class);
        short length = getChildTag(nbtMap, "Length", ShortTag.class);
        short height = getChildTag(nbtMap, "Height", ShortTag.class);

        // Check type of Schematic
        String materials = getChildTag(nbtMap, "Materials", StringTag.class);
        if (!materials.equals("Alpha")) {
            throw new InvalidObjectException("Schematic file is not an Alpha schematic");
        }

        // Get blocks
        byte[] blockId = getChildTag(nbtMap, "Blocks", ByteArrayTag.class);
        byte[] blockData = getChildTag(nbtMap, "Data", ByteArrayTag.class);
        byte[] addId = new byte[0];
        short[] blocks = new short[blockId.length]; // Have to later combine IDs

        // We support 4096 block IDs using the same method as vanilla Minecraft, where
        // the highest 4 bits are stored in a separate byte array.
        if (nbtMap.containsKey("AddBlocks")) {
            addId = getChildTag(nbtMap, "AddBlocks", ByteArrayTag.class);
        }

        // Combine the AddBlocks data with the first 8-bit block ID
        for (int index = 0; index < blockId.length; index++) {
            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
                blocks[index] = (short) (blockId[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
                }
            }
        }

        // Need to pull out tile entities
        List<? extends Tag<?>> tileEntities = getChildTag(nbtMap, "TileEntities", ListTag.class);
        Map<BlockVector, Map<String, Tag<?>>> tileEntitiesMap = new HashMap<>();

        for (Tag<?> tag : tileEntities) {
            if (!(tag instanceof CompoundTag)) continue;
            CompoundTag t = (CompoundTag) tag;

            int x = 0;
            int y = 0;
            int z = 0;

            Map<String, Tag<?>> values = new HashMap<>();

            for (Map.Entry<String, Tag<?>> entry : t.getValue().entrySet()) {
                switch (entry.getKey()) {
                    case "x":
                        if (entry.getValue() instanceof IntTag) {
                            x = ((IntTag) entry.getValue()).getValue();
                        }
                        break;
                    case "y":
                        if (entry.getValue() instanceof IntTag) {
                            y = ((IntTag) entry.getValue()).getValue();
                        }
                        break;
                    case "z":
                        if (entry.getValue() instanceof IntTag) {
                            z = ((IntTag) entry.getValue()).getValue();
                        }
                        break;
                }

                values.put(entry.getKey(), entry.getValue());
            }

            BlockVector vec = new BlockVector(x, y, z);
            tileEntitiesMap.put(vec, values);
        }

        Vector size = new Vector(width, height, length);
        Schematic schematic = new Schematic(plugin, name, size);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    int index = y * width * length + z * width + x;
                    BlockVector pt = new BlockVector(x, y, z);
                    BaseBlock block = getBlockForId(blocks[index], blockData[index]);

                    if (tileEntitiesMap.containsKey(pt)) {
                        block.setNbtData(new CompoundTag("", new CompoundMap(tileEntitiesMap.get(pt))));
                    }
                    schematic.setBlock(pt, block);
                }
            }
        }
        plugin.getLog().info("Loaded schematic:" + schematic.getName() + " with " + schematic.getBlocks().size() + " blocks");
        return schematic;
    }

    public static Schematic load(AutoBuildFarms plugin, String name, File file) throws IOException {
        return load(plugin, name, new FileInputStream(file));
    }

    public static Schematic load(AutoBuildFarms plugin, String schematicName, String fileName) throws IOException {
        File file;
        try {
            file = AutoBuildFarms.getPlugin().getBundledFile(fileName);
            return load(plugin, schematicName, file);
        } catch (IllegalArgumentException e) {
            return chest(plugin, schematicName);
        }
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items    The parent tag map
     * @param key      The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws InvalidObjectException if the tag does not exist or the tag is not of the expected type
     */
    private static <E, T extends Tag<E>> E getChildTag(CompoundMap items, String key,
                                                       Class<T> expected) throws InvalidObjectException {

        if (!items.containsKey(key)) {
            throw new InvalidObjectException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new InvalidObjectException(
                    key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag).getValue();
    }

    public static BaseBlock getBlockForId(int id, short data) {
        return new BaseBlock(id, data);
    }

    private void setBlock(BlockVector pt, BaseBlock block) {
        this.getBlocks().put(pt, block);
    }

    public void place(Location location) {
        Scheduler async = Schedulers.async();
        Scheduler sync = Schedulers.sync();
        World world = location.getWorld();
        //Run the task creation async but sync when looping through a block partition
        async.run(() -> {
            AtomicInteger i = new AtomicInteger(this.getBlocks().size());
            this.getPlugin().getLog().info("Placing " + this.getName() + " at " + location);
            this.getBlocks().forEach((blockVector, baseBlock) -> sync.runLater(() -> {

                Block block = world.getBlockAt(location.add(blockVector));
                //noinspection deprecation
                block.setTypeIdAndData(baseBlock.getId(),
                        (byte) baseBlock.getData(),
                        false);

            }, i.getAndDecrement()));
        });
    }

    @Data
    private static class BaseBlock {
        private int id;
        private int data;
        private CompoundTag nbtData;


        /**
         * Construct a block with the given ID and a data value of 0.
         *
         * @param id ID value
         * @see #setId(int)
         */
        public BaseBlock(int id) {
            setId(id);
            setData(0);
        }


        public BaseBlock(int id, int data) {
            setId(id);
            setData(data);
        }


        public BaseBlock(int id, int data, CompoundTag nbtData) {
            setId(id);
            setData(data);
            setNbtData(nbtData);
        }

        public BaseBlock(BaseBlock other) {
            this(other.getId(), other.getData(), other.getNbtData());
        }


        public void setIdAndData(int id, int data) {
            this.setId(id);
            this.setData(data);
        }

        public boolean hasWildcardData() {
            return getData() == -1;
        }

        public boolean hasNbtData() {
            return nbtData != null;
        }

        public String getNbtId() {
            CompoundTag nbtData = getNbtData();
            if (nbtData == null) {
                return "";
            }
            Tag idTag = nbtData.getValue().get("id");
            if (idTag instanceof StringTag) {
                return ((StringTag) idTag).getValue();
            } else {
                return "";
            }
        }

        public int getType() {
            return getId();
        }

        public void setType(int type) {
            setId(type);
        }

        public boolean isAir() {
            return getType() == 0;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof BaseBlock)) {
                return false;
            }

            final BaseBlock otherBlock = (BaseBlock) o;

            return getType() == otherBlock.getType() && getData() == otherBlock.getData();
        }

        public boolean equalsFuzzy(BaseBlock o) {
            return (getType() == o.getType()) && (getData() == o.getData() || getData() == -1 || o.getData() == -1);
        }

        @Override
        public int hashCode() {
            int ret = getId() << 3;
            if (getData() != (byte) -1) ret |= getData();
            return ret;
        }

        @Override
        public String toString() {
            return "Block{ID:" + getId() + ", Data: " + getData() + "}";
        }
    }


}