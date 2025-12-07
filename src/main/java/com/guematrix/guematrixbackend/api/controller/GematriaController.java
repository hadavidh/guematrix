package com.guematrix.guematrixbackend.api.controller;

import org.springframework.web.bind.annotation.*;
import com.guematrix.guematrixbackend.api.model.GematriaMethod;
import com.guematrix.guematrixbackend.api.model.GematriaResponse;
import com.guematrix.guematrixbackend.api.service.GematriaService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:5173")  // <--- AJOUT ICI
@RestController
public class GematriaController {

    private final GematriaService gematriaService;

    public GematriaController(GematriaService gematriaService) {
        this.gematriaService = gematriaService;
    }

    // On garde la même URL qu’avant : /gematria
    @GetMapping("/gematria")
    public GematriaResponse compute(
            @RequestParam String text,
            @RequestParam(name = "method", required = false) String methodParam
    ) {
        GematriaMethod method = resolveMethod(methodParam);
        return gematriaService.compute(text, method);
    }

    private GematriaMethod resolveMethod(String methodParam) {
        if (methodParam == null || methodParam.isBlank()) {
            return GematriaMethod.HECHRACHI;
        }

        String m = methodParam.trim().toLowerCase();

        switch (m) {
            case "gadol":
            case "mispar_gadol":
                return GematriaMethod.GADOL;

            case "katan":
            case "mispar_katan":
                return GematriaMethod.KATAN;

            case "atbash":
            case "mispar_atbash":
                return GematriaMethod.ATBASH;

            case "siduri":
            case "sidouri":
            case "mispar_siduri":
            case "ordinal":
                return GematriaMethod.SIDURI;

            case "albam":
            case "mispar_albam":
                return GematriaMethod.ALBAM;

            case "milui":
            case "miloui":
            case "miluy":
            case "mispar_milui":
            case "mispar_miloui":
                return GematriaMethod.MILUI;

            default:
                return GematriaMethod.HECHRACHI;
        }
    }
}