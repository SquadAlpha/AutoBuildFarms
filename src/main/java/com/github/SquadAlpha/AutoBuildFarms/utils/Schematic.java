package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.Reference;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;

import java.io.File;
import java.io.FileInputStream;

public class Schematic{

    @Getter
    private final String name;
    @Getter
    private final short[] blocks;
    @Getter
    private final byte[] data;
    @Getter
    private final short width;
    @Getter
    private final short length;
    @Getter
    private final short height;

    public Schematic(String name, short[] blocks, byte[] data, short width, short length, short height){
        this.name = name;
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.length = length;
        this.height = height;
    }


    public static Schematic loadSchematic(String name){
        if(!name.endsWith(".schematic"))
            name = name + ".schematic";
        File file = new File(Reference.plugin.getDataFolder() + "/schematics/" + name);
        if(!file.exists())
            return null;
        try{
            FileInputStream stream = new FileInputStream(file);
            NBTTagCompound nbtdata = NBTCompressedStreamTools.a(stream);

            short width = nbtdata.getShort("Width");
            short height = nbtdata.getShort("Height");
            short length = nbtdata.getShort("Length");

            byte[] blocks = nbtdata.getByteArray("Blocks");
            byte[] data = nbtdata.getByteArray("Data");

            byte[] addId = new byte[0];

            if(nbtdata.hasKey("AddBlocks")){
                addId = nbtdata.getByteArray("AddBlocks");
            }

            short[] sblocks = new short[blocks.length];
            for(int index = 0; index < blocks.length; index++){
                if((index >> 1) >= addId.length){
                    sblocks[index] = (short) (blocks[index] & 0xFF);
                }else{
                    if((index & 1) == 0){
                        sblocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blocks[index] & 0xFF));
                    }else{
                        sblocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blocks[index] & 0xFF));
                    }
                }
            }

            stream.close();
            return new Schematic(name, sblocks, data, width, length, height);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
