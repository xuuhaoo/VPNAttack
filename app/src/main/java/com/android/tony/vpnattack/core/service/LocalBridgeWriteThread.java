package com.android.tony.vpnattack.core.service;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.xuhao.android.libsocket.sdk.client.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.client.OkSocketFactory;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalBridgeWriteThread extends Thread {

    public static final String TAG = "LocalBridgeReadThread";

    private ParcelFileDescriptor mParcelFileDescriptor;

    private VpnService mVpnService;

    private ExecutorService mThreadPool;

    public LocalBridgeWriteThread(String name, ParcelFileDescriptor parcelFileDescriptor, VpnService vpnService) {
        super(name);
        this.mParcelFileDescriptor = parcelFileDescriptor;
        this.mVpnService = vpnService;
        mThreadPool = Executors.newCachedThreadPool();
        OkSocketOptions okSocketOptions = new OkSocketOptions.Builder()
                .setSocketFactory(new OkSocketFactory() {
                    @Override
                    public Socket createSocket(ConnectionInfo info, OkSocketOptions options) throws Exception {
                        Socket socket = new Socket();
                        socket.bind(new InetSocketAddress(0));//local port
                        mVpnService.protect(socket);
                        return socket;
                    }
                })
                .build();
    }

    @Override
    public void run() {
        try {
            FileChannel localOut = new FileOutputStream(mParcelFileDescriptor.getFileDescriptor()).getChannel();
            Log.i(TAG, "本地输出服务创建成功");
            while (!Thread.interrupted()) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (mParcelFileDescriptor != null) {
            try {
                mParcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mThreadPool != null) {
            mThreadPool.shutdownNow();
        }
        interrupt();
    }

}
