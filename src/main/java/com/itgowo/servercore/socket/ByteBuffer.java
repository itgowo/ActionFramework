package com.itgowo.servercore.socket;

import java.io.IOException;

/**
 * @author lujianchao
 * 数组缓存处理类
 * 故事：
 *      写一个TCP长连接方案，遇到黏包分包问题，服务端于是用ByteArrayOutStream实现了，感觉太过于麻烦，
 *  于是用java nio 的 ByteBuffer，但是不太灵活，最后用Netty的ByteBuf类，豁然开朗，尽然可以把代码
 *  压缩到这么少，转念一想，Android端怎么实现呢？毕竟引入Netty包太大，即使是部分代码也很大。Nio的ByteBuffer
 *  呢？遇见好用的自然看不上不太灵活的，于是写了此类解决。
 */
public class ByteBuffer {
    /**
     * 指针位置，即将读取的位置
     */
    private int readerIndex;
    /**
     * 指针位置，即将写入的位置
     */
    private int writerIndex;
    private byte[] data;

    private ByteBuffer(int capacity) {
        data = new byte[capacity];
    }

    /**
     * 重置指针位置
     * @param position
     * @return
     * @throws ByteBufferException
     */
    public ByteBuffer readerIndex(int position) throws ByteBufferException {
        if (position <= writerIndex) {
            readerIndex = position;
        }
        throw new ByteBufferException("readerIndex < writerIndex");
    }

    /**
     * 读取指针位置
     * @return
     */
    public int readerIndex() {
        return readerIndex;
    }

    /**
     * 写入指针位置
     * @return
     */
    public int writerIndex() {
        return writerIndex;
    }

    /**
     * 当前可读数据量，writerIndex - readerIndex
     * @return
     */
    public int readableBytes() {
        return writerIndex - readerIndex;
    }

    /**
     * 当前可写入数据量，每次触发扩容后都不一样
     * @return
     */
    public int writableBytes() {
        return data.length - writerIndex;
    }

    public static ByteBuffer newByteBuffer() {
        return new ByteBuffer(256);
    }

    public static ByteBuffer newByteBuffer(int capacity) {
        return new ByteBuffer(capacity);
    }

    /**
     * 当前容量，当写入数据超过当前容量后自动扩容
     * @return
     */
    public int capacity() {
        return data.length;
    }

    /**
     * 读取数据到byte，1 byte，从readIndex位置开始
     * @return
     * @throws ByteBufferException
     */
    public int read() throws ByteBufferException {
        if (readableBytes() > 0) {
            int i = data[readerIndex];
            readerIndex++;
            return i;
        } else {
            throw new ByteBufferException("readableBytes = 0");
        }
    }

    /**
     * 读取integer值，读4 byte转换为integer，从readIndex位置开始
     * @return
     * @throws ByteBufferException
     */
    public int readInt() throws ByteBufferException {
        if (readableBytes() >= 4) {
            int result = byteArrayToInt(data, readerIndex);
            readerIndex += 4;
            return result;
        } else {
            throw new ByteBufferException("readableBytes < 4");
        }
    }

    /**
     * 读取数据到bytes，从readIndex位置开始
     * @param bytes
     * @return
     * @throws ByteBufferException
     */
    public int readBytes(byte[] bytes) throws ByteBufferException {
        if (readableBytes() >= bytes.length) {
            int result = readBytesFromBytes(data, bytes, readerIndex);
            readerIndex += bytes.length;
            return result;
        } else {
            throw new ByteBufferException("readableBytes < " + bytes.length);
        }
    }

    /**
     * 写入Byte数据，1 byte
     * @param b
     * @return
     */
    public int writeByte(byte b) {
        checkWriteLengthAndInit(1);
        data[writerIndex] = b;
        writerIndex++;
        return 1;

    }

    /**
     * 写入int值的byte转换结果，即丢弃高位
     * @param b
     * @return
     */
    public int write(int b) {
        checkWriteLengthAndInit(1);
        data[writerIndex] = (byte) b;
        writerIndex++;
        return 1;

    }

    /**
     * 写入integer数据，4 byte
     * @param b
     * @return
     */
    public int writeInt(int b) {
        checkWriteLengthAndInit(4);
        writeBytesToBytes(intToByteArray(b), data, writerIndex);
        writerIndex += 4;
        return 4;

    }

    /**
     * 写入数组
     * @param b
     * @return
     */
    public int writeBytes(byte[] b) {
        checkWriteLengthAndInit(b.length);
        writeBytesToBytes(b, data, writerIndex);
        writerIndex += b.length;
        return b.length;

    }

    /**
     * 检查写入数据长度，如果不够则扩容
     *
     * @param addLength
     */
    private void checkWriteLengthAndInit(int addLength) {
        if (writableBytes() < addLength) {
            byte[] newBytes = new byte[(data.length + addLength) * 2];
            writeBytesToBytes(data, newBytes, 0);
            data = newBytes;
        }
    }

    /**
     * 数组转换成整数型
     *
     * @param b
     * @return return 0；
     */
    private int byteArrayToInt(byte[] b, int position) {
        return b[position + 3] & 0xFF | (b[position + 2] & 0xFF) << 8 | (b[position + 1] & 0xFF) << 16 | (b[position] & 0xFF) << 24;
    }

    /**
     * 数据读取，从一个数组中读取一部分数组
     *
     * @param src
     * @param result
     * @param position
     * @return
     */
    private int readBytesFromBytes(byte[] src, byte[] result, int position) {
        System.arraycopy(src, position, result, 0, result.length);
        return src.length;
    }

    /**
     * 数组复制，向一个数组写入一个数组数组
     *
     * @param src            来源数组
     * @param target         被写入新数据数组
     * @param targetPosition 新数组被写入位置
     * @return
     */
    private int writeBytesToBytes(byte[] src, byte[] target, int targetPosition) {
        System.arraycopy(src, 0, target, targetPosition, src.length);
        return src.length;
    }

    /**
     * 整数转换成数组
     *
     * @param i
     * @return byte length=4
     */
    private byte[] intToByteArray(int i) {
        return new byte[]{(byte) ((i >> 24) & 0xFF), (byte) ((i >> 16) & 0xFF), (byte) ((i >> 8) & 0xFF), (byte) (i & 0xFF)};
    }

    public static class ByteBufferException extends IOException {
        ByteBufferException(String message) {
            super(message);
        }
    }
}