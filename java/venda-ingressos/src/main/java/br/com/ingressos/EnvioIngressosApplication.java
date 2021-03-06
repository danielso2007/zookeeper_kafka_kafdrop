package br.com.ingressos;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnvioIngressosApplication {

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(EnvioIngressosApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }

}
