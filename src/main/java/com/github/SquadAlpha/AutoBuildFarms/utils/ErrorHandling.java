package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;

import java.util.Arrays;

public class ErrorHandling {
    private final AutoBuildFarms plugin;

    public ErrorHandling(AutoBuildFarms plugin) {
        this.plugin = plugin;
    }

    public boolean silentNPE(Runnable r) {
        boolean success;
        try {
            r.run();
            success = true;
        } catch (NullPointerException e) {
            success = false;
        }
        return success;
    }

    public boolean NPE(Runnable r) {
        boolean success;
        try {
            r.run();
            success = true;
        } catch (NullPointerException e) {
            success = false;
            this.plugin.getLog().warning(e.getMessage());
            this.plugin.getLog().warning(e.getLocalizedMessage());
            Arrays.asList(e.getStackTrace()).forEach(s -> this.plugin.getLog().warning(s.toString()));
        }
        return success;
    }

}
