package dmitriy.bereza.exchangereader;

import dmitriy.bereza.exchangereader.entity.bom.CurrencyPair;
import dmitriy.bereza.exchangereader.entity.bom.sales.Ask;
import dmitriy.bereza.exchangereader.entity.bom.sales.Bid;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CurrencyPairTest {

    private CurrencyPair pair;

    @Before
    public void setUp() {
        pair = new CurrencyPair();
        Ask ask = new Ask();
        ask.setPrice("100");
        ask.setQuantity("9");
        ask.calculateValue();
        Ask secondAsk = new Ask();
        secondAsk.setPrice("30");
        secondAsk.setQuantity("31");
        secondAsk.calculateValue();
        Bid bid = new Bid();
        bid.setPrice("200");
        bid.setQuantity("3");
        bid.calculateValue();
        Bid bid2 = new Bid();
        bid2.setPrice("200");
        bid2.setQuantity("2");
        bid2.calculateValue();
        pair.addToAsks(ask);
        pair.addToAsks(secondAsk);
        pair.addToBids(bid);
        pair.addToBids(bid2);
    }

    @Test
    public void shouldTestRemainsWithEnoughBundget() {
        pair.calculateRemains(3000);
        assertEquals(new BigDecimal(1170), pair.getAsksRemains());
        assertEquals(new BigDecimal(40), pair.getAsksQuantity());
        assertEquals(new BigDecimal(65), pair.getAverageAsksPrice());
        assertEquals(new BigDecimal(2000), pair.getBidsRemains());
        assertEquals(new BigDecimal(5), pair.getBidsQuantity());
        assertEquals(new BigDecimal(200), pair.getAverageBidsPrice());
    }

    @Test
    public void shouldTestRemainsWithNotEnoughBudget() {
        pair.calculateRemains(1000);
        assertEquals(new BigDecimal(0), pair.getAsksRemains());
        assertEquals(new BigDecimal(12), pair.getAsksQuantity());
        assertEquals(new BigDecimal(65), pair.getAverageAsksPrice());
        assertEquals(new BigDecimal(0), pair.getBidsRemains());
        assertEquals(new BigDecimal(5), pair.getBidsQuantity());
        assertEquals(new BigDecimal(200), pair.getAverageBidsPrice());
    }
}
