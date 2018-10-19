package com.itgowo.socketframework;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lujianchao
 * 包大小长度最小为6
 * type 1 byte 消息类型  系统协议
 * length 4 byte 消息包总长度
 * dataType 1 byte 消息数据类型
 * dataSign 4 byte 消息数据校验
 * data n byte 消息数据
 */
public class PackageMessage {
    /**
     * 0   new 出来默认值
     * 1   读取type值
     * 2   读取length值
     * 3   读取dataType值
     * 4   读取dataSign值
     * 5   读取data数据完整
     * 6   读取data数据部分
     * 7   无效包
     */
    public static final int STEP_DEFAULT = 0;
    public static final int STEP_TYPE = 1;
    public static final int STEP_LENGTH = 2;
    public static final int STEP_DATA_TYPE = 3;
    public static final int STEP_DATA_SIGN = 4;
    public static final int STEP_DATA_COMPLETEED = 5;
    public static final int STEP_DATA_PART = 6;
    public static final int STEP_DATA_INVALID = 7;
    /**
     * 数据包类型为定长类型，数据长度固定
     */
    public static final int TYPE_FIX_LENGTH = 220;
    /**
     * 数据包类型为动态长度类型，数据长度不固定
     */
    public static final int TYPE_DYNAMIC_LENGTH = TYPE_FIX_LENGTH + 1;

    /**
     * 数据类型，指令
     */
    public static final int DATA_TYPE_COMMAND = 1;
    /**
     * 数据类型，心跳
     */
    public static final int DATA_TYPE_HEART = 2;
    /**
     * 数据类型，二进制
     */
    public static final int DATA_TYPE_BYTE = 3;
    /**
     * 数据类型，文本
     */
    public static final int DATA_TYPE_TEXT = 4;
    /**
     * 数据类型，Json文本
     */
    public static final int DATA_TYPE_JSON = 5;
    /**
     * 处理黏包分包
     */
    private static PackageMessage pack;
    /**
     * 下次处理的半包数据
     */
    private static ByteBuf nextData = Unpooled.buffer();
    /**
     * type 1 byte 消息类型  系统协议
     */
    private int type = 0;
    /**
     * length 4 byte 消息包总长度
     */
    private int length = 0;
    /**
     * 用于指示读取长度,相对于data数据
     */
    private int position = 0;
    /**
     * 数据类型，0-10 是预定义或保留值。
     */
    private int dataType = DATA_TYPE_HEART;
    /**
     * legth 4 0-Integer.MAX_VALUE 按照一定规则生成的验证信息，用来过滤脏数据请求
     */
    private int dataSign = 0;
    /**
     * 承载数据
     */
    private ByteBuffer data;
    /**
     * 当前处理进度，初始小于6byte不进入进度
     * 0   new 出来默认值
     * 1   读取type值
     * 2   读取length值
     * 3   读取dataType值
     * 4   读取dataSign值
     * 5   读取data数据完整
     * 6   读取data数据部分
     * 7   无效包
     */
    private int step = 0;

    public int getType() {
        return type;
    }

    public PackageMessage setType(int type) {
        this.type = type;
        return this;
    }

    public int getLength() {
        return length;
    }

