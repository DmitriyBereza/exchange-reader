package dmitriy.bereza.exchangereader.entity.bom.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
public abstract class AbstractSale {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String price;

    private String quantity;

    private BigDecimal value;

    @Override
    public String toString() {
        return new BigDecimal(getPrice()).toPlainString() + ", " + new BigDecimal(getQuantity()).toPlainString() + ", " + getValue();
    }

    public void calculateValue() {
        setValue(new BigDecimal(price).multiply(new BigDecimal(quantity)));
    }
}
