package br.com.ingressos.service;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.ingressos.model.Venda;
import br.com.ingressos.serializer.VendaSerializer;

@Service
public class IngressoService {

    private static final String TOPIC = "venda-ingressos";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Value("${servidores}")
    private String servidores;

    private static Random rand = new Random();
    private long operacao = 1;
    private static BigDecimal valorIngresso = BigDecimal.valueOf(35.90);

    public void enviarIngresso() throws InterruptedException, ExecutionException {

        try (KafkaProducer<String, Venda> producer = new KafkaProducer<>(properties())) {
            int count = 1;
            while (count > 0) {
                Venda venda = geraVenda();
                ProducerRecord<String, Venda> record = new ProducerRecord<>(TOPIC, venda);
                
                String msgInf = String.format("Send--> %s", venda);
                logger.info(msgInf);
                
                Callback callback = (data, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    logger.info("Mensagem enviada com sucesso para: {} partition: {} offset: {} | tempo: {}",
                            data.topic(), data.partition(), data.offset(), data.timestamp());
                };
                
                producer.send(record, callback).get();
                
                TimeUnit.MILLISECONDS.sleep(500);
            }
        }

    }
    
    private Properties properties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servidores);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VendaSerializer.class.getName());
        return properties;
    }

    private Venda geraVenda() {
        String[] status = {"COMPRADO", "CANCELADO", "VALIDAR_CARTAO", "VALIDAR_DEBITO"};
        UUID cliente = UUID.randomUUID();
        int qtdIngressos = rand.nextInt(10) + 1;
        return new Venda(operacao++, 
                cliente.toString(), 
                qtdIngressos, 
                valorIngresso.multiply(BigDecimal.valueOf(qtdIngressos)),
                status[new Random().nextInt(4)]);
    }

}
