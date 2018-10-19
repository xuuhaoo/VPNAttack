package com.android.tony.vpnattack.packet;


import com.android.tony.vpnattack.core.utils.CommonMethods;

import java.nio.ByteBuffer;

/**
 * Created by didi on 2018/5/30.
 */

public class TCP extends Packet {
    public static final int TCP_FIXED_HEADER_LENGTH = 20;
    /**
     * source port(16bits)
     */
    public static final short offset_src_port = 0;

    /**
     * destination port(16bits)
     */
    public static final short offset_dest_port = 2;

    /**
     * sequence number (32bits)
     */
    public static final int offset_seq_num = 4;

    /**
     * acknowledgment number (32bits)
     */
    public static final int offset_ack_num = 8;

    /**
     * header length (8bits)
     */
    public static final byte offset_hdr_len = 12;

    /**
     * flags (8bits)
     */
    public static final byte offset_flags = 13;

    /**
     * receive window (16bits)
     */
    public static final short offset_recv_window = 14;
    /**
     * check sum (16Bits)
     */
    public static final short offset_checksum = 16;
    /**
     * emergency pointer (16Bits)
     */
    public static final short offset_emergency = 18;

    public static final byte FIN = 1;
    public static final byte SYN = 2;
    public static final byte RST = 4;
    public static final byte PSH = 8;
    public static final byte ACK = 16;
    public static final byte URG = 32;

    public TCP(IP ip) {
        super(parse2TCPBytes(ip));
    }

    public TCP(byte[] packetBuf) {
        super(packetBuf);
    }

    public TCP(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    public static byte[] parse2TCPBytes(IP ip) {
        ip.mByteBuffer.position(ip.getDataPosition());
        byte[] tcp = new byte[ip.mByteBuffer.remaining()];
        ip.mByteBuffer.get(tcp);
        return tcp;
    }

    /**
     * get source port.
     *
     * @return source port (1~65535).
     */
    public int getSrcPort() {
        return CommonMethods.getUnsignedShort(readShort(offset_src_port));
    }

    /**
     * modify source port.
     * Note: this operation wouldn't re-generate the checksum,you should re-generate by yourself.
     *
     * @param port the port you want to set as source port.
     */
    public void setSrcPort(short port) {
        writeShort(offset_src_port, port);
    }

    /**
     * get destination port.
     *
     * @return destination port (1~65535).
     */
    public int getDestPort() {
        return CommonMethods.getUnsignedShort(readShort(offset_dest_port));
    }

    /**
     * modify destination port.
     * Note: this operation wouldn't re-generate the checksum,you should re-generate by yourself.
     *
     * @param port the port you want to set as destination port.
     */
    public void setDestPort(short port) {
        writeShort(offset_dest_port, port);
    }

    /**
     * get sequence number.
     *
     * @return sequence number.
     */
    public int getSeqNum() {
        return readInt(offset_seq_num);
    }

    /**
     * get acknowledgment number.
     *
     * @return acknowledgment number.
     */
    public int getAckNum() {
        return readInt(offset_ack_num);
    }

    /**
     * get header length.
     *
     * @return header length.
     */
    public int getHeaderLength() {
        return (readByte(offset_hdr_len) & 0xf0) >> 2;
    }

    @Override
    public int getDataPosition() {
        return getHeaderLength();
    }

    public void setEmergencyPointer(short pointer) {
        writeShort(offset_emergency, pointer);
    }

    public short getEmergencyPointer() {
        return readShort(offset_emergency);
    }

    @Override
    public void fillHeaderToBuffer(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) getSrcPort());
        byteBuffer.putShort((short) getDestPort());

        byteBuffer.putInt(getSeqNum());
        byteBuffer.putInt(getAckNum());

        byteBuffer.put((byte) getHeaderLength());
        byteBuffer.put(getFlags());
        byteBuffer.putShort(getRecvWindow());

        byteBuffer.putShort(getChecksum());
        byteBuffer.putShort(getEmergencyPointer());
    }

    public byte getFlags() {
        return readByte(offset_flags);
    }

    /**
     * get receive window.
     *
     * @return receive window.
     */
    public short getRecvWindow() {
        return readShort(offset_recv_window);
    }

    /**
     * get the checksum of the packet,it is read from packet.
     *
     * @return the checksum of the packet.
     */
    public short getChecksum() {
        return readShort(offset_checksum);
    }

    public void setChecksum(short checksum) {
        writeShort(offset_checksum, checksum);
    }

    public boolean isFIN() {
        return getFlags() == FIN;
    }

    public boolean isSYN() {
        return (getFlags() & SYN) == SYN;
    }

    public boolean isRST() {
        return (getFlags() & RST) == RST;
    }

    public boolean isPSH() {
        return (getFlags() & PSH) == PSH;
    }

    public boolean isACK() {
        return (getFlags() & ACK) == ACK;
    }

    public boolean isURG() {
        return (getFlags() & URG) == URG;
    }

    public static boolean isTCP(int protocol) {
        return protocol == 6;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TCP{");
        sb.append("sourcePort=").append(getSrcPort());
        sb.append(", destinationPort=").append(getDestPort());
        sb.append(", sequenceNumber=").append(getSeqNum());
        sb.append(", acknowledgementNumber=").append(getAckNum());
        sb.append(", headerLength=").append(getHeaderLength());
        sb.append(", window=").append(getRecvWindow());
        sb.append(", checksum=").append(getChecksum());
        sb.append(", flags=");
        if (isFIN()) sb.append(" FIN");
        if (isSYN()) sb.append(" SYN");
        if (isRST()) sb.append(" RST");
        if (isPSH()) sb.append(" PSH");
        if (isACK()) sb.append(" ACK");
        if (isURG()) sb.append(" URG");
        sb.append('}');
        return sb.toString();
    }
}