    public PackageMessage setLength(int length) {
        this.length = length;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public PackageMessage setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getDataType() {
        return dataType;
    }

    public PackageMessage setDataType(int dataType) {
        this.dataType = dataType;
        return this;
    }

    public int getDataSign() {
        return dataSign;
    }

    public PackageMessage setDataSign(int dataSign) {
        this.dataSign = dataSign;
        return this;
    }

    public ByteBuffer getData() {
        return data;
    }

    public PackageMessage setData(ByteBuffer data) {
        this.data = data;
        return this;
    }

    private PackageMessage() {
    }

    public static List<PackageMessage> packageMessage(ByteBuf byteBuf) {
        List<PackageMessage> messageList = new ArrayList<>();
        try {
            while (true) {
                PackageMessage packageMessage = decodePackageMessage(byteBuf);
                if (packageMessage != null && packageMessage.isCompleted()) {
                    messageList.add(packageMessage);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    private static PackageMessage decodeFixLengthPackageMessage(ByteBufInputStream inputStream) throws IOException {
        return null;
    }

    private static PackageMessage decodeDynamicLengthPackageMessage(ByteBufInputStream inputStream) throws IOException {
        if (pack.step == STEP_TYPE) {
            pack.setLength(inputStream.readInt());
            pack.step = STEP_LENGTH;
        }
        if (pack.step == STEP_LENGTH) {
            pack.setDataType(inputStream.readByte());
            pack.step = STEP_DATA_TYPE;
        }
        if (pack.step == STEP_DATA_TYPE) {
            if (pack.getLength() < 6) {
                pack.step = STEP_DATA_INVALID;
                return null;
            }
            if (pack.getLength() == 6) {
                pack.step = STEP_DATA_COMPLETEED;
                return pack;
            }
            //pack.getLength>6情况
            if (inputStream.available() < 4) {
                //可能存在数据读取一半情况，直接返回，返回后由上游处理器暂存输入流剩余数据，下次合并输入流。
                return pack;
            }
            pack.dataSign = inputStream.readInt();
            pack.step = STEP_DATA_SIGN;
        }
        if (pack.step == STEP_DATA_SIGN) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(pack.getLength() - 10);
            pack.setData(byteBuffer);
            //数据包大小在已有数据范围内，即要执行拆包操作
            if (byteBuffer.capacity() <= inputStream.available()) {
                byte[] bytes = new byte[byteBuffer.capacity()];
                inputStream.read(bytes);
                byteBuffer.put(bytes);
                byteBuffer.flip();
                pack.step = STEP_DATA_COMPLETEED;
                return pack;
            } else {
                //处理半包结果，即保存部分数据
                if (inputStream.available() > 0) {
                    byte[] bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);
                    byteBuffer.put(bytes);
                }
                pack.step = STEP_DATA_PART;
                return pack;
            }
        }
        //半包处理
        if (pack.step == STEP_DATA_PART) {
            int partLength = pack.getData().capacity() - pack.getData().position();
            if (partLength <= inputStream.available()) {
                byte[] bytes = new byte[partLength];
                inputStream.read(bytes);
                ByteBuffer byteBuffer = pack.getData();
                byteBuffer.put(bytes);
                byteBuffer.flip();
                pack.step = STEP_DATA_COMPLETEED;
            } else {
                if (inputStream.available() > 0) {
                    byte[] bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);
                    ByteBuffer byteBuffer = pack.getData();
                    byteBuffer.put(bytes);
                }
            }
        }
        return pack;
    }

    private static synchronized PackageMessage decodePackageMessage(ByteBuf byteBuf) throws IOException {
        nextData.writeBytes(byteBuf);
        if (nextData.readableBytes() < 6) {
            return null;
        }
        //nextData大于6，则正常处理
        ByteBufInputStream inputStream = new ByteBufInputStream(nextData);
        int type = inputStream.readByte();
        if (TYPE_FIX_LENGTH == type || TYPE_DYNAMIC_LENGTH == type) {
            if (pack == null) {
                pack = new PackageMessage();
            }
            pack.setType(type);
            pack.step = STEP_TYPE;
        } else {
            return null;
        }
        PackageMessage packageMessage = null;
        if (pack.getType() == TYPE_FIX_LENGTH) {
            packageMessage = decodeFixLengthPackageMessage(inputStream);
        } else if (pack.getType() == TYPE_DYNAMIC_LENGTH) {
            return decodeDynamicLengthPackageMessage(inputStream);
        }
        return packageMessage;
    }

    /**
     * 是否数据结束，是完整包数据
     *
     * @return
     */
    public boolean isCompleted() {
        return step == STEP_DATA_COMPLETEED;
    }

    /**
     * 数组转换成整数型，数组长度小于等于4有效，长度多余4则只转换前4个。
     *
     * @param b
     * @return b=null return 0；
     */
    public static int byteArrayToInt(byte[] b) {
        if (b == null) {
            return 0;
        }
        if (b.length == 3) {
            return (b[2] & 0xFF) | (b[1] & 0xFF) << 8 | (b[0] & 0xFF) << 16;
        }
        if (b.length == 2) {
            return (b[1] & 0xFF) | (b[0] & 0xFF) << 8;
        }
        if (b.length == 1) {
            return b[0] & 0xFF;
        }
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    /**
     * 整数转换成数组
     *
     * @param a
     * @return byte length=4
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{(byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF), (byte) (a & 0xFF)};
    }

    /**
     * 获取简单数据签名
     *
     * @param bytes
     * @return
     */
    public static int dataSign(byte[] bytes) {
        byte[] bytes1 = new byte[4];
        if (bytes == null) {
            return 0;
        }
        if (bytes.length < 10) {
            return 1;
        }
        int position = bytes.length / 4;
        bytes1[0] = (byte) position;
        bytes1[1] = bytes[position];
        position = bytes.length * 3 / 4;
        bytes1[2] = (byte) position;
        bytes1[3] = bytes[position];
        return byteArrayToInt(bytes1);
    }


}
