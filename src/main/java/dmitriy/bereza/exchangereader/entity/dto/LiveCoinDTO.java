package dmitriy.bereza.exchangereader.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveCoinDTO extends StringBasedDTO {

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("asks")
    private List<String[]> asks = new ArrayList<>();

    @JsonProperty("bids")
    private List<String[]> bids = new ArrayList<>();

}
