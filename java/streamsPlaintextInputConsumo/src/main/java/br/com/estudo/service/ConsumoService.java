package br.com.estudo.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ConsumoService {

    private static final String GROUP_KAFKA = "wordcount-lambda-example-consume";
    static final String outputTopic = "streams-wordcount-output";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${servidores}")
    private String servidores;

    public void consumo() throws InterruptedException {
        int count = 1;
        while (count > 0) {
            consumoChamada();
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }

    private void consumoChamada() throws InterruptedException {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servidores);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "wordcount-lambda-example-consume");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_KAFKA);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {

            consumer.subscribe(Arrays.asList(outputTopic));

            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    logger.info(String.format("Value--> %s", record.value()));
                    logger.info(String.format("Headers--> %s", record.headers()));
                    logger.info(String.format("Key--> %s", record.key()));
                    logger.info(String.format("Offset--> %s", record.offset()));
                    logger.info(String.format("Topic--> %s", record.topic()));
                    logger.info(String.format("Timestamp--> %s", record.timestamp()));
                    logger.info("-------------------------------------------------------------");
                }
            }
        }
    }
}
