package com.ipev.controle_material.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class JsonCacheManager {
    private static final String FILE_NAME = "cache_dados.json";

    public static void salvar(Context context, JSONArray dados, String versao) {
        try {
            JSONObject root = new JSONObject();
            root.put("timestamp", versao);
            root.put("dados", dados);

            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(root.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject carregar(Context context) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            if (!file.exists()) return null;

            FileInputStream fis = context.openFileInput(FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder = new StringBuilder();
            String linha;

            while ((linha = reader.readLine()) != null) {
                builder.append(linha);
            }

            return new JSONObject(builder.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static String obterVersao(Context context) {
        JSONObject root = carregar(context);
        return root != null ? root.optString("timestamp", "") : "";
    }

    public static JSONArray obterDados(Context context) {
        JSONObject root = carregar(context);
        if (root != null) {
            return root.optJSONArray("dados");
        }
        return new JSONArray();
    }
}