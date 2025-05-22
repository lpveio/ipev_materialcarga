package com.ipev.controle_material.Model;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

public class VersaoManager {
    private static final String VERSAO_ATUAL = "12"; // ou gerado dinamicamente no Firebase

    public static String getVersaoAtual() {
        return VERSAO_ATUAL;
    }

    public static String gerarVersaoTimestamp() {
        return Instant.now().toString();
    }

    public static boolean isCacheDesatualizado(Context context) {
        String versaoCache = JsonCacheManager.obterVersao(context);
        return !VERSAO_ATUAL.equals(versaoCache);
    }
}