package com.example.pwdschool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkStatusUtility {

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final Handler mainHandler;
    private ConnectivityManager.NetworkCallback networkCallback;

    public NetworkStatusUtility(Context context) {
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public boolean isNetworkAvailable() {
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public void startMonitoringNetworkStatus(final NetworkStatusListener listener) {
        if (connectivityManager == null) {
            return;
        }

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                mainHandler.post(() -> listener.onNetworkAvailable());
            }

            @Override
            public void onLost(Network network) {
                mainHandler.post(() -> listener.onNetworkLost());
            }
        };

        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    public void stopMonitoringNetworkStatus() {
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public interface NetworkStatusListener {
        void onNetworkAvailable();

        void onNetworkLost();
    }
    public boolean isNetworkQualityGood() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://embeddedcreation.in/tribalpwd/adminPanelNewVer2/app_upload_Image.php") // Replace with an actual URL for testing
                    .build();
            long startTime = System.currentTimeMillis();
            Response response = client.newCall(request).execute();
            long endTime = System.currentTimeMillis();

            long timeTaken = endTime - startTime;
            // You can adjust this threshold as needed
            return timeTaken < 2000; // 2 seconds threshold for good network quality
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void showNetworkQualityAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Weak Network Quality");
        builder.setMessage("The network quality is too weak for the upload. Please try again later when the network is stable.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}


