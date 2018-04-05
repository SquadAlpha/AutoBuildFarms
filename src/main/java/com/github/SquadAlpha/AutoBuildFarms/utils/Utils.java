package com.github.SquadAlpha.AutoBuildFarms.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    public static void saveResource(JavaPlugin plugin, String resourcePath, String destPath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = plugin.getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in the jar");
        }

        File outFile = new File(destPath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(plugin.getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                if (!outFile.exists()) {
                    try {
                        outFile.createNewFile();
                    } catch (IOException e) {
                        prettyPrintException(plugin.getLogger(), e);
                    }
                }
                FileOutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                //TODO seems to not handle a bunch of NULL characters well But just changed
                while (in.available() > 0) {
                    len = in.read(buf);
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
            prettyPrintException(plugin.getLogger(), ex);
        }
    }

    public static void prettyPrintException(Logger logger, Exception e) {
        logger.severe(e.getLocalizedMessage());
        Arrays.stream(e.getStackTrace()).forEachOrdered(s -> logger.severe(s.toString()));
    }
}
