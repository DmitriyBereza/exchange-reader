package dmitriy.bereza.exchangereader.service;

import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.interceptors.UserAgentInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractFetcher {

    protected final RestTemplate restTemplate;

    @Value("${retry.count}")
    private int retryCount;

    public AbstractFetcher(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        restTemplate.setInterceptors(Collections.singletonList(new UserAgentInterceptor()));
    }

    public CompletableFuture<List<CurrencyPair>> fetch() {
        List<CurrencyPair> result = new ArrayList<>();
        if (isFetchEnabled()) {
            for (int i = 1; i <= retryCount; i++) {
                long start = System.currentTimeMillis();
                log.info(getExchangeName() + " fetching started in thread with id " + Thread.currentThread().getId());
                try {
                    readData(result);
                } catch (Exception e) {
                    log.error("Can't read data from " + getExchangeName() + ". Reason:", e);
                    if (i == retryCount) {
                        log.error("Retry amount reached, stops fetching");
                    } else {
                        log.warn("Retrying, retry number: " + i);
                        continue;
                    }
                }
                long duration = System.currentTimeMillis() - start;
                log.info(getExchangeName() + " fetch finished " + duration + " ms");
                break;
            }
        } else {
            log.warn(getExchangeName() + " fetch was disabled. To enable it set fetch.enabled property to 'true'");
        }
        return CompletableFuture.completedFuture(result);
    }

    protected abstract void readData(List<CurrencyPair> result);

    protected abstract String getExchangeName();

    protected abstract boolean isFetchEnabled();
}
