package com.ipev.controle_material;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class AppUpdater {

    private final Activity activity;
    private long downloadId;
    private ProgressDialog progressDialog;

    public AppUpdater(Activity activity) {
        this.activity = activity;
    }

    public void startDownload(String url, String fileName) {
        // Caminho do APK
        File apkFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        // Exclui APK antigo se existir
        if (apkFile.exists()) apkFile.delete();

        // Mostrar ProgressDialog
        showProgressDialog();

        // Registrar BroadcastReceiver para capturar quando o download terminar
        activity.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // Configurar DownloadManager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Atualizando aplicativo");
        request.setDescription("Baixando nova versão...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationUri(Uri.fromFile(apkFile));
        request.setAllowedOverMetered(true);
        request.setAllowedOverRoaming(true);

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Atualizando");
        progressDialog.setMessage("Baixando atualização...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    // BroadcastReceiver para detectar término do download
    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long receivedId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (receivedId == downloadId) {
                progressDialog.dismiss();
                activity.unregisterReceiver(this);
                instalarApk();
            }
        }
    };

    private void instalarApk() {
        File apkFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "AppUpdate.apk");

        if (!apkFile.exists()) {
            Toast.makeText(activity, "Arquivo não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri apkUri = FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".provider", // Certifique-se de declarar esse FileProvider no manifest
                apkFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Erro ao abrir instalador", Toast.LENGTH_SHORT).show();
        }
    }
}
