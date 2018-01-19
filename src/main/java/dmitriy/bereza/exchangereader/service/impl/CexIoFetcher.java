package dmitriy.bereza.exchangereader.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dmitriy.bereza.exchangereader.converter.CurrencyPairConverter;
import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.entity.bom.ExchangeName;
import dmitriy.bereza.exchangereader.service.AbstractFetcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CexIoFetcher extends AbstractFetcher {

    @Value("${cex.io.prefix}")
    private String url;

    @Value("${cex.io.fetch.enabled}")
    private boolean isCexIoFetchEnabled;

    public CexIoFetcher(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    protected void readData(List<CurrencyPair> result) {
        List<JsonNode> dtos = new ArrayList<>();
        String[] pairs = new String[]{"BTC/USD", "ETH/USD", "BCH/USD", "BTG/USD", "DASH/USD", "XRP/USD", "ZEC/USD", "BTC/EUR", "ETH/EUR", "BCH/EUR",
                "BTG/EUR", "DASH/EUR", "XRP/EUR", "ZEC/EUR", "BTC/GBP", "ETH/GBP", "BCH/GBP", "DASH/GBP", "ZEC/GBP", "BTC/RUB", "ETH/BTC", "BCH/BTC",
                "BTG/BTC", "DASH/BTC", "XRP/BTC", "ZEC/BTC", "GHS/BTC"};
        for (String pair : pairs) {
            try {
                ClientHttpRequest request = restTemplate.getRequestFactory().createRequest(new URI(url + pair + "/?depth=20"), HttpMethod.GET);
                ClientHttpResponse response = request.execute();
                String s = IOUtils.toString(response.getBody());
                ObjectMapper mapper = new ObjectMapper();
                JsonFactory factory = mapper.getFactory();
                JsonParser parser = factory.createParser(s);
                JsonNode actualObj = mapper.readTree(parser);
                dtos.add(actualObj);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        for (JsonNode dto : dtos) {
            CurrencyPair pair = new CurrencyPair(dto.get("pair").asText(), ExchangeName.CEX_IO);
            new CurrencyPairConverter().fromDTO(dto, pair);
            result.add(pair);
        }
    }

    @Override
    protected String getExchangeName() {
        return ExchangeName.CEX_IO.name();
    }

    @Override
    protected boolean isFetchEnabled() {
        return isCexIoFetchEnabled;
    }
}
