package com.android.tony.vpnattack.packet;



import java.nio.ByteBuffer;

/**
 * Created by didi on 2018/5/30.
 */

public class UDP extends Packet {
    public static final int UDP_FIXED_HEADER_LENGTH = 8;
    /**
     * source port(16bits)
     */
    public static final short offset_src_port = 0;

    /**
     * destination port(16bits)
     */
    public static final short offset_dest_port = 2;

    /**
     * udp head+data length (16bits)
     */
    public static final int offset_udp_length = 4;

    /**
     * check sum (16bits)
     */
    public static final int offset_check_sum = 6;

    public UDP(IP ip) {
        super(parse2UDPBytes(ip));
    }

    public UDP(byte[] packetBuf) {
        super(packetBuf);
    }

    public UDP(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    public static byte[] parse2UDPBytes(IP ip) {
        ip.mByteBuffer.position(ip.getDataPosition());
        byte[] udp = new byte[ip.mByteBuffer.remaining()];
        ip.mByteBuffer.get(udp);
        return udp;
    }

    /**
     * get source port.
     *
     * @return source port (1~65535).
     */
    public short getSrcPort() {
        return readShort(offset_src_port);
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
    public short getDestPort() {
        return readShort(offset_dest_port);
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
     * get the UDP length
     *
     * @return total length.
     */
    public short getUDPLength() {
        return readShort(offset_udp_length);
    }

    /**
     * set the UDP length
     *
     * @param totallength
     */
    public void setUDPLength(short totallength) {
        writeShort(offset_udp_length, totallength);
    }

    /**
     * set the check sum
     *
     * @param sum
     */
    public void setCheckSum(short sum) {
        writeShort(offset_check_sum, sum);
    }

    /**
     * get check sum
     *
     * @return check sum.
     */
    public short getCheckSum() {
        return readShort(offset_check_sum);
    }

    /**
     * get header length.
     *
     * @return header length.
     */
    public int getHeaderLength() {
        return UDP_FIXED_HEADER_LENGTH;
    }

    @Override
    public int getDataPosition() {
        return getHeaderLength();
    }

    @Override
    public void fillHeaderToBuffer(ByteBuffer byteBuffer) {
        byteBuffer.putShort(getSrcPort());
        byteBuffer.putShort(getDestPort());
        byteBuffer.putShort(getUDPLength());
        byteBuffer.putShort(getCheckSum());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UDP{");
        sb.append("sourcePort=").append(getSrcPort());
        sb.append(", destinationPort=").append(getDestPort());
        sb.append(", totalLength=").append(getUDPLength());
        sb.append(", checkSum=").append(getCheckSum());
        sb.append('}');
        return sb.toString();
    }

    public static boolean isUDP(int protocol) {
        return protocol == 17;
    }
}
