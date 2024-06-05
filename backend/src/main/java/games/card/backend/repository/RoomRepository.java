package games.card.backend.repository;

import games.card.backend.model.RoomEntity;
import games.card.backend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByPlayersContainsAndActiveTrue(UserEntity user);
}
