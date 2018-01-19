package dmitriy.bereza.exchangereader.entity.bom.sales;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BIDS")
public class Bid extends AbstractSale {

    @Override
    public String toString() {
        return ", bid, " + super.toString();
    }
}
