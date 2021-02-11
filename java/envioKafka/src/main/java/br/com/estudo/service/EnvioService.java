package br.com.estudo.service;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    private static final String TOPIC = "teste_java";

    @Value("${servidores}")
    private String servidores;

    private static final String[] ALFA = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "X", "Y", "W", "Z" };

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void enviarMensagem() throws InterruptedException {
        int count = 1;
        int server = new Random().nextInt(25);
        while (count > 0) {
            enviarKafka(count++, ALFA[server]);
        }
    }

    private void enviarKafka(int count, String servidor) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(200);

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servidores);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "servidor-" + servidor);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String msg = "{ id: " + count + ", servidor: " + servidor + " }";

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, msg);
            String msgInf = String.format("Send--> %s", msg);
            logger.info(msgInf);

            producer.send(record);
        }
    }

}
