package com.android.tony.vpnattack.core.service;

import android.util.SparseArray;

import com.android.tony.vpnattack.core.utils.CommonMethods;

public class NatSessionManager {

    static final int MAX_SESSION_COUNT = 4096;
    static final long SESSION_TIMEOUT_NS = 120 * 1000000000L;
    static final SparseArray<NatSession> mSessionsMap = new SparseArray<NatSession>();

    public static NatSession getSession(int portKey) {
        return mSessionsMap.get(portKey);
    }

    public static int getSessionCount() {
        return mSessionsMap.size();
    }

    static void clearExpiredSessions() {
        long now = System.nanoTime();
        for (int i = mSessionsMap.size() - 1; i >= 0; i--) {
            NatSession natSession = mSessionsMap.valueAt(i);
            if (now - natSession.LastNanoTime > SESSION_TIMEOUT_NS) {
                mSessionsMap.removeAt(i);
            }
        }
    }

    public static void clearAllSessions() {
        mSessionsMap.clear();
    }

    public static NatSession createSession(int portKey, int remoteIP, short remotePort) {
        if (mSessionsMap.size() > MAX_SESSION_COUNT) {
            clearExpiredSessions();
        }

        NatSession natSession = new NatSession();
        natSession.LastNanoTime = System.nanoTime();
        natSession.RemoteIP = remoteIP;
        natSession.RemotePort = remotePort;

        if (natSession.RemoteHost == null) {
            natSession.RemoteHost = CommonMethods.ipIntToString(remoteIP);
        }
        mSessionsMap.put(portKey, natSession);
        return natSession;
    }
}