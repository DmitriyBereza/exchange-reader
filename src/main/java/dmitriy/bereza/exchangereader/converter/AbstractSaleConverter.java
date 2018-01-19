package dmitriy.bereza.exchangereader.converter;

import com.fasterxml.jackson.databind.JsonNode;
import dmitriy.bereza.exchangereader.entity.bom.sales.AbstractSale;

public class AbstractSaleConverter {

    public void fromStringArray(String[] source, AbstractSale destination) {
        destination.setPrice(source[0]);
        destination.setQuantity(source[1]);
        destination.calculateValue();
    }

    public void fromJsonNODE(JsonNode source, AbstractSale destination) {
        destination.setPrice(source.get(0).asText());
        destination.setQuantity(source.get(1).asText());
        destination.calculateValue();
    }
}
