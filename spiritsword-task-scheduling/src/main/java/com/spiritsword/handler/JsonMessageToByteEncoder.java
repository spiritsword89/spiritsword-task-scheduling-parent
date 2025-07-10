package com.spiritsword.handler;

import com.alibaba.fastjson.JSON;
import com.spiritsword.task.model.ChannelMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JsonMessageToByteEncoder extends MessageToByteEncoder<ChannelMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ChannelMessage channelMessage, ByteBuf byteBuf) throws Exception {
        byte[] jsonBytes = JSON.toJSONBytes(channelMessage);
        byteBuf.writeInt(jsonBytes.length);
        byteBuf.writeBytes(jsonBytes);
    }
}
