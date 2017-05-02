package com.yang.eric.a17010.utils;

import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientSendingDelegate;
import com.vilyever.socketclient.helper.SocketPacket;
import com.vilyever.socketclient.helper.SocketPacketHelper;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.vilyever.socketclient.util.CharsetUtil;


/**
 * Created by Yang on 2017/4/14.
 */

public class TcpClient {

    private SocketClient socketClient;

    final TcpClient self = this;

    public void connect() {
        self.getSocketClient().connect();
    }
    public void disconnect() {
        self.getSocketClient().disconnect();
    }
    public SocketClient getSocketClient() {

        if (this.socketClient == null) {
            this.socketClient = new SocketClient();

            socketClient.getAddress().setRemoteIP(Constants.SERVER_IP); // 远程端IP地址
            socketClient.getAddress().setRemotePort(Constants.SERVER_PORT); // 远程端端口号
            socketClient.getAddress().setConnectionTimeout(15 * 1000); // 连接超时时长，单位毫秒
            socketClient.setCharsetName(CharsetUtil.UTF_8);// 设置编码为UTF-8

            /**
             * 设置自动发送的心跳包信息
             */
            socketClient.getHeartBeatHelper().setDefaultSendData(CharsetUtil.stringToData("HeartBeat", CharsetUtil.UTF_8));

            /**
             * 设置远程端发送到本地的心跳包信息内容，用于判断接收到的数据包是否是心跳包
             * 通过{@link SocketResponsePacket#isHeartBeat()} 查看数据包是否是心跳包
             */
            socketClient.getHeartBeatHelper().setDefaultReceiveData(CharsetUtil.stringToData("HeartBeat", CharsetUtil.UTF_8));
            socketClient.getHeartBeatHelper().setHeartBeatInterval(20 * 1000); // 设置自动发送心跳包的间隔时长，单位毫秒
            socketClient.getHeartBeatHelper().setSendHeartBeatEnabled(true); // 设置允许自动发送心跳包，此值默认为false

            /**
             * 设置包长度转换器
             * 即每次发送数据时，将包头以外的数据长度转换为特定的byte[]发送个远程端用于解析还需要读取多少长度的数据
             *
             * 例：socketClient.sendData(new byte[]{0x01, 0x02})的步骤为
             * 1. socketClient向远程端发送包头（如果设置了包头信息）
             * 2. socketClient要发送的数据为{0x01, 0x02}，长度为2（若设置了包尾，还需加上包尾的字节长度），通过此转换器将int类型的2转换为4字节的byte[]，远程端也照此算法将4字节的byte[]转换为int值
             * 3. socketClient向远程端发送转换后的长度信息byte[]
             * 4. socketClient向远程端发送正文数据{0x01, 0x02}
             * 5. socketClient向远程端发送包尾（如果设置了包尾信息）
             *
             * 此转换器用于第二步
             *
             * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}必须设置此项
             * 用于分隔多条消息
             */
            socketClient.getSocketPacketHelper().setSendPacketLengthDataConvertor(new SocketPacketHelper.SendPacketLengthDataConvertor() {
                @Override
                public byte[] obtainSendPacketLengthDataForPacketLength(SocketPacketHelper helper, int packetLength) {
                    /**
                     * 简单将int转换为byte[]
                     */
                    packetLength -= 2;
                    byte[] data = new byte[2];
                    data[0] = (byte) ((packetLength >> 8) & 0xFF);
                    data[1] = (byte) (packetLength & 0xFF);
                    return data;
                }
            });
            /**
             * 根据连接双方协议设置自动发送的包头数据
             * 每次发送数据包（包括心跳包）都会在发送包内容前自动发送此包头
             *
             * 若无需包头可删除此行
             */
//            socketClient.getSocketPacketHelper().setSendHeaderData(CharsetUtil.stringToData(Constants.HEAD, CharsetUtil.UTF_8));
            socketClient.getSocketPacketHelper().setSendHeaderData(Constants.HEAD_BYTES);

            /**
             * 根据连接双方协议设置自动发送的包尾数据
             * 每次发送数据包（包括心跳包）都会在发送包内容后自动发送此包尾
             *
             * 若无需包尾可删除此行
             * 注意：
             * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}时不依赖包尾读取数据
             */
//            socketClient.getSocketPacketHelper().setSendTrailerData(CharsetUtil.stringToData(Constants.TAIL, CharsetUtil.UTF_8));
            socketClient.getSocketPacketHelper().setSendTrailerData(Constants.TAIL_BYTES);

            /**
             * 设置分段发送数据长度
             * 即在发送指定长度后通过 {@link SocketClientSendingDelegate#onSendingPacketInProgress(SocketClient, SocketPacket, float, int)}回调当前发送进度
             * 注意：回调过于频繁可能导致设置UI过于频繁从而导致主线程卡顿
             *
             * 若无需进度回调可删除此二行，删除后仍有【发送开始】【发送结束】的回调
             */
//            socketClient.getSocketPacketHelper().setSendSegmentLength(8); // 设置发送分段长度，单位byte
//            socketClient.getSocketPacketHelper().setSendSegmentEnabled(true); // 设置允许使用分段发送，此值默认为false

            /**
             * 设置发送超时时长
             * 在发送每个数据包时，发送每段数据的最长时间，超过后自动断开socket连接
             * 通过设置分段发送{@link SocketPacketHelper#setSendSegmentEnabled(boolean)} 可避免发送大数据包时因超时断开，
             *
             * 若无需限制发送时长可删除此二行
             */
            socketClient.getSocketPacketHelper().setSendTimeout(30 * 1000); // 设置发送超时时长，单位毫秒
            socketClient.getSocketPacketHelper().setSendTimeoutEnabled(true); // 设置允许使用发送超时时长，此值默认为false

            /**
             * 设置读取策略为自动读取指定长度
             */
            socketClient.getSocketPacketHelper().setReadStrategy(SocketPacketHelper.ReadStrategy.AutoReadByLength);
            /**
             * 设置包长度转换器
             * 即每次接收数据时，将远程端发送到本地的长度信息byte[]转换为int，然后读取相应长度的值
             *
             * 例：自动接收远程端所发送的socketClient.sendData(new byte[]{0x01, 0x02})【{0x01, 0x02}为将要接收的数据】的步骤为
             * 1. socketClient接收包头（如果设置了包头信息）（接收方式为一直读取到与包头相同的byte[],即可能过滤掉包头前的多余信息）
             * 2. socketClient接收长度为{@link SocketPacketHelper#getReceivePacketLengthDataLength()}（此处设置为4）的byte[]，通过下面设置的转换器，将byte[]转换为int值，此int值暂时称为X
             * 3. socketClient接收长度为X的byte[]
             * 4. socketClient接收包尾（如果设置了包尾信息）（接收方式为一直读取到与包尾相同的byte[],如无意外情况，此处不会读取到多余的信息）
             * 5. socketClient回调数据包
             *
             * 此转换器用于第二步
             *
             * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}必须设置此项
             * 用于分隔多条消息
             */
            socketClient.getSocketPacketHelper().setReceivePacketLengthDataLength(2);
            socketClient.getSocketPacketHelper().setReceivePacketDataLengthConvertor(new SocketPacketHelper.ReceivePacketDataLengthConvertor() {
                @Override
                public int obtainReceivePacketDataLength(SocketPacketHelper helper, byte[] packetLengthData) {
                    /**
                     * 简单将byte[]转换为int
                     */
                    int length =  ((packetLengthData[0] & 0xFF) << 8) + (packetLengthData[1] & 0xFF) + 2 ;

                    return length;
                }
            });


            /**
             * 根据连接双方协议设置的包头数据
             * 每次接收数据包（包括心跳包）都会先接收此包头
             *
             * 若无需包头可删除此行
             */
//            socketClient.getSocketPacketHelper().setReceiveHeaderData(CharsetUtil.stringToData(Constants.HEAD, CharsetUtil.UTF_8));
            socketClient.getSocketPacketHelper().setReceiveHeaderData(Constants.HEAD_BYTES);

            /**
             * 根据连接双方协议设置的包尾数据
             *
             * 若无需包尾可删除此行
             * 注意：
             * 使用{@link com.vilyever.socketclient.helper.SocketPacketHelper.ReadStrategy.AutoReadByLength}时不依赖包尾读取数据
             */
//            socketClient.getSocketPacketHelper().setReceiveTrailerData(CharsetUtil.stringToData(Constants.TAIL, CharsetUtil.UTF_8));
            socketClient.getSocketPacketHelper().setReceiveTrailerData(Constants.TAIL_BYTES);

            /**
             * 设置接收超时时长
             * 在指定时长内没有数据到达本地自动断开
             *
             * 若无需限制接收时长可删除此二行
             */
//            socketClient.getSocketPacketHelper().setReceiveTimeout(120 * 1000); // 设置接收超时时长，单位毫秒
//            socketClient.getSocketPacketHelper().setReceiveTimeoutEnabled(true); // 设置允许使用接收超时时长，此值默认为false
        }
        return this.socketClient;
    }
}
