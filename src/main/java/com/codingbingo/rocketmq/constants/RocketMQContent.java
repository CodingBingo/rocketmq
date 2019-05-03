package com.codingbingo.rocketmq.constants;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.io.Serializable;

/**
 * Auther bingo
 * Date 2018/8/27 14:39
 */
public class RocketMQContent implements Serializable {
    @Override
    public String toString() {
        return JSON.toJSONString(this, SerializerFeature.NotWriteDefaultValue);
    }
}
