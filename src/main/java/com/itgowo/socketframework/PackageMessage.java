package com.itgowo.socketframework;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
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
    public static final int TYPE_FIX_LENGTH = 120;
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
     * 标准格式协议头大小
     */
    public static final int LENGTH_HEAD = 10;
    /**
     * 处理黏包分包
     */
    private static PackageMessage pack;
    /**
     * 下次处理的半包数据
     */
    private static ByteBuf nextData = Unpooled.buffer();
    /**
     * type 1 byte 消息类型  系统协议  范围-127 ~ 128
     */
    private int type = TYPE_DYNAMIC_LENGTH;
    /**
     * length 4 byte 消息包总长度
     */
    private int length = 0;
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
    private ByteBuf data;
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

    public ByteBuf getData() {
        return data;
    }

    public byte[] getDataBytes() {
        byte[] bytes = new byte[data.readableBytes()];
        data.getBytes(0, bytes);
        return bytes;
    }

    public PackageMessage setData(ByteBuf data) {
        this.data = data;
        data.readerIndex(0);
        length = data.readableBytes() + LENGTH_HEAD;
        dataSign = dataSign();
        return this;
    }

    public PackageMessage setData(byte[] data) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(data);
        setData(byteBuf);
        return this;
    }

    private PackageMessage() {
    }

    public static PackageMessage getPackageMessage() {
        return new PackageMessage();
    }

    public ByteBuf encodePackageMessage() {
        if (type != TYPE_FIX_LENGTH && type != TYPE_DYNAMIC_LENGTH) {
            return null;
        }
        if (length <= 6) {
            return null;
        }
        if (dataType == 0) {
            return null;
        }

        if (data.readerIndex(0).readableBytes() != length - LENGTH_HEAD) {
            return null;
        }
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(type)
                .writeInt(length)
                .writeByte(dataType)
                .writeInt(dataSign)
                .writeBytes(data);
        return byteBuf;
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

    private static PackageMessage decodeFixLengthPackageMessage(ByteBuf inputStream) throws IOException {
        return null;
    }

    private static PackageMessage decodeDynamicLengthPackageMessage(ByteBuf byteBuf) throws IOException {
        if (pack.step == STEP_TYPE) {
            pack.setLength(byteBuf.readInt());
            pack.step = STEP_LENGTH;
        }
        if (pack.step == STEP_LENGTH) {
            pack.setDataType(byteBuf.readByte());
            pack.step = STEP_DATA_TYPE;
        }
        if (pack.step == STEP_DATA_TYPE) {
            if (pack.getLength() < 6) {
                pack.step = STEP_DATA_INVALID;
                return pack;
            }
            if (pack.getLength() == 6) {
                pack.step = STEP_DATA_COMPLETEED;
                return pack;
            }
            //pack.getLength>6情况
            if (byteBuf.readableBytes() < 4) {
                //可能存在数据读取一半情况，直接返回，返回后由上游处理器暂存输入流剩余数据，下次合并输入流。
                return pack;
            }
            pack.dataSign = byteBuf.readInt();
            pack.step = STEP_DATA_SIGN;
        }
        if (pack.step == STEP_DATA_SIGN) {
            pack.data = Unpooled.buffer();
            //数据包大小在已有数据范围内，即要执行拆包操作
            int dataLength = pack.getLength() - LENGTH_HEAD;
            if (dataLength <= byteBuf.readableBytes()) {
                pack.data.writeBytes(byteBuf,dataLength);
                pack.step = STEP_DATA_COMPLETEED;
                return pack;
            } else {
                //处理半包结果，即保存部分数据
                if (byteBuf.readableBytes() > 0) {
                    pack.getData().writeBytes(byteBuf, byteBuf.readableBytes());
                }
                pack.step = STEP_DATA_PART;
                return pack;
            }
        }
        //半包处理
        if (pack.step == STEP_DATA_PART) {
            int partLength = pack.getLength() - LENGTH_HEAD - pack.data.readableBytes();
            if (partLength <= byteBuf.readableBytes()) {
                pack.data.writeBytes(byteBuf, partLength);
                pack.step = STEP_DATA_COMPLETEED;
            } else {
                if (byteBuf.readableBytes() > 0) {
                    pack.data.writeBytes(byteBuf, byteBuf.readableBytes());
                }
            }
        }
        return pack;
    }

    /**
     * 获取data长度，如果没有data，则返回0，返回结果只作为正常数据参考,不一定是data真实长度
     *
     * @return
     */
    public int getDataLength() {
        if (length <= 6 || data == null) {
            return 0;
        }
        return length - LENGTH_HEAD;
    }

    private static synchronized PackageMessage decodePackageMessage(ByteBuf byteBuf) throws IOException {
        nextData.writeBytes(byteBuf);
        if (nextData.readableBytes() < 6) {
            return null;
        }
        //nextData大于6，则正常处理

        int type = nextData.readByte();
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
            packageMessage = decodeFixLengthPackageMessage(nextData);
        } else if (pack.getType() == TYPE_DYNAMIC_LENGTH) {
            packageMessage = decodeDynamicLengthPackageMessage(nextData);
        }
        byteBuf.discardReadBytes();
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
     * 获取简单数据签名，注意先初始化length
     *
     * @return
     */
    public int dataSign() {
        byte[] bytes1 = new byte[4];
        if (data == null) {
            return 0;
        }
        if (length < 10) {
            return 1;
        }
        data.readerIndex(0);
        if (data.readableBytes() < 10) {
            return 1;
        }
        int length = data.readableBytes();
        int position = length / 4;
        bytes1[0] = (byte) position;
        data.readerIndex(position);
        bytes1[1] = data.readByte();
        position = length * 3 / 4;
        bytes1[2] = (byte) position;
        data.readerIndex(position);
        bytes1[3] = data.readByte();
        return byteArrayToInt(bytes1);
    }


}
