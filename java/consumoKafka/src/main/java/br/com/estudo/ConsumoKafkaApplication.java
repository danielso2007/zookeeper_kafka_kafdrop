package br.com.estudo;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumoKafkaApplication {

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(ConsumoKafkaApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }

}
