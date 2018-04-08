package com.github.SquadAlpha.AutoBuildFarms.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.function.Consumer;

public class ReplaceCommand implements Consumer<ReplaceCommand.replaceBlock> {

    @Override
    public void accept(replaceBlock replaceBlock) {
        replaceBlock.go();
    }

    public static class replaceBlock {

        private final byte type;
        private final byte data;
        private final Block b;


        public replaceBlock(Location l, byte type, byte data) {
            this.type = type;
            this.data = data;
            this.b = l.getBlock();
        }

        public void go() {
            b.setTypeIdAndData(type, data, false);
        }
    }
}
