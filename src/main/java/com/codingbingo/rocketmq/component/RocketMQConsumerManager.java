package com.codingbingo.rocketmq.component;

import java.util.List;

/**
 * Author bingo
 * Date 2018/8/27 15:40
 */
public class RocketMQConsumerManager {
    private List<AbstractRocketMQConsumer> consumers;

    public List<AbstractRocketMQConsumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<AbstractRocketMQConsumer> consumers) {
        this.consumers = consumers;
    }
}
