package com.codingbingo.rocketmq.component;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.codingbingo.rocketmq.RocketMQProperties;
import com.codingbingo.rocketmq.constants.RocketMQContent;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Author bingo
 * Date 2018/8/27 14:16
 */
public abstract class AbstractRocketMQConsumer<Content extends RocketMQContent> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RocketMQProperties rocketMqProperties;

    private Class<Content> contentClass;
    /**
     * consumer holder.
     */
    private Consumer defaultConsumer;

    /**
     * 订阅方式
     *
     * @return
     */
    public String getMessageModel() {
        return PropertyValueConst.CLUSTERING;
    }

    /**
     * 订阅的topic和tag
     */
    public abstract Set<String> subscribeTags();

    /**
     * 消费者ID
     *
     * @return consumer id
     */
    public String getConsumerId(){
        return  getConsumerIdPrefix() + rocketMqProperties.getcMiddle() + getClass().getSimpleName();
    };

    /**
     * 消费消息
     *
     * @param content content
     * @param msg     msg
     * @return consume result
     */
    public abstract void consumeMsg(Content content, Message msg) throws Exception;

    @PostConstruct
    private void init() throws Exception {
        Class<? extends AbstractRocketMQConsumer> parentClazz = this.getClass();
        Type genType = parentClazz.getGenericSuperclass();
        Type[] types = ((ParameterizedType) genType).getActualTypeArguments();
        contentClass = (Class<Content>) types[0];

        initRocketMQPushConsumer();
    }

    private void initRocketMQPushConsumer() throws Exception {
        Assert.notNull(rocketMqProperties.getAccessKey(), "Access key should not be null");
        Assert.notNull(rocketMqProperties.getSecretKey(), "Secret key should not be null");
        Assert.notNull(rocketMqProperties.getONSAddr(), "Secret key should not be null");
        Assert.notNull(getConsumerId(), "Property 'consumerId' is required.");

        Set<String> tags = subscribeTags();
        Assert.notEmpty(tags, "Subscrible tags method can't return empty tag.");

        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(PropertyKeyConst.ConsumerId, getConsumerId());
        consumerProperties.setProperty(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        consumerProperties.setProperty(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        consumerProperties.setProperty(PropertyKeyConst.ONSAddr, rocketMqProperties.getONSAddr());
        consumerProperties.setProperty(PropertyKeyConst.MessageModel, getMessageModel());

        defaultConsumer = ONSFactory.createConsumer(consumerProperties);
        defaultConsumer.subscribe(rocketMqProperties.getTopic(), String.join("||", tags), new DefaultMessageListener());
    }

    public <T> T formatMsgContent(byte[] body, Class<? extends RocketMQContent> tClass) {
        T t = null;
        if (body != null) {
            t = JSON.parseObject(body, tClass);
        }
        return t;
    }

    public Consumer getDefaultConsumer() {
        return defaultConsumer;
    }

    public class DefaultMessageListener implements MessageListener {
        public Action consume(Message message, ConsumeContext consumeContext) {
            try {
                long startTime = System.currentTimeMillis();
                consumeMsg(formatMsgContent(message.getBody(), contentClass), message);
                long costTime = System.currentTimeMillis() - startTime;
                logger.info("consume: {}, msgId: {} cost: {} ms", getConsumerId(), message.getMsgID(), costTime);
            } catch (Exception e) {
                logger.error("consume message failed. messageExt: {}", message, e);
                return Action.ReconsumeLater;
            }
            return Action.CommitMessage;
        }
    }

    public void start() throws Exception {
        logger.info("AbstractRocketMQConsumer start!!");
        defaultConsumer.start();
    }

    protected String getConsumerIdPrefix(){
        return "GID_";
    }
}