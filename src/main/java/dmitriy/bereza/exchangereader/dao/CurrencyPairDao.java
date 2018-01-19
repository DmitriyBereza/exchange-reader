package dmitriy.bereza.exchangereader.dao;

import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.entity.bom.ExchangeName;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyPairDao extends CrudRepository<CurrencyPair, Long> {

    CurrencyPair findCurrencyPairByNameAndExchangeName(String name, ExchangeName exchangeName);
}
