package com.example.pwdschool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.os.Looper;

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
}


