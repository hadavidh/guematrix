package com.guematrix.guematrixbackend.api.service;

import com.guematrix.guematrixbackend.api.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GematriaService {

    // Alphabet hébraïque (22 lettres, sans finales) pour Atbash, Albam, Siduri, Hakadmi
    private static final String ALEPHBET = "אבגדהוזחטיכלמנסעפצקרשת";

    // Finales -> lettre de base (pour gérer ך/כ, ם/מ, etc.)
    private static final Map<Character, Character> FINAL_TO_BASE = Map.of(
            'ך', 'כ',
            'ם', 'מ',
            'ן', 'נ',
            'ף', 'פ',
            'ץ', 'צ'
    );

    // Lettres de base -> formes finales (pour la DERNIÈRE lettre Atbash/Albam)
    private static final Map<Character, Character> BASE_TO_FINAL = Map.of(
            'כ', 'ך',
            'מ', 'ם',
            'נ', 'ן',
            'פ', 'ף',
            'צ', 'ץ'
    );

    // Valeurs de base : mispar hechrechi
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
            Map.entry('ך', 20),
            Map.entry('ל', 30),
            Map.entry('מ', 40),
            Map.entry('ם', 40),
            Map.entry('נ', 50),
            Map.entry('ן', 50),
            Map.entry('ס', 60),
            Map.entry('ע', 70),
            Map.entry('פ', 80),
            Map.entry('ף', 80),
            Map.entry('צ', 90),
            Map.entry('ץ', 90),
            Map.entry('ק', 100),
            Map.entry('ר', 200),
            Map.entry('ש', 300),
            Map.entry('ת', 400)
    );

    // Orthographe des lettres pour le Milouï
    private static final Map<Character, String> MILUI_SPELLING = Map.ofEntries(
            Map.entry('א', "אלף"),
            Map.entry('ב', "בית"),
            Map.entry('ג', "גימל"),
            Map.entry('ד', "דלת"),
            Map.entry('ה', "הא"),
            Map.entry('ו', "וו"),
            Map.entry('ז', "זין"),
            Map.entry('ח', "חית"),
            Map.entry('ט', "טית"),
            Map.entry('י', "יוד"),
            Map.entry('כ', "כף"),
            Map.entry('ל', "למד"),
            Map.entry('מ', "מם"),
            Map.entry('נ', "נון"),
            Map.entry('ס', "סמך"),
            Map.entry('ע', "עין"),
            Map.entry('פ', "פה"),
            Map.entry('צ', "צדי"),
            Map.entry('ק', "קוף"),
            Map.entry('ר', "ריש"),
            Map.entry('ש', "שין"),
            Map.entry('ת', "תיו")
    );

    public GematriaResponse compute(String text, GematriaMethod method) {
        if (text == null) {
            text = "";
        }

        String normalizedText = text.replaceAll("\\s+", "");
        int total = 0;
        List<GematriaLetterDetail> details = new ArrayList<>();

        for (int i = 0; i < normalizedText.length(); i++) {
            char originalChar = normalizedText.charAt(i);
            char baseChar = toBaseLetter(originalChar);

            Integer baseValue = HECHRACHI_VALUES.get(baseChar);
            if (baseValue == null) {
                // caractère non hébreu / nikoud / ponctuation...
                continue;
            }

            int letterValue;
            String displayText = String.valueOf(originalChar); // ce qu'on affiche dans le JSON

            switch (method) {
                case HECHRACHI -> {
                    letterValue = baseValue;
                    displayText = String.valueOf(originalChar);
                }

                case GADOL -> {
                    if (originalChar == 'ך')      letterValue = 500;
                    else if (originalChar == 'ם') letterValue = 600;
                    else if (originalChar == 'ן') letterValue = 700;
                    else if (originalChar == 'ף') letterValue = 800;
                    else if (originalChar == 'ץ') letterValue = 900;
                    else                          letterValue = baseValue;
                    displayText = String.valueOf(originalChar);
                }

                case KATAN -> {
                    int tmp = baseValue % 9;
                    letterValue = (tmp == 0 && baseValue > 0) ? 9 : tmp;
                    displayText = String.valueOf(originalChar);
                }

                case SIDURI -> {
                    int idxSiduri = ALEPHBET.indexOf(baseChar);
                    if (idxSiduri >= 0) {
                        letterValue = idxSiduri + 1; // א=1, ב=2, ..., ת=22
                    } else {
                        letterValue = 0;
                    }
                    displayText = String.valueOf(originalChar);
                }

                case ATBASH -> {
                    int idx = ALEPHBET.indexOf(baseChar);
                    if (idx >= 0) {
                        int mappedIdx = ALEPHBET.length() - 1 - idx;
                        char mappedBaseChar = ALEPHBET.charAt(mappedIdx);

                        Integer mappedValue = HECHRACHI_VALUES.get(mappedBaseChar);
                        letterValue = (mappedValue != null ? mappedValue : baseValue);

                        char shown = mappedBaseChar;
                        if (i == normalizedText.length() - 1) {
                            Character finalForm = BASE_TO_FINAL.get(mappedBaseChar);
                            if (finalForm != null) {
                                shown = finalForm;
                            }
                        }
                        displayText = String.valueOf(shown);
                    } else {
                        letterValue = baseValue;
                        displayText = String.valueOf(originalChar);
                    }
                }

                case ALBAM -> {
                    int idx = ALEPHBET.indexOf(baseChar);
                    if (idx >= 0) {
                        int mappedIdx = (idx < 11) ? idx + 11 : idx - 11;
                        char mappedBaseChar = ALEPHBET.charAt(mappedIdx);

                        Integer mappedValue = HECHRACHI_VALUES.get(mappedBaseChar);
                        letterValue = (mappedValue != null ? mappedValue : baseValue);

                        char shown = mappedBaseChar;
                        if (i == normalizedText.length() - 1) {
                            Character finalForm = BASE_TO_FINAL.get(mappedBaseChar);
                            if (finalForm != null) {
                                shown = finalForm;
                            }
                        }
                        displayText = String.valueOf(shown);
                    } else {
                        letterValue = baseValue;
                        displayText = String.valueOf(originalChar);
                    }
                }

                case MILUI -> {
                    String spelling = MILUI_SPELLING.get(baseChar);
                    if (spelling == null) {
                        letterValue = baseValue;
                        displayText = String.valueOf(originalChar);
                    } else {
                        letterValue = computeHechrechiForString(spelling);
                        displayText = spelling; // ex : אלף, בית...
                    }
                }

                case HAKADMI -> {
                    int idx = ALEPHBET.indexOf(baseChar);
                    if (idx >= 0) {
                        // Préfixe de l'alphabet : א..lettre
                        String prefix = ALEPHBET.substring(0, idx + 1);
                        letterValue = computeHechrechiForString(prefix);
                        displayText = prefix; // ex : א, אבגד, אבגדהוזחטיכלמ
                    } else {
                        letterValue = baseValue;
                        displayText = String.valueOf(originalChar);
                    }
                }

                default -> {
                    letterValue = baseValue;
                    displayText = String.valueOf(originalChar);
                }
            }

            total += letterValue;
            details.add(new GematriaLetterDetail(displayText, letterValue));
        }

        GematriaResponse response = new GematriaResponse();
        response.setText(text);
        response.setMethod(method);
        response.setValue(total);
        response.setDetails(details);

        return response;
    }

    private char toBaseLetter(char ch) {
        Character base = FINAL_TO_BASE.get(ch);
        return (base != null ? base : ch);
    }

    // Gematria hechrechi d'une chaîne (utilisé pour Milouï et Hakadmi)
    private int computeHechrechiForString(String s) {
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            char baseCh = toBaseLetter(ch);
            Integer v = HECHRACHI_VALUES.get(baseCh);
            if (v != null) {
                sum += v;
            }
        }
        return sum;
    }
}