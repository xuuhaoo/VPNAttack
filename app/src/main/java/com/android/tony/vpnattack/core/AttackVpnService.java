package com.android.tony.vpnattack.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.tony.vpnattack.R;
import com.android.tony.vpnattack.core.service.LocalBridgeReadThread;
import com.android.tony.vpnattack.core.service.LocalBridgeWriteThread;

import java.io.IOException;

public class AttackVpnService extends VpnService {
    private static final String TAG = AttackVpnService.class.getSimpleName();

    public static final String ACTION_DISCONNECT = "com.android.tony.vpnattack.action_disconnect";

    public static final String VPN_ADDRESS = "10.0.0.1"; // Only IPv4 support for now

    public static final String VPN_ROUTE = "0.0.0.0"; // Intercept everything

    private static boolean isRunning = false;

    private ParcelFileDescriptor mParcelFileDescriptor = null;

    private LocalBridgeReadThread mLocalBridgeReadThread;

    private LocalBridgeWriteThread mLocalBridgeWriteThread;

    private BroadcastReceiver mDisconnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mParcelFileDescriptor != null) {
                destroy();
                stopSelf();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        //断开广播注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DISCONNECT);
        registerReceiver(mDisconnectReceiver, intentFilter);
        //初始化VPN
        mParcelFileDescriptor = initVPN();
        //初始化接收线程
        mLocalBridgeReadThread = new LocalBridgeReadThread("LocalBridgeReadThread", mParcelFileDescriptor, this);
        mLocalBridgeReadThread.start();
        mLocalBridgeWriteThread = new LocalBridgeWriteThread("LocalBridgeWriteThread", mParcelFileDescriptor, this);
        mLocalBridgeWriteThread.start();

        Log.i(TAG, "AttackVpnService Started...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private ParcelFileDescriptor initVPN() {
        Builder builder = new Builder();
        builder.addAddress(VPN_ADDRESS, 32);
        builder.addRoute(VPN_ROUTE, 0);
        builder.setSession(getString(R.string.app_name));
        return builder.establish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
        Log.i(TAG, "Destroy");
        unregisterReceiver(mDisconnectReceiver);
    }

    private void destroy() {
        isRunning = false;
        if (mLocalBridgeReadThread != null) {
            mLocalBridgeReadThread.shutdown();
        }
        try {
            mParcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

}