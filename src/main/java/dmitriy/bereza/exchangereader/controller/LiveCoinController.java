package dmitriy.bereza.exchangereader.controller;

import dmitriy.bereza.exchangereader.dao.CurrencyPairDao;
import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.service.FetchingService;
import dmitriy.bereza.exchangereader.service.impl.CSVWriteServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@RestController("/api")
@Slf4j
public class LiveCoinController {

    @Autowired
    private CurrencyPairDao currencyPairDao;
    @Autowired
    private Executor executor;
    @Autowired
    private FetchingService fetchingService;
    @Autowired
    private CSVWriteServiceImpl csvWriteService;

    @GetMapping(path = "/fillDb")
    public String fillDb() throws ExecutionException, InterruptedException {
        cleanDb();
        log.debug("There are " + currencyPairDao.findAll() + " rows in db");
        log.info("Database filling started");
        long start = System.currentTimeMillis();
        List<CurrencyPair> result = new ArrayList<>();
        CompletableFuture<List<CurrencyPair>> cexIo = fetchingService.readFromCexIo();
        CompletableFuture<List<CurrencyPair>> liveCoin = fetchingService.readFromLivecoin();
        CompletableFuture<List<CurrencyPair>> lakeBtc = fetchingService.readFromLakeBTC();
        CompletableFuture.allOf(liveCoin, cexIo, lakeBtc).join();
        result.addAll(liveCoin.get());
        result.addAll(cexIo.get());
        result.addAll(lakeBtc.get());
        log.info("Persisting to db");
        long startPersist = System.currentTimeMillis();
        currencyPairDao.save(result);
        long persistenceDuration = System.currentTimeMillis() - startPersist;
        log.info(result.size() + " objects persisted in " + persistenceDuration + " ms.");
        long duration = System.currentTimeMillis() - start;
        log.info("Database filling finished " + duration + " ms.");
        return "Processed and saved " + result.size() + " entities in " + duration + " ms.";
    }

    @GetMapping(path = "/show")
    public List<CurrencyPair> getAllRecords() {
        return (List<CurrencyPair>) currencyPairDao.findAll();
    }

    @GetMapping(path = "/showSize")
    public int getAllRecordsSize() {
        return ((List<CurrencyPair>) currencyPairDao.findAll()).size();
    }

    @GetMapping(path = "/print")
    public String printToFile() {
        long start = System.currentTimeMillis();
        Iterable<CurrencyPair> all = currencyPairDao.findAll();
        log.info("Start filling csv file");
        csvWriteService.export(all);
        log.info("CSV file successfully filled");
        long duration = System.currentTimeMillis() - start;
        return "CSV file filled in " + duration + " ms";

    }

    @GetMapping(path = "/calculate/{budget}")
    public String calculate(@PathVariable double budget) {
        long start = System.currentTimeMillis();
        Iterable<CurrencyPair> all = currencyPairDao.findAll();
        log.info("Start filling csv file");
        csvWriteService.calculatedExport(all, budget);
        log.info("CSV file successfully filled");
        long duration = System.currentTimeMillis() - start;
        return "CSV file filled in " + duration + " ms";
    }

    @GetMapping(path = "/clean")
    public void cleanDb() {
        currencyPairDao.deleteAll();
    }

}
