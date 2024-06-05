package games.card.backend.model;

import games.card.backend.games.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class RoomInfo {
    private Long id;
    private String owner;
    private GameType gameType;
    private Integer playerLimit;
    private Integer noPlayers;
    private List<String> players;

    public static List<RoomInfo> convert(List<RoomEntity> roomList){
        List<RoomInfo> list = new ArrayList<>();
        for(RoomEntity room: roomList){
            list.add(new RoomInfo(
                    room.getId(), room.getOwner().getUsername(),
                    room.getGameType(),room.getPlayerLimit(),
                    room.getPlayers().size(), getUsernames(room))
            );
        }
        return list;
    }

    private static List<String> getUsernames(RoomEntity room){
        List<String> list = new ArrayList<>();
        for(UserEntity player: room.getPlayers()){
            list.add(player.getUsername());
        }
        return list;
    }
}


