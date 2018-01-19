package dmitriy.bereza.exchangereader.service;

import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;

public interface CSVWriteService {

    void export(Iterable<CurrencyPair> all);

    void calculatedExport(Iterable<CurrencyPair> all, Double budget);

}
