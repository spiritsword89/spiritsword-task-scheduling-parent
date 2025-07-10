package com.spiritsword.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.spiritsword.task.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ByteToJsonMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();
        if(byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] byteBuffer = new byte[length];
        byteBuf.readBytes(byteBuffer);

        ChannelMessage channelMessage = JSON.parseObject(byteBuffer, ChannelMessage.class);

        MessageType messageType = channelMessage.getMessageType();

        if(messageType.equals(MessageType.TASK_REQUEST)) {
            JSONObject payload = (JSONObject) channelMessage.getPayload();
            TaskRequest taskRequest = payload.toJavaObject(TaskRequest.class);
            channelMessage.setPayload(taskRequest);
        }else if (messageType.equals(MessageType.TASK_RESPONSE)) {
            JSONObject payload = (JSONObject) channelMessage.getPayload();
            TaskResult taskResult = payload.toJavaObject(TaskResult.class);
            channelMessage.setPayload(taskResult);
        }else if (messageType.equals(MessageType.EXECUTOR_LIST)){
            JSONObject payload = (JSONObject) channelMessage.getPayload();
            List<ExecutorInfo> executorInfos = JSON.parseArray(payload.toJSONString(), ExecutorInfo.class);
            channelMessage.setPayload(executorInfos);
        }else {
            channelMessage.setPayload(null);
        }

        list.add(channelMessage);
    }
}
