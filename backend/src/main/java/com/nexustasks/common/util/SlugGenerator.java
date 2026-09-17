package com.nexustasks.common.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Component
public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHES = Pattern.compile("(^-|-$)"); // Tirets aux extrémités
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-{2,}");

    /**
     * Génère un slug URL-friendly à partir d'une chaîne de caractères.
     * Ex: "Work & Travel!" -> "work-travel"
     */
    public String toSlug(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be null or blank for slug generation");
        }

        // 1. Normaliser (enlever les accents : é -> e)
        String normalized = Normalizer.normalize(input.trim().toLowerCase(), Normalizer.Form.NFD);

        // 2. Remplacer les espaces par des tirets
        String withDashes = WHITESPACE.matcher(normalized).replaceAll("-");

        // 3. Enlever les caractères non latins (ponctuation, symboles)
        String noSpecialChars = NON_LATIN.matcher(withDashes).replaceAll("");

        // 4. Enlever les tirets multiples
        String noMultipleDashes = MULTIPLE_DASHES.matcher(noSpecialChars).replaceAll("-");

        // 5. Enlever les tirets aux extrémités
        return EDGESDHES.matcher(noMultipleDashes).replaceAll("");
    }
}
