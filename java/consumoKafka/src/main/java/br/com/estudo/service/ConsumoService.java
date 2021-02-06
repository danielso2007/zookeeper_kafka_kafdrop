package br.com.estudo.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConsumoService {

    private static final String GROUP_KAFKA = "group_java_test";
    private static final String TOPIC = "teste_java";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String[] ALFA = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "X", "Y", "W", "Z" };
    
    @Value("${servidores}")
    private String servidores;

    public void consumo() throws InterruptedException {
        int count = 1;
        while (count > 0) {
            consumoChamada();
        }
    }

    private void consumoChamada() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500);
        
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servidores);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "Consumidor-" + ALFA[new Random().nextInt(25)]);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_KAFKA);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {

            consumer.subscribe(Arrays.asList(TOPIC));

            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for (ConsumerRecord<String, String> record : records) {
                    String msgInfo = String.format("Receive--> %s", record.value());
                    logger.info(msgInfo);
                }
            }
        }
    }
}
