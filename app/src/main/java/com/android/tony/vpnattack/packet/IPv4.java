package com.android.tony.vpnattack.packet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class IPv4 extends IP {

    public static final int IP4_FIXED_HEADER_LENGTH = 20;

    /**
     * type of service
     */
    public static final byte offset_tos = 1;

    /**
     * total length,including the length of header (16bits)
     */
    public static final short offset_tot_len = 2;

    /**
     * identification (16bits)
     */
    public static final short offset_id = 4;

    /**
     * 3bits used for flag,and 13bits for fragment offset (total 16bits)
     */
    public static final short offset_flag_and_frag_off = 6;

    /**
     * time to live
     */
    public static final byte offset_ttl = 8;

    /**
     * protocol
     */
    public static final byte offset_protocol = 9;

    /**
     * header checksum (16bits)
     */
    public static final short offset_header_checksum = 10;

    /**
     * source ip (32bits)
     */
    public static final int offset_src_ip = 12;

    /**
     * destination ip (32bits)
     */
    public static final int offset_dest_ip = 16;

    public IPv4(byte[] packetBuf) {
        super(packetBuf);
    }

    public IPv4(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    @Override
    public int getHeaderLength() {
        return getInternetHeaderLength();
    }

    @Override
    public void fillHeaderToBuffer(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) (getVersion() << 4 | getInternetHeaderLength()));
        byteBuffer.put(getTypeOfService());
        byteBuffer.putShort(this.getTotalLength());
        byteBuffer.putShort(getIdentification());
        byteBuffer.putShort(getFlagAndSegment());
        byteBuffer.put(getTTL());
        byteBuffer.put(getProtocol());
        byteBuffer.putShort(getHeaderChecksum());
        byteBuffer.putInt(getSrcAddress());
        byteBuffer.putInt(getDestAddress());
    }

    /**
     * get the length of internet header,in bytes.
     *
     * @return the length of internet header,in bytes.
     */
    public byte getInternetHeaderLength() {
        byte ver_and_ihl = readByte(default_offset + offset_ver_and_ihl);
        return (byte) (ver_and_ihl & 0xf);
    }

    /**
     * set the length of internet header,in bytes.
     *
     * @param len the length of internet header,in bytes,
     *            the value must be 4*n and range from 20~60.
     */
    public void setInternetHeaderLength(int len) {
        // TODO: check length? (max header length = 60)
        int count = len >> 2;
        writeByte(default_offset + offset_ver_and_ihl, (byte) (0x40 | count));
    }

    public void setDefaultInternetHeaderLength() {
        setInternetHeaderLength(20);
    }

    /**
     * get type of service.
     *
     * @return type of service.
     */
    public byte getTypeOfService() {
        return readByte(default_offset + offset_tos);
    }

    public void setTypeOfService(byte tos) {
        writeByte(default_offset + offset_tos, tos);
    }

    /**
     * get the total length of this ip packet,in bytes.
     *
     * @return the total length of ip packet,in bytes.
     */
    public short getTotalLength() {
        return readShort(default_offset + offset_tot_len);
    }

    public void setTotalLength(int len) {
        writeShort(default_offset + offset_tot_len, (short) (len & 0xffff));
    }

    /**
     * get identification.
     *
     * @return identification.
     */
    public short getIdentification() {
        return readShort(default_offset + offset_id);
    }

    public void setIdentification(int id) {
        writeShort(default_offset + offset_id, (short) (id & 0xffff));
    }

    /**
     * get flag.
     *
     * @return flag.
     */
    public byte getFlag() {
        short flag_and_seg_off = readShort(default_offset + offset_flag_and_frag_off);
        return (byte) ((flag_and_seg_off >> 13) & 0x7);
    }

    /**
     * set flags.
     *
     * @param flags value ranges from 0 to 7.
     */
    public void setFlag(byte flags) {
        // TODO: check range 0~7
        short flag_and_seg_off = readShort(default_offset + offset_flag_and_frag_off);
        writeShort(default_offset + offset_flag_and_frag_off,
                (short) ((flags << 13) | (flag_and_seg_off & 0x1fff)));
    }

    /**
     * get segment offset.
     *
     * @return segment offset.
     */
    public int getSegmentOffset() {
        short flag_and_seg_off = readShort(default_offset + offset_flag_and_frag_off);
        return (flag_and_seg_off & 0x1fff);
    }

    /**
     * set segment offset.
     *
     * @param seg_offset value ranges from 0 to 0x1fff.
     */
    public void setSegmentOffset(int seg_offset) {
        // TODO: check range 0~0x1fff
        short flag_and_seg_off = readShort(default_offset + offset_flag_and_frag_off);
        writeShort(default_offset + offset_flag_and_frag_off,
                (short) ((flag_and_seg_off & 0xe000) | (seg_offset & 0x1fff)));
    }

    public short getFlagAndSegment() {
        return readShort(default_offset + offset_flag_and_frag_off);
    }

    /**
     * get time to live of this ip packet.
     *
     * @return TTL of the ip packet.
     */
    public byte getTTL() {
        return readByte(default_offset + offset_ttl);
    }

    /**
     * set time to live of this ip packet.
     *
     * @param ttl value ranges from 0 to 0xff
     */
    public void setTTL(int ttl) {
        // TODO: check range 0~0xff
        writeByte(default_offset + offset_ttl, (byte) (ttl & 0xff));
    }

    /**
     * get protocol.
     *
     * @return protocol code.For example, 6 for tcp,17 for udp.
     */
    public byte getProtocol() {
        return readByte(default_offset + offset_protocol);
    }

    /**
     * set protocol.
     *
     * @param protocol value ranges from 0 to 0xff.
     */
    public void setProtocol(int protocol) {
        // TODO: check range 0~0xff
        writeByte(default_offset + offset_protocol, (byte) (protocol & 0xff));
    }

    /**
     * get ip header checksum,it is read from packet.
     *
     * @return the checksum of ip header.
     */
    public short getHeaderChecksum() {
        return readShort(default_offset + offset_header_checksum);
    }

    public void setHeaderChecksum(short checksum) {
        writeShort(default_offset + offset_header_checksum, checksum);
    }

    /**
     * get source ipv4 address.
     *
     * @return source ip address.
     */
    public int getSrcAddress() {
        return readInt(default_offset + offset_src_ip);
    }

    /**
     * get source ipv4 address as a 4-length bytes array.
     *
     * @return source ip address as a 4-length bytes array,the byte order is Big-endian.
     */
    public InetAddress getSrcAddressAsInetAddress() throws UnknownHostException {
        byte[] bytes = new byte[4];
        mByteBuffer.position(default_offset + offset_src_ip);
        mByteBuffer.get(bytes, 0, 4);
        return InetAddress.getByAddress(bytes);
    }

    public byte[] getSrcAddressAsByteArray() {
        byte[] bytes = new byte[4];
        mByteBuffer.position(default_offset + offset_src_ip);
        mByteBuffer.get(bytes, 0, 4);
        return bytes;
    }

    /**
     * modify source ipv4 address.
     * Note: this operation wouldn't re-generate the checksum,you should re-generate by yourself.
     *
     * @param address the ipv4 address you want to set as source address.
     */
    public void setSrcAddress(int address) {
        writeInt(default_offset + offset_src_ip, address);
    }

    /**
     * get destination ipv4 address.
     *
     * @return destination ip address.
     */
    public int getDestAddress() {
        return readInt(default_offset + offset_dest_ip);
    }

    /**
     * get destination ipv4 address as a 4-length bytes array.
     *
     * @return destination ip address as a 4-length bytes array,the byte order is Big-endian.
     */
    public InetAddress getDestAddressAsInetAddress() throws UnknownHostException {
        byte[] bytes = new byte[4];
        mByteBuffer.position(default_offset + offset_dest_ip);
        mByteBuffer.get(bytes, 0, 4);
        return InetAddress.getByAddress(bytes);
    }

    public byte[] getDestAddressAsByteArray() {
        byte[] bytes = new byte[4];
        mByteBuffer.position(default_offset + offset_dest_ip);
        mByteBuffer.get(bytes, 0, 4);
        return bytes;
    }

    /**
     * modify destination ipv4 address.
     * Note: this operation wouldn't re-generate the checksum,you should re-generate by yourself.
     *
     * @param address the ipv4 address you want to set as destination address.
     */
    public void setDestAddress(int address) {
        writeInt(default_offset + offset_dest_ip, address);
    }

    /**
     * get the starting position of the data portion,the position is determined by internet header
     * length (IHL) and 'initial position for parsing packet'.
     *
     * @return the starting position of data.
     */
    @Override
    public int getDataPosition() {
        return default_offset + getInternetHeaderLength();
    }

    @Override
    public String toString() {
        InetAddress src = null;
        InetAddress des = null;
        try {
            src = getSrcAddressAsInetAddress();
            des = getDestAddressAsInetAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return " ver = " + getVersion()
                + " ihl = " + getInternetHeaderLength()
                + " tos = " + getTypeOfService()
                + " tot_len = " + getTotalLength()
                + " id = " + getIdentification()
                + " flag = " + getFlag()
                + " seg_off = " + getSegmentOffset()
                + " ttl = " + getTTL()
                + " protocol = " + getProtocol()
                + " head_checksum = " + getHeaderChecksum()
                + " __checksum__ = " + getHeaderChecksum()
                + " src = " + src == null ? "" : src.getHostName()
                + " dest = " + des == null ? "" : des.getHostName();
    }

}