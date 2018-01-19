package dmitriy.bereza.exchangereader.entity.bom;

import dmitriy.bereza.exchangereader.entity.bom.sales.AbstractSale;
import dmitriy.bereza.exchangereader.entity.bom.sales.Ask;
import dmitriy.bereza.exchangereader.entity.bom.sales.Bid;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "CURRENCY_PAIR")
public class CurrencyPair {

    private static final String[] LONG_NAMES = new String[]{"FUEL", "DASH", "USDT", "GAME"};
    private static final String[] SEPARATORS = new String[]{"/", ":", "_"};
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM.dd.yyyy-HH:mm:ss");
    private static final int MERGED_PAIR_NAME_LENGTH = 6;
    private static final String OLD_BITCOIN_NAME = "XBT";
    private static final String NEW_BITCOIN_NAME = "BTC";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date datetime;
    private String name;
    private ExchangeName exchangeName;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CURRENCY_PAIR_ID")
    private List<Ask> asks = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CURRENCY_PAIR_ID")
    private List<Bid> bids = new ArrayList<>();
    /*
     * Transient fields are filled with call of calculate remains for asks/bids
     * */
    @Transient
    private BigDecimal asksRemains;
    @Transient
    private BigDecimal bidsRemains;
    @Transient
    private BigDecimal averageAsksPrice;
    @Transient
    private BigDecimal averageBidsPrice;
    @Transient
    private BigDecimal asksQuantity;
    @Transient
    private BigDecimal bidsQuantity;

    public CurrencyPair() {
        setDatetime(new Date());
    }

    public CurrencyPair(String name, ExchangeName exchangeName) {
        super();
        setName(convertName(name));
        setExchangeName(exchangeName);
    }

    public void addToAsks(Ask ask) {
        getAsks().add(ask);
    }

    public void addToBids(Bid bid) {
        getBids().add(bid);
    }

    @Override
    public String toString() {
        return getExchangeName() + ", " + SIMPLE_DATE_FORMAT.format(getDatetime()) + ", " + getName();
    }

    public String toStringWithoutDate() {
        return getExchangeName() + ", " + getName();
    }

    private String convertName(String name) {
        String result = name.toUpperCase();
        String complexName = null;
        if (!areSeparatorsExists(name) && result.length() >= MERGED_PAIR_NAME_LENGTH) {
            for (String longName : LONG_NAMES) {
                if (result.contains(longName)) {
                    complexName = longName;
                    break;
                }
            }
            result = complexName == null ? result.substring(0, 3) + "/" + result.substring(3, 6) : processLongNameForPair(result, complexName);
        } else {
            result = result.replace(":", "/").replace("_", "/");
        }
        return result.replace(OLD_BITCOIN_NAME, NEW_BITCOIN_NAME);
    }

    private boolean areSeparatorsExists(String name) {
        for (String separator : SEPARATORS) {
            if (name.contains(separator)) {
                return true;
            }
        }
        return false;
    }

    private String processLongNameForPair(String name, String longName) {
        return name.startsWith(longName) ? name.replace(longName, longName + "/") : name.replace(longName, "/" + longName);
    }

    private BigDecimal calculateAveragePrice(List<? extends AbstractSale> sales) {
        BigDecimal result = new BigDecimal(0);
        for (AbstractSale sale : sales) {
            result = result.add(new BigDecimal(sale.getPrice()));
        }
        return sales.isEmpty() ? new BigDecimal(0) : result.divide(new BigDecimal(sales.size()), RoundingMode.HALF_EVEN);
    }

    public void calculateRemains(double budget) {
        calculateAskRemains(budget);
        calculateBidsRemains(budget);
    }

    public void calculateAskRemains(double budget) {
        Pair<BigDecimal, BigDecimal> remainsAndQuantity = calculateRemainsAndQuantity(getAsks(), budget);
        if (remainsAndQuantity != null) {
            BigDecimal remains = remainsAndQuantity.getKey();
            BigDecimal quantity = remainsAndQuantity.getValue();
            setAsksRemains(remains);
            setAsksQuantity(quantity);
            setAverageAsksPrice(calculateAveragePrice(getAsks()));
        } else {
            log.warn(toString() + " pair has no asks set, skipping calculation.");
        }
    }

    public void calculateBidsRemains(double budget) {
        Pair<BigDecimal, BigDecimal> remainsAndQuantity = calculateRemainsAndQuantity(getBids(), budget);
        if (remainsAndQuantity != null) {
            BigDecimal remains = remainsAndQuantity.getKey();
            BigDecimal quantity = remainsAndQuantity.getValue();
            setBidsRemains(remains);
            setBidsQuantity(quantity);
            setAverageBidsPrice(calculateAveragePrice(getBids()));
        } else {
            log.warn(toString() + " pair has no bids set, skipping calculation.");
        }
    }

    private Pair<BigDecimal, BigDecimal> calculateRemainsAndQuantity(List<? extends AbstractSale> sales, double budget) {
        BigDecimal remains = new BigDecimal(budget);
        BigDecimal quantity = new BigDecimal(0);
        if (sales.isEmpty()) {
            return null;
        }
        for (AbstractSale sale : sales) {
            if (remains.subtract(sale.getValue()).doubleValue() <= 0) {
                quantity = quantity.add(remains.divide(new BigDecimal(sale.getPrice()), RoundingMode.HALF_EVEN));
                remains = new BigDecimal(0);
                break;
            } else {
                quantity = quantity.add(new BigDecimal(sale.getQuantity()));
                remains = remains.subtract(sale.getValue());
            }
        }
        return new Pair<>(remains, quantity);
    }
}
