package com.android.tony.vpnattack.core.service;

import com.android.tony.vpnattack.packet.IPv4;

public class NatSession {

    public int RemoteIP;
    public short RemotePort;
    public String RemoteHost;
    public int BytesSent;
    public int PacketSent;
    public long LastNanoTime;
    public IPv4 iPv4;

}
