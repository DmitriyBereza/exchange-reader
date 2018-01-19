package dmitriy.bereza.exchangereader.entity.bom.sales;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ASKS")
public class Ask extends AbstractSale {

    @Override
    public String toString() {
        return ", ask, " + super.toString();
    }
}
