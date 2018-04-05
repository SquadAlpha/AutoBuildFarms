package com.github.SquadAlpha.AutoBuildFarms.utils;

import java.util.Arrays;
import java.util.logging.Logger;

public class Utils {

    public static void prettyPrintException(Logger logger, Exception e) {
        logger.severe(e.getLocalizedMessage() + " caused by " + e.getCause().getLocalizedMessage());
        Arrays.stream(e.getStackTrace()).forEachOrdered(s -> logger.severe(s.toString()));
    }


    public static String replaceWithParagraph(String pattern, String subject) {
        return subject.replaceAll("(?<!\\\\)" + pattern, "§");
    }
}
