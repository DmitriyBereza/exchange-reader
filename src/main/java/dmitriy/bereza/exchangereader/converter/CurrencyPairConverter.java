package dmitriy.bereza.exchangereader.converter;

import com.fasterxml.jackson.databind.JsonNode;
import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.entity.bom.sales.Ask;
import dmitriy.bereza.exchangereader.entity.bom.sales.Bid;
import dmitriy.bereza.exchangereader.entity.dto.StringBasedDTO;

import java.util.Iterator;

public class CurrencyPairConverter {

    public void fromDTO(StringBasedDTO source, CurrencyPair destination) {
        AbstractSaleConverter converter = new AbstractSaleConverter();
        for (String[] strings : source.getAsks()) {
            Ask ask = new Ask();
            converter.fromStringArray(strings, ask);
            destination.addToAsks(ask);
        }
        for (String[] strings : source.getBids()) {
            Bid bid = new Bid();
            converter.fromStringArray(strings, bid);
            destination.addToBids(bid);
        }
    }

    public void fromDTO(JsonNode source, CurrencyPair destination) {
        AbstractSaleConverter converter = new AbstractSaleConverter();
        Iterator<JsonNode> asks = source.get("asks").elements();
        while (asks.hasNext()) {
            Ask ask = new Ask();
            JsonNode next = asks.next();
            converter.fromJsonNODE(next, ask);
            destination.addToAsks(ask);
        }
        Iterator<JsonNode> bids = source.get("bids").elements();
        while (bids.hasNext()) {
            Bid bid = new Bid();
            JsonNode next = bids.next();
            converter.fromJsonNODE(next, bid);
            destination.addToBids(bid);
        }
    }

}
