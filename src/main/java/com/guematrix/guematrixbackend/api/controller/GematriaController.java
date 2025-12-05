package com.guematrix.guematrixbackend.api.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GematriaController {

    // Valeurs de mispar hechrechi
    private static final Map<Character, Integer> HECHRACHI_VALUES = Map.ofEntries(
            Map.entry('א', 1),
            Map.entry('ב', 2),
            Map.entry('ג', 3),
            Map.entry('ד', 4),
            Map.entry('ה', 5),
            Map.entry('ו', 6),
            Map.entry('ז', 7),
            Map.entry('ח', 8),
            Map.entry('ט', 9),
            Map.entry('י', 10),
            Map.entry('כ', 20),
            Map.entry('ך', 20), // kaf finale
            Map.entry('ל', 30),
            Map.entry('מ', 40),
            Map.entry('ם', 40), // mem finale
            Map.entry('נ', 50),
            Map.entry('ן', 50), // noun finale
            Map.entry('ס', 60),
            Map.entry('ע', 70),
            Map.entry('פ', 80),
            Map.entry('ף', 80), // pe finale
            Map.entry('צ', 90),
            Map.entry('ץ', 90), // tsadi finale
            Map.entry('ק', 100),
            Map.entry('ר', 200),
            Map.entry('ש', 300),
            Map.entry('ת', 400)
    );

    @GetMapping("/gematria")
    public Map<String, Object> compute(@RequestParam String text) {
        String normalized = text.replaceAll("\\s+", "");

        int total = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            Integer value = HECHRACHI_VALUES.get(ch);

            if (value != null) {
                total += value;
                Map<String, Object> letterInfo = new HashMap<>();
                letterInfo.put("letter", String.valueOf(ch));
                letterInfo.put("value", value);
                details.add(letterInfo);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        response.put("method", "MISPAR_HECHRACHI");
        response.put("value", total);
        response.put("details", details);

        return response; // Spring convertit ça en JSON automatiquement
    }
}