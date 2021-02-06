package br.com.ingressos.service;

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

import br.com.ingressos.deserializer.VendaDeserializer;
import br.com.ingressos.model.Venda;

@Service
public class ProcessoIngressoService {

    private static final String GRUPO_PROCESSAMENTO = "grupo-processamento-ingressos";

    private static final String TOPIC = "venda-ingressos";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${servidores}")
    private String servidores;

    public void processar() throws InterruptedException {

        try (KafkaConsumer<String, Venda> consumer = new KafkaConsumer<>(properties())) {
            consumer.subscribe(Arrays.asList(TOPIC));

            int count = 1;

            while (count > 0) {

                ConsumerRecords<String, Venda> vendas = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, Venda> record : vendas) {

                    Venda venda = record.value();

                    if (new Random().nextBoolean()) {
                        venda.setStatus("APROVADA");
                    } else {
                        venda.setStatus("REPROVADA");
                    }

                    TimeUnit.MILLISECONDS.sleep(500);

                    String msgInfo = String.format("Receive--> %s", venda);
                    logger.info(msgInfo);
                }
            }
        }
    }
    
    private Properties properties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servidores);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "processamento-ingresso");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, VendaDeserializer.class.getName());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GRUPO_PROCESSAMENTO);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
        return properties;
    }

}
