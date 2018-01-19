package dmitriy.bereza.exchangereader.service;

import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FetchingService {

    CompletableFuture<List<CurrencyPair>> readFromLivecoin();

    CompletableFuture<List<CurrencyPair>> readFromCexIo();

    CompletableFuture<List<CurrencyPair>> readFromLakeBTC();

}
