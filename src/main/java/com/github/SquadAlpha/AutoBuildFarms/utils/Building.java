package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.Config;
import com.github.SquadAlpha.AutoBuildFarms.Reference;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.DataException;
import lombok.Getter;

import java.io.File;
import java.io.IOException;


@SuppressWarnings("deprecation")
public class Building {

    @Getter
    private final String name;
    @Getter
    private final CuboidClipboard clipboard;

    public Building(String name, File schem) {
        this.name = name;
        CuboidClipboard clip = null;
        try {
            clip = SchematicFormat.MCEDIT.load(schem);
        } catch (IOException | DataException e) {
            Reference.log.severe("Could not load schematic:" + name + " at " + schem.getAbsolutePath());
            Reference.log.severe(e.getLocalizedMessage());
            clip = new CuboidClipboard(new Vector());
        } finally {
            this.clipboard = clip;
        }
    }

    //TODO write this
    /*public void PlaceBuilding(){
        BukkitScheduler
    }*/

    public static Building loadSchematic(String schematic) {
        String schematicFileName = schematic;
        if (!schematicFileName.endsWith(".schematic")) {
            schematicFileName = schematicFileName + ".schematic";
        }

        File schemFile = new File(Config.getSchematicsDir().getAbsolutePath() + File.separator + schematicFileName);
        return new Building(schematic, schemFile);
    }
}
