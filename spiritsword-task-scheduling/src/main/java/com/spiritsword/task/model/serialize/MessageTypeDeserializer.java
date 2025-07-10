package com.spiritsword.task.model.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.spiritsword.task.model.MessageType;

import java.lang.reflect.Type;

public class MessageTypeDeserializer implements ObjectDeserializer {
    @Override
    public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
        String name = defaultJSONParser.parseObject(String.class);
        return (T) MessageType.valueOf(name);
    }
}
