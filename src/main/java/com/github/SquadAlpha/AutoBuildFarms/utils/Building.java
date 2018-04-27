package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.github.SquadAlpha.AutoBuildFarms.config.Config;
import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Scheduler;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


@SuppressWarnings("deprecation")
/*
 *
 *    This class is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This class is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this class.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/*
  @author Max
 */
public class Building {

    private byte[] blocks;
    private byte[] data;
    private short width;
    private short lenght;
    private short height;

    public Building(byte[] blocks, byte[] data, short width, short lenght, short height) {
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.lenght = lenght;
        this.height = height;
    }

    //Loading
    public static Building loadSchematic(String schematic) {
        String schematicFileName = schematic;
        if (!schematicFileName.endsWith(".schematic")) {
            schematicFileName = schematicFileName + ".schematic";
        }

        File schemFile = new File(Config.getSchematicsDir().getAbsolutePath() + File.separator + schematicFileName);
        if (!schemFile.exists()) {
            Reference.log.warning("Schematic file:" + schemFile.getAbsolutePath() + " not found loading putting in an example");
            try {
                schemFile.createNewFile();
                FileOutputStream out = new FileOutputStream(schemFile);
                Arrays.stream(Reference.standardSchematic).forEachOrdered(i -> {
                    try {
                        out.write(i);
                    } catch (IOException e) {
                        Utils.prettyPrintException(Reference.plugin.getLogger(), e);
                    }
                });
                out.close();
            } catch (IOException e) {
                Utils.prettyPrintException(Reference.log, e);
            }
        }
        return loadSchematic(schemFile);
    }

    private static Building loadSchematic(File schemFile) {
        try {
            NBTInputStream nbtStream = new NBTInputStream(new FileInputStream(schemFile));

            CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
            if (!schematicTag.getName().equals("Schematic")) {
                throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
            }

            CompoundMap schematic = schematicTag.getValue();
            if (!schematic.containsKey("Blocks")) {
                throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
            }

            short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
            short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
            short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

            String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
            if (!materials.equals("Alpha")) {
                throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
            }

            byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
            byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
            return new Building(blocks, blockData, width, length, height);
        } catch (IOException e) {
            Reference.log.severe(e.getLocalizedMessage());
            Arrays.stream(e.getStackTrace()).forEachOrdered(s -> Reference.log.severe(s.toString()));
            return null;
        }
    }

    public void pasteSchematic(World world, Location loc) {
        byte[] blocks = this.getBlocks();
        byte[] blockData = this.getData();

        short length = this.getLenght();
        short width = this.getWidth();
        short height = this.getHeight();
        Schedulers.async().run(() -> {
            Scheduler syncS = Schedulers.sync();
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {
                        int index = y * width * length + z * width + x; //the equation to store 3d in a 1d array
                        ReplaceCommand.replaceBlock rplC = new ReplaceCommand.replaceBlock(
                                new Location(world,
                                        x + loc.getX(),
                                        y + loc.getY(),
                                        z + loc.getZ()),
                                blocks[index],
                                blockData[index]);
                        syncS.run(rplC::go);
                    }
                }
            }
        });

    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items    The parent tag map
     * @param key      The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws IllegalArgumentException if the tag does not exist or the tag is not of the
     *                                  expected type
     */
    private static <T extends Tag> T getChildTag(CompoundMap items, String key, Class<T> expected) throws IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }

    /**
     * @return the blocks
     */
    public byte[] getBlocks() {
        return blocks;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return the width
     */
    public short getWidth() {
        return width;
    }

    /**
     * @return the lenght
     */
    public short getLenght() {
        return lenght;
    }

    /**
     * @return the height
     */
    public short getHeight() {
        return height;
    }


}


