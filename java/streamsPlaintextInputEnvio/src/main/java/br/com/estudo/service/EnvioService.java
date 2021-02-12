package br.com.estudo.service;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EnvioService {

    private static final String TOPIC = "streams-plaintext-input";

    @Value("${servidores}")
    private String servidores;

    private static final String[] ALFA = {"Lorem", "ipsum", "dolor", "sit", "amet",
            "consectetur", "adipiscing", "elit", "Maecenas", "quis", "viverra",
            "dolor", "Nulla", "tincidunt", "feugiat", "risus", "et", "interdum"
    };

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void enviarMensagem() throws InterruptedException {
        int count = 1;
        while (count > 0) {
            enviarKafka();
            TimeUnit.MILLISECONDS.sleep(1500);
        }
    }

    private void enviarKafka() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servidores);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "wordcount-lambda-example-envio");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        int index = new Random().nextInt(18);
        String msg = ALFA[index];
        for(int x = 0; x <= 4; x++) {
            index = new Random().nextInt(18);
            msg = msg + " " + ALFA[index];
        }

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, msg);
            String msgInf = String.format("Send--> %s", msg);
            logger.info(msgInf);

            producer.send(record);
        }
    }

}
