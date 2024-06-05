package games.card.backend.model;

import games.card.backend.games.GameType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name =  "rooms")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private GameType gameType;
    @ManyToOne
    private UserEntity owner;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserEntity> players;
    private Integer playerLimit;
    private String winner;
    private boolean active;

}
