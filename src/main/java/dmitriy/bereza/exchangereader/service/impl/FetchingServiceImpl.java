package dmitriy.bereza.exchangereader.service.impl;

import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.service.FetchingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FetchingServiceImpl implements FetchingService {

    @Autowired
    private LivecoinFetcher livecoinFetcher;

    @Autowired
    private CexIoFetcher cexIoFetcher;

    @Autowired
    private LakeBtcFetcher lakeBtcFetcher;

    @Async
    public CompletableFuture<List<CurrencyPair>> readFromLivecoin() {
        return livecoinFetcher.fetch();
    }

    @Async
    public CompletableFuture<List<CurrencyPair>> readFromCexIo() {
        return cexIoFetcher.fetch();
    }

    @Async
    public CompletableFuture<List<CurrencyPair>> readFromLakeBTC() {
        return lakeBtcFetcher.fetch();
    }
}
