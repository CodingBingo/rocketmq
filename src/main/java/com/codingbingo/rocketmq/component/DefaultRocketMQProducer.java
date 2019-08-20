package com.codingbingo.rocketmq.component;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.codingbingo.rocketmq.RocketMQProperties;
import com.codingbingo.rocketmq.constants.RocketMQContent;
import java.util.Properties;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Author bingo
 * Date 2018/9/28 17:31
 */
public class DefaultRocketMQProducer {

    @Autowired
    private RocketMQProperties rocketMqProperties;

    private Producer producer;

    private String topic;

    @PostConstruct
    public void init(){
        Assert.notNull(rocketMqProperties.getProducerId(), "Producer id should not be null");
        Assert.notNull(rocketMqProperties.getAccessKey(), "Access key should not be null");
        Assert.notNull(rocketMqProperties.getSecretKey(), "Secret key should not be null");
        Assert.notNull(rocketMqProperties.getTopic(), "Topic should not be null");

        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.ProducerId, rocketMqProperties.getProducerId());
        producerProperties.setProperty(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        producerProperties.setProperty(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        if (rocketMqProperties.getONSAddr() != null) {
            producerProperties.setProperty(PropertyKeyConst.ONSAddr, rocketMqProperties.getONSAddr());
        }

        producer = ONSFactory.createProducer(producerProperties);
        producer.start();
        topic = rocketMqProperties.getTopic();
    }

    public SendResult sendTagMsg(String tag, RocketMQContent rocketMQContent){
        Message message = new Message();

        message.setTopic(topic);
        message.setTag(tag);
        message.setBody(rocketMQContent.toString().getBytes());

        return producer.send(message);
    }

    /**
     * 发送单向的消息，可能会丢失
     * @param tag
     * @param rocketMQContent
     */
    public void sendOneWayTagMsg(String tag, RocketMQContent rocketMQContent) {
        Message message = new Message();

        message.setTopic(topic);
        message.setTag(tag);
        message.setBody(rocketMQContent.toString().getBytes());

        producer.sendOneway(message);
    }
}
