package com.guematrix.guematrixbackend.api.service;

import com.guematrix.guematrixbackend.api.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GematriaService {

    // Valeurs hébraïques pour mispar hechrechi
    private static final Map<Character, Integer> MISPAR_HECHRACHI_VALUES = Map.ofEntries(
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

    public GematriaResponse compute(String text, GematriaMethod method) {
        if (text == null) {
            text = "";
        }

        String normalized = normalize(text);
        int total = 0;
        List<GematriaLetterDetail> details = new ArrayList<>();

        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);

            Integer value = switch (method) {
                case MISPAR_HECHRACHI -> MISPAR_HECHRACHI_VALUES.get(ch);
                // plus tard : d'autres méthodes ici
            };

            if (value != null) {
                total += value;
                details.add(new GematriaLetterDetail(String.valueOf(ch), value));
            }
            // si value == null, on ignore le caractère (espace, ponctuation, etc.)
        }

        return new GematriaResponse(text, method, total, details);
    }

    private String normalize(String text) {
        // pour l’instant : on enlève juste les espaces
        return text.replaceAll("\\s+", "");
    }
}