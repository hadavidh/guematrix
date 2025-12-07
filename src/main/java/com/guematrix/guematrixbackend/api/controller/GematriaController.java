package com.guematrix.guematrixbackend.api.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GematriaController {

    // Alphabet hébraïque (22 lettres, sans finales) pour Atbash, Albam, Sidouri
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

    @GetMapping("/gematria")
    public Map<String, Object> compute(
            @RequestParam String text,
            @RequestParam(name = "method", required = false, defaultValue = "hechrechi") String method
    ) {
        // normaliser le texte : enlever les espaces
        String normalizedText = text.replaceAll("\\s+", "");

        // Normaliser et mapper le nom de méthode
        String inputMethod = method.trim().toLowerCase();
        String normalizedMethod;
        String methodNameForJson;

        switch (inputMethod) {
            case "gadol", "mispar_gadol" -> {
                normalizedMethod = "gadol";
                methodNameForJson = "MISPAR_GADOL";
            }
            case "katan", "mispar_katan" -> {
                normalizedMethod = "katan";
                methodNameForJson = "MISPAR_KATAN";
            }
            case "atbash", "mispar_atbash" -> {
                normalizedMethod = "atbash";
                methodNameForJson = "MISPAR_ATBASH";
            }
            case "siduri", "sidouri", "mispar_siduri", "ordinal" -> {
                normalizedMethod = "siduri";
                methodNameForJson = "MISPAR_SIDURI";
            }
            case "albam", "mispar_albam" -> {
                normalizedMethod = "albam";
                methodNameForJson = "MISPAR_ALBAM";
            }
            default -> {
                normalizedMethod = "hechrechi";
                methodNameForJson = "MISPAR_HECHRACHI";
            }
        }

        int total = 0;
        List<Map<String, Object>> details = new ArrayList<>();

        for (int i = 0; i < normalizedText.length(); i++) {
            char originalChar = normalizedText.charAt(i);
            char baseChar = toBaseLetter(originalChar); // gère ך/כ, ם/מ, etc.

            Integer baseValue = HECHRACHI_VALUES.get(baseChar);
            if (baseValue == null) {
                // caractère non reconnu (ponctuation, etc.) -> on ignore
                continue;
            }

            int letterValue;
            char displayChar; // ce qu’on mettra dans le JSON

            switch (normalizedMethod) {
                case "hechrechi" -> {
                    letterValue = baseValue;
                    displayChar = originalChar;
                }
                case "gadol" -> {
                    // mispar gadol : les finales ont des valeurs 500-900
                    if (originalChar == 'ך')      letterValue = 500;
                    else if (originalChar == 'ם') letterValue = 600;
                    else if (originalChar == 'ן') letterValue = 700;
                    else if (originalChar == 'ף') letterValue = 800;
                    else if (originalChar == 'ץ') letterValue = 900;
                    else                          letterValue = baseValue;
                    displayChar = originalChar;
                }
                case "katan" -> {
                    // mispar katan : réduction des valeurs à 1–9
                    int tmp = baseValue % 9;
                    letterValue = (tmp == 0 && baseValue > 0) ? 9 : tmp;
                    displayChar = originalChar;
                }
                case "atbash" -> {
                    // Atbash : première lettre <-> dernière, etc.
                    int idx = ALEPHBET.indexOf(baseChar);
                    if (idx >= 0) {
                        int mappedIdx = ALEPHBET.length() - 1 - idx;
                        char mappedBaseChar = ALEPHBET.charAt(mappedIdx); // ex: א -> ת, ב -> ש, ...

                        // valeur de la lettre Atbash (gematria classique)
                        Integer mappedValue = HECHRACHI_VALUES.get(mappedBaseChar);
                        letterValue = (mappedValue != null ? mappedValue : baseValue);

                        // Affichage : si c'est la DERNIÈRE lettre de la chaîne et qu’elle a une forme finale,
                        // on utilise la forme finale (כ->ך, מ->ם, נ->ן, פ->ף, צ->ץ)
                        if (i == normalizedText.length() - 1) {
                            Character finalForm = BASE_TO_FINAL.get(mappedBaseChar);
                            displayChar = (finalForm != null ? finalForm : mappedBaseChar);
                        } else {
                            displayChar = mappedBaseChar;
                        }
                    } else {
                        // lettre pas dans ALEPHBET (cas rare)
                        letterValue = baseValue;
                        displayChar = originalChar;
                    }
                }
                case "albam" -> {
                    // Albam : alphabet coupé en deux lignes de 11 lettres
                    // א-כ  <->  ל-ת
                    int idx = ALEPHBET.indexOf(baseChar);
                    if (idx >= 0) {
                        int mappedIdx = (idx < 11) ? idx + 11 : idx - 11;
                        char mappedBaseChar = ALEPHBET.charAt(mappedIdx);

                        Integer mappedValue = HECHRACHI_VALUES.get(mappedBaseChar);
                        letterValue = (mappedValue != null ? mappedValue : baseValue);

                        // Comme Atbash : forme finale si c'est la dernière lettre et si possible
                        if (i == normalizedText.length() - 1) {
                            Character finalForm = BASE_TO_FINAL.get(mappedBaseChar);
                            displayChar = (finalForm != null ? finalForm : mappedBaseChar);
                        } else {
                            displayChar = mappedBaseChar;
                        }
                    } else {
                        letterValue = baseValue;
                        displayChar = originalChar;
                    }
                }
                case "siduri" -> {
                    // Mispar Sidouri : rang de la lettre dans ALEPHBET (1..22)
                    int idx = ALEPHBET.indexOf(baseChar);
                    if (idx >= 0) {
                        letterValue = idx + 1;
                    } else {
                        letterValue = 0; // ou baseValue si tu préfères
                    }
                    displayChar = originalChar;
                }
                default -> {
                    letterValue = baseValue;
                    displayChar = originalChar;
                }
            }

            total += letterValue;

            Map<String, Object> letterInfo = new HashMap<>();
            letterInfo.put("letter", String.valueOf(displayChar));
            letterInfo.put("value", letterValue);
            details.add(letterInfo);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        response.put("method", methodNameForJson);
        response.put("value", total);
        response.put("details", details);

        return response; // Spring convertit en JSON
    }

    // Convertit les finales ךםןףץ en leurs lettres de base כמנפצ
    private static char toBaseLetter(char ch) {
        Character base = FINAL_TO_BASE.get(ch);
        return (base != null ? base : ch);
    }
}