package br.com.ingressos.serializer;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ingressos.model.Venda;

public class VendaSerializer implements Serializer<Venda> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public byte[] serialize(String topic, Venda venda) {
        try {
            return new ObjectMapper().writeValueAsBytes(venda);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

}
