package com.ugustavob.finsuppapi.utils;

public class StringFormatUtil {
    public static String toTitleCase(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        str = str.replaceAll("[^a-zA-Z0-9\\s]", "");

        str = str.trim().replaceAll("\\s+", " ");

        StringBuilder formattedString = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : str.toCharArray()) {
            if (c == ' ') {
                formattedString.append(c);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                formattedString.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                formattedString.append(Character.toLowerCase(c));
            }
        }

        return formattedString.toString();
    }

    public static String formatRole(String role) {
        role = role.trim().toUpperCase();

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return role;
    }
}
