package org.educa.ffegen.helper;

public class SanitizerHelper {
    private static final String INVALID_CHARS = "[\\\\/:*?\"<>|]";

    // Palabras reservadas en Windows (no distinguen mayúsculas)
    private static final String[] RESERVED_NAMES = {
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    };

    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return "default";
        }

        // Reemplazar caracteres inválidos con "_"
        String sanitized = input.replaceAll(INVALID_CHARS, "_");

        // Eliminar espacios al inicio o final
        sanitized = sanitized.trim();

        // Evitar nombres reservados en Windows
        for (String reserved : RESERVED_NAMES) {
            if (sanitized.equalsIgnoreCase(reserved)) {
                sanitized = "_" + sanitized;
                break;
            }
        }

        // Evitar nombres vacíos
        if (sanitized.isEmpty()) {
            sanitized = "default";
        }

        return sanitized;
    }
}
