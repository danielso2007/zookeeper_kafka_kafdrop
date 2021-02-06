package br.com.ingressos.deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ingressos.model.Venda;

public class VendaDeserializer implements Deserializer<Venda> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Venda deserialize(String topic, byte[] venda) {
        try {
            return new ObjectMapper().readValue(venda, Venda.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

}
