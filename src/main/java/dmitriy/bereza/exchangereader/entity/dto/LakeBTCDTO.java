package dmitriy.bereza.exchangereader.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LakeBTCDTO extends StringBasedDTO {

    @JsonProperty("asks")
    private List<String[]> asks = new ArrayList<>();

    @JsonProperty("bids")
    private List<String[]> bids = new ArrayList<>();

    @JsonProperty()
    private String name;
}
