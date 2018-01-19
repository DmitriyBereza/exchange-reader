package dmitriy.bereza.exchangereader.entity.dto;

import java.util.List;

public abstract class StringBasedDTO {

    public abstract List<String[]> getAsks();

    public abstract List<String[]> getBids();
}
