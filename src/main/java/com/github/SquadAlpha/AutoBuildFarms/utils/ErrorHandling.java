package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;

import java.util.Arrays;
import java.util.logging.Logger;

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

    public boolean NPE(Runnable r, Logger log) {
        boolean success;
        try {
            r.run();
            success = true;
        } catch (NullPointerException e) {
            success = false;
            log.warning(e.getMessage());
            log.warning(e.getLocalizedMessage());
            Arrays.asList(e.getStackTrace()).forEach(s -> log.warning(s.toString()));
        }
        return success;
    }

}
