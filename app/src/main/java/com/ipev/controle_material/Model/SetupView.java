package com.ipev.controle_material.Model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;


public class SetupView {

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            return "N/A";
        }
    }

    public static String getNome(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return  sharedPref.getString("nome", "Usuário");
    }

    public static String getSetor(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return  sharedPref.getString("setor", "");
    }

    public static String getStatus(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPref.getString("status", "");
    }



    public static ArrayList<String> removeDuplicates(ArrayList<String> lista) {
        // Cria um HashSet para armazenar itens únicos
        ArrayList<String> lista_unica = new ArrayList<>();
        HashSet<String> conjunto = new HashSet<>();

        // Itera sobre a lista original
        for (int i = 0; i < lista.size(); i++) {
            // Se o conjunto não contiver o elemento, adiciona ao conjunto e mantém na lista
            if (!conjunto.contains(lista.get(i))) {
                conjunto.add(lista.get(i));

            } else { // Se o conjunto já contém o elemento, remove da lista
                lista.remove(i);
                i--; // Ajusta o índice após a remoção do elemento
            }
        }

        return lista_unica;
    }
}
