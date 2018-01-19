package dmitriy.bereza.exchangereader.service.impl;

import dmitriy.bereza.exchangereader.converter.CurrencyPairConverter;
import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.entity.bom.ExchangeName;
import dmitriy.bereza.exchangereader.entity.dto.LiveCoinDTO;
import dmitriy.bereza.exchangereader.service.AbstractFetcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LivecoinFetcher extends AbstractFetcher {

    @Value("${livecoin.url}")
    private String url;

    @Value("${livecoin.fetch.enabled}")
    private boolean isLivecoinFetchEnabled;

    public LivecoinFetcher(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    protected void readData(List<CurrencyPair> result) {
        ResponseEntity<Map<String, LiveCoinDTO>> exchange = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, LiveCoinDTO>>() {
                });
        for (Map.Entry<String, LiveCoinDTO> entry : exchange.getBody().entrySet()) {
            CurrencyPair pair = new CurrencyPair(entry.getKey(), ExchangeName.LIVECOIN);
            new CurrencyPairConverter().fromDTO(entry.getValue(), pair);
            result.add(pair);
        }
    }

    @Override
    protected String getExchangeName() {
        return ExchangeName.LIVECOIN.name();
    }

    @Override
    protected boolean isFetchEnabled() {
        return isLivecoinFetchEnabled;
    }
}
