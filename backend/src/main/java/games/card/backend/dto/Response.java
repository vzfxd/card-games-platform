package games.card.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class Response<T> {
    private HttpStatus status;
    private T info;
}
