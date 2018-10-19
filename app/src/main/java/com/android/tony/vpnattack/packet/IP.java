package com.android.tony.vpnattack.packet;


import com.android.tony.vpnattack.packet.Packet;

import java.nio.ByteBuffer;

public abstract class IP extends Packet {


    protected int default_offset = 0;

    /**
     * ip version and internet header length
     */
    public static final byte offset_ver_and_ihl = 0;

    protected IP(byte[] packetBuf) {
        super(packetBuf);
    }


    protected IP(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    /**
     * set initial position for parsing packet.
     *
     * @param offset the initial position for parsing packet.
     */
    public final void setDefaultOffset(int offset) {
        default_offset = offset;
    }

    /**
     * get initial position which for starting to parse2TcpBytes packet.
     *
     * @return the initial position for parsing packet.
     */
    public final int getDefaultOffset() {
        return default_offset;
    }

    /**
     * get ip version.
     *
     * @return 4 for IPv4,and 6 for IPv6.
     */
    public final byte getVersion() {
        byte ver_and_ihl = readByte(default_offset + offset_ver_and_ihl);
        return (byte) ((ver_and_ihl >> 4) & 0xf);
    }


}