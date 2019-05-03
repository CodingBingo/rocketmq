### Aliyun RocketMQ third-party tools
---
最近接入阿里云的rokcetMQ的时候，发现阿里云的接入确实有点麻烦，于是就花了一点时间封装了一套对应的工具。

## How to use ?
  - 引入对应的jar包
     ```xml
     <dependency>
        <groupId>com.codingbingo</groupId>
        <artifactId>rocketmq</artifactId>
        <version>${TARGET_VERSION}</version>
    </dependency>
    ``` 
  - 配置文件添加对应配置
    ```yaml
      spring:
        rocketmq:
          topic:        # 选择订阅的目标topic，阿里云上可以配置
          producerId:   # 生产者ID
          accessKey:    # 
          secretKey:    #
          ONSAddr:      # 
          cMiddle: ""   # 注册时，添加的中间标识。最终的消费者名称如：GID_${cMiddle}_${ConsumerClassName}
          defaultProducerEnable:  # 如果当前系统需要充当生产者，设置为true即可，默认false
    ```