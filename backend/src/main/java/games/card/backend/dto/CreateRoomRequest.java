package games.card.backend.dto;

import games.card.backend.games.GameType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomRequest {

    private GameType gameType;
    private Integer playerLimit;
}