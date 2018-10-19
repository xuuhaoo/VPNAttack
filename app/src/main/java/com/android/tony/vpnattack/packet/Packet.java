package com.android.tony.vpnattack.packet;

import java.nio.ByteBuffer;

/**
 * Created by didi on 2018/5/30.
 */

public abstract class Packet {
    protected ByteBuffer mByteBuffer;

    protected Packet(byte[] packetBuf) {
        mByteBuffer = ByteBuffer.allocate(packetBuf.length);
        mByteBuffer.put(packetBuf);
    }


    protected Packet(ByteBuffer byteBuffer) {
        mByteBuffer = byteBuffer;
    }

    /**
     * get the length of the header
     *
     * @return the header length
     */
    public abstract int getHeaderLength();

    /**
     * get the starting position of the data portion.
     *
     * @return the starting position of data.
     */
    public abstract int getDataPosition();

    /**
     * get the packet data according to getDataPosition();
     *
     * @return data bytes
     */
    public byte[] getDataBytes() {
        if (mByteBuffer == null) {
            return new byte[0];
        }

        int dataPosition = getDataPosition();
        if (dataPosition <= 0 || dataPosition > mByteBuffer.limit()) {
            return new byte[0];
        }
        mByteBuffer.position(dataPosition);
        byte[] result = new byte[mByteBuffer.remaining()];
        mByteBuffer.get(result);
        return result;
    }

    /**
     * @param byteBuffer
     */
    public abstract void fillHeaderToBuffer(ByteBuffer byteBuffer);

    /**
     * @return
     */
    public ByteBuffer getByteBuffer() {
        if (mByteBuffer == null) {
            return null;
        }
        return ByteBuffer.wrap(mByteBuffer.array());
    }

    protected byte readByte(int position) {
        return mByteBuffer.get(position);
    }

    protected short readShort(int position) {
        return mByteBuffer.getShort(position);
    }

    protected void writeByte(int position, byte value) {
        mByteBuffer.put(position, value);
    }

    protected void writeInt(int position, int value) {
        mByteBuffer.putInt(position, value);
    }

    protected void writeShort(int position, short value) {
        mByteBuffer.putShort(position, value);
    }

    protected int readInt(int position) {
        return mByteBuffer.getInt(position);
    }
}
