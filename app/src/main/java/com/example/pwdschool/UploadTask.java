package com.example.pwdschool;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class UploadTask extends AsyncTask<Void, Integer, Void> {
    private final NotificationManagerCompat notificationManager;
    private final NotificationCompat.Builder notificationBuilder;
    private final UploadDatabaseHelper dbHelper;

    public UploadTask(NotificationManagerCompat manager, NotificationCompat.Builder builder, UploadDatabaseHelper dbHelper) {
        this.notificationManager = manager;
        this.notificationBuilder = builder;
        this.dbHelper = dbHelper;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public int getRowCount() {
        int count = 0;
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + UploadDatabaseHelper.TABLE_UPLOAD, null);
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        int rowCount = getRowCount();
        for (int i = 0; i <= rowCount; i++) {
            // Upload logic for each element
            // Update progress and notify
            publishProgress(i);
            try {
                Thread.sleep(500); // Simulating upload time
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
        int totalRowCount = getRowCount(); // Get the total row count
        notificationBuilder.setProgress(totalRowCount, uploadedCount, false)
                .setContentText(uploadedCount + " out of " + totalRowCount + " uploaded");
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
