package Dominio;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Value;

import java.util.Objects;

@Data
@Builder
public class Producer {
    private Integer id;
    private String name;

}
