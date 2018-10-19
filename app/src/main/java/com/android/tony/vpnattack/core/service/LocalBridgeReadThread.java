package com.android.tony.vpnattack.core.service;

import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.android.tony.vpnattack.core.utils.ByteBufferPool;
import com.android.tony.vpnattack.packet.IPv4;
import com.android.tony.vpnattack.packet.TCP;
import com.android.tony.vpnattack.packet.UDP;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalBridgeReadThread extends Thread {

    public static final String TAG = "LocalBridgeReadThread";

    private ParcelFileDescriptor mParcelFileDescriptor;

    private VpnService mVpnService;

    private ExecutorService mThreadPool;

    public LocalBridgeReadThread(String name, ParcelFileDescriptor parcelFileDescriptor, VpnService vpnService) {
        super(name);
        this.mParcelFileDescriptor = parcelFileDescriptor;
        this.mVpnService = vpnService;
        mThreadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            FileChannel localIn = new FileInputStream(mParcelFileDescriptor.getFileDescriptor()).getChannel();
            Log.i(TAG, "本地输入服务创建成功");
            while (!Thread.interrupted()) {
                ByteBuffer byteBuffer = ByteBufferPool.acquire();
                int size = localIn.read(byteBuffer);
                if (size > 0) {
                    //虽然读取了很多倍的IP包结构,但是会不会出现断包呢.不得而知.
                    IPv4 iPv4 = new IPv4(byteBuffer);

                    if (TCP.isTCP(iPv4.getProtocol())) {
                        TCP tcp = new TCP(iPv4);
                        Log.i(TAG, "源地址和端口号:" + iPv4.getSrcAddressAsInetAddress().getHostAddress() + ":" + tcp.getSrcPort());
                        int portKey = tcp.getSrcPort();
                        NatSession session = NatSessionManager.getSession(portKey);
                        if (session == null) {
                            session = NatSessionManager.createSession(tcp.getSrcPort(), iPv4.getDestAddress(), (short) tcp.getDestPort());
                        }
                        session.iPv4 = iPv4;

                        Log.i(TAG, "TCP数据包头" + tcp.toString());
                        byte[] payload = tcp.getDataBytes();
                        Log.i(TAG, "TCP-payload" + new String(payload, "UTF-8"));


                    } else if (UDP.isUDP(iPv4.getProtocol())) {

                    }
                }
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
