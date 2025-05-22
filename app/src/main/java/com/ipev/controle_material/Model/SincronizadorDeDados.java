package com.ipev.controle_material.Model;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SincronizadorDeDados {
    private static final String TAG = "SincronizadorDeDados";
    private static final String CAMINHO_FIREBASE = "Banco_Dados_IPEV";

    public static void sincronizarCacheComFirebase(Context context) {
        if (!VersaoManager.isCacheDesatualizado(context)) {
            Log.d(TAG, "Cache está atualizado. Nenhuma sincronização necessária.");
            return;
        } else {
            Log.d(TAG, "Cache está desatualizada. Sincronizando...");
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(CAMINHO_FIREBASE).child("itens");
        databaseRef.get().addOnCompleteListener(task -> {
            Log.d(TAG, "Sincronização iniciada.");
            if (task.isSuccessful()) {
                Log.d(TAG, "Dados do Firebase obtidos com sucesso.");
                try {
                    DataSnapshot snapshot = task.getResult();
                    JSONArray dados = new JSONArray();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Object value = child.getValue();
                        if (value instanceof Map) {
                            JSONObject obj = new JSONObject((Map) value);
                            dados.put(obj);
                        } else if (value instanceof String) {
                            JSONObject obj = new JSONObject((String) value);
                            dados.put(obj);
                        } else {
                            Log.w(TAG, "Formato inesperado no nó: " + child.getKey());
                        }
                    }

                    // Salva o cache atualizado com nova versão
                    JsonCacheManager.salvar(context, dados, VersaoManager.getVersaoAtual());
                    Log.d(TAG, "Cache sincronizado com Firebase.");
                } catch (JSONException e) {
                    Log.e(TAG, "Erro ao converter dados JSON", e);
                }
            } else {
                Exception e = task.getException();
                Log.e(TAG, "Erro ao buscar dados do Firebase: " + (e != null ? e.getMessage() : "Erro desconhecido"), e);
            }

            Log.d(TAG, "Sincronização concluída.");
        });
    }

    public static void atualizarItemNoFirebase(Context context, JSONObject novoItem) {
        try {
            String chave = novoItem.getString("id"); // suposição: todos os objetos têm um campo "id"
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(CAMINHO_FIREBASE).child(chave);
            ref.setValue(novoItem.toString());

            // Atualiza localmente também
            JSONArray dados = JsonCacheManager.obterDados(context);
            JSONArray novoArray = new JSONArray();

            for (int i = 0; i < dados.length(); i++) {
                JSONObject item = dados.getJSONObject(i);
                if (item.getString("id").equals(chave)) {
                    novoArray.put(novoItem);
                } else {
                    novoArray.put(item);
                }
            }

            JsonCacheManager.salvar(context, novoArray, VersaoManager.getVersaoAtual());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}