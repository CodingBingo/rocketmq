package com.codingbingo.rocketmq;

/**
 * Author bingo
 * Date 2018/8/26 23:21
 */
public class RocketMQProperties {
    private String producerId;

    private String accessKey;

    private String secretKey;

    private String ONSAddr;

    private String topic;

    private String cMiddle;

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getONSAddr() {
        return ONSAddr;
    }

    public void setONSAddr(String ONSAddr) {
        this.ONSAddr = ONSAddr;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getcMiddle() {
        return cMiddle;
    }

    public void setcMiddle(String cMiddle) {
        this.cMiddle = cMiddle;
    }
}
