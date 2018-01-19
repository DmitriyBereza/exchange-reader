package dmitriy.bereza.exchangereader.service.impl;

import dmitriy.bereza.exchangereader.dao.CurrencyPairDao;
import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.entity.bom.sales.Ask;
import dmitriy.bereza.exchangereader.entity.bom.sales.Bid;
import dmitriy.bereza.exchangereader.service.CSVWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

@Service
@Slf4j
public class CSVWriteServiceImpl implements CSVWriteService {

    private static final String HEADER_EXPORT = "Exchange, DateTime, CurrencyPair, Bid/Ask, Price, Quantity, Volume\n";
    private static final String HEADER_CALCULATED_EXPORT = "Exchange, CurrencyPair, Bid/Ask, Average Price, Quantity, Volume, Remains\n";

    @Autowired
    private CurrencyPairDao currencyPairDao;

    public void export(Iterable<CurrencyPair> all) {
        try (Writer writer = new FileWriter("out.csv")) {
            writer.write(HEADER_EXPORT);
            all.forEach(p -> {
                String pair = p.toString();
                try {
                    for (Ask ask : p.getAsks()) {
                        String result = pair + ask.toString() + "\n";
                        writer.write(result);
                    }
                    for (Bid bid : p.getBids()) {
                        String result = pair + bid.toString() + "\n";
                        writer.write(result);
                    }
                } catch (IOException e) {
                    log.error("Can't write data to file");
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            log.error("Can't write data to file");
            e.printStackTrace();
        }
    }

    public void calculatedExport(Iterable<CurrencyPair> all, Double budget) {
        try (Writer writer = new FileWriter("calculated_data.csv")) {
            writer.write(HEADER_CALCULATED_EXPORT);
            all.forEach(p -> {
                String pair = p.toStringWithoutDate();
                String name = p.getName();
                String[] split = name.split("/");
                Double actualBudget = budget;
                if (split.length == 2 && !"USD".equals(split[1])) {
                    CurrencyPair currencyPair = currencyPairDao.findCurrencyPairByNameAndExchangeName(split[1] + "/USD", p.getExchangeName());
                    if (currencyPair == null) {
                        log.warn(split[1] + "/USD" + " doesn't found on " + p.getExchangeName() + ". Skipping pair " + pair);
                        return;
                    } else {
                        currencyPair.calculateAskRemains(budget);
                        if (currencyPair.getAsksQuantity() == null) {
                            log.warn(currencyPair.toString() + " has no asks can't calculate remains for " + p.toString());
                            return;
                        }
                        actualBudget = currencyPair.getAsksQuantity().doubleValue();
                    }
                }
                p.calculateRemains(actualBudget);
                BigDecimal averageAsksPrice = p.getAverageAsksPrice();
                BigDecimal averageAsksQuantity = p.getAsksQuantity();
                String ask = "";
                if (averageAsksPrice != null) {
                    ask = pair + ",ask , " + averageAsksPrice + ", " + averageAsksQuantity + ", " + averageAsksPrice.multiply(averageAsksQuantity) + ", " + p.getAsksRemains() + "\n";
                }
                BigDecimal averageBidsPrice = p.getAverageBidsPrice();
                BigDecimal averageBidsQuantity = p.getBidsQuantity();
                String bid = "";
                if (averageBidsPrice != null) {
                    bid = pair + ",bid , " + averageBidsPrice + ", " + averageBidsQuantity + ", " + averageBidsPrice.multiply(averageBidsQuantity) + ", " + p.getBidsRemains() + "\n";
                }
                try {
                    writer.write(ask);
                    writer.write(bid);
                } catch (IOException e) {
                    log.error("Can't write data to file");
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            log.error("Can't write data to file");
            e.printStackTrace();
        }
    }
}
