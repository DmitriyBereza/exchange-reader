package dmitriy.bereza.exchangereader.entity.dto.sales;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractSalesDTO {

    @JsonProperty("price")
    private String price;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("remaining_volume")
    private String remaining_volume;

    @JsonProperty("Volume")
    private String volume;

    @JsonProperty("Price")
    private String UpperPrice;
}
