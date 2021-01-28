package br.com.estudo;

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
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnvioKafkaApplication implements CommandLineRunner {

    @Value("${servidores}")
    private String servidores;
    
    private static String[] ALFA = {"A","B","C","D","E","F","G","H","I","J"};
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(EnvioKafkaApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        int count = 1;
        Random random = new Random();
        int randomWithNextInt = random.nextInt(10 - 0)  + 0;
        String servidor = ALFA[randomWithNextInt];
        while (true) {
            mensagem(count++, servidor);
        }
    }
    
    private void mensagem(int count, String servidor) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500);
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servidores);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String msg = "{ id: " + count + ", servidor: " + servidor + " }";
        
        try (KafkaProducer<String, String> producer = 
                new KafkaProducer<>(properties)) {
            ProducerRecord<String, String> record = 
                    new ProducerRecord<>("testejava", msg);
            logger.info(String.format("Enviando --> %s", msg));
            producer.send(record);
        }
    }

}