package dmitriy.bereza.exchangereader.service.impl;

import dmitriy.bereza.exchangereader.converter.CurrencyPairConverter;
import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.entity.bom.ExchangeName;
import dmitriy.bereza.exchangereader.entity.dto.LakeBTCDTO;
import dmitriy.bereza.exchangereader.service.AbstractFetcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LakeBtcFetcher extends AbstractFetcher {

    @Value("${lakebtc.prefix}")
    private String url;

    @Value("${lakebtc.fetch.enabled}")
    private boolean isLakeBtcFetchEnabled;

    @Value("${lakebtc.request.delay}")
    private long delay;

    public LakeBtcFetcher(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    protected void readData(List<CurrencyPair> result) {
        List<LakeBTCDTO> dtos = new ArrayList<>();
        String[] pairs = new String[]{"btcusd", "btcsek", "btceur", "btchkd", "btcjpy", "btcgbp", "btcaud", "btccad", "btcsgd", "btckrw"};
        try {
            for (String pair : pairs) {
                LakeBTCDTO dto = restTemplate.getForObject(new URI(url + pair), LakeBTCDTO.class);
                dto.setName(pair);
                dtos.add(dto);
                Thread.sleep(delay);
            }
            for (LakeBTCDTO dto : dtos) {
                CurrencyPair pair = new CurrencyPair(dto.getName(), ExchangeName.LAKE_BTC);
                new CurrencyPairConverter().fromDTO(dto, pair);
                result.add(pair);
            }
        } catch (InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getExchangeName() {
        return ExchangeName.LAKE_BTC.name();
    }

    @Override
    protected boolean isFetchEnabled() {
        return isLakeBtcFetchEnabled;
    }
}
