package com.codingbingo.rocketmq;

import com.codingbingo.rocketmq.component.AbstractRocketMQConsumer;
import com.codingbingo.rocketmq.component.DefaultRocketMQProducer;
import com.codingbingo.rocketmq.component.RocketMQConsumerManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Author bingo
 * Date 2018/8/27 00:21
 */
@Configuration
@ComponentScan("com.codingbingo.rocketmq")
public class RocketMQAutoConfiguration {
    static final String ROCKETMQ_PREFIX = "spring.rocketmq";
    final static Logger logger = LoggerFactory.getLogger(RocketMQAutoConfiguration.class);

    private RocketMQConsumerManager rocketMQConsumerManager;

    @Bean("defaultRocketMQProperties")
    @ConfigurationProperties(prefix = ROCKETMQ_PREFIX)
    @ConditionalOnProperty(prefix = ROCKETMQ_PREFIX, name = {"producerId", "accessKey", "secretKey", "ONSAddr", "topic"})
    public RocketMQProperties defaultRocketMQProperties() {
        return new RocketMQProperties();
    }

    @Bean
    @ConditionalOnProperty(name = "spring.rocketmq.defaultProducerEnable", havingValue = "true")
    @ConditionalOnBean(name = {"defaultRocketMQProperties"})
    public DefaultRocketMQProducer defaultRocketMQProducer(@Qualifier("defaultRocketMQProperties") RocketMQProperties defaultRocketMQProperties){
        return new DefaultRocketMQProducer();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(value = AbstractRocketMQConsumer.class)
    public RocketMQConsumerManager rocketMQConsumerManager(List<AbstractRocketMQConsumer> rocketMQConsumerList){
        rocketMQConsumerManager = new RocketMQConsumerManager();
        rocketMQConsumerManager.setConsumers(rocketMQConsumerList);
        return rocketMQConsumerManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startUpConsumer(){
        if (rocketMQConsumerManager != null) {
            List<AbstractRocketMQConsumer> rocketMQConsumerList = rocketMQConsumerManager.getConsumers();
            if (rocketMQConsumerList != null) {
                for (AbstractRocketMQConsumer abstractRocketMQConsumer : rocketMQConsumerList) {
                    try {
                        abstractRocketMQConsumer.start();
                    } catch (Exception e) {
                        logger.error("Consumer start failed.", e);
                    }
                }
            }
        } else {
            logger.info("RocketMQConsumerManager is null, no consumer!");
        }
    }
}
