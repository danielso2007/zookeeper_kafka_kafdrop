package br.com.estudo.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import br.com.estudo.service.EnvioService;

@Component
public class StartupBean implements ApplicationListener<ApplicationReadyEvent>, ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EnvioService service;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("ApplicationRunner#run()");
        service.enviarMensagem();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("ApplicationListener#onApplicationEvent(ApplicationReadyEvent)");
    }

}
