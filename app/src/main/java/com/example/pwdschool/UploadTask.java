package com.example.pwdschool;

import android.os.AsyncTask;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class UploadTask extends AsyncTask<Void, Integer, Void> {
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public UploadTask(NotificationManagerCompat manager, NotificationCompat.Builder builder) {
        this.notificationManager = manager;
        this.notificationBuilder = builder;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int i = 0; i <= 26; i++) {
            // Upload logic for each element
            // Update progress and notify
            publishProgress(i);
            try {
                Thread.sleep(1000); // Simulating upload time
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int uploadedCount = values[0];
        notificationBuilder.setProgress(26, uploadedCount, false)
                .setContentText(uploadedCount + " out of 26 uploaded");
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        notificationBuilder.setProgress(0, 0, false)
                .setContentText("Upload completed")
                .setOngoing(false);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
