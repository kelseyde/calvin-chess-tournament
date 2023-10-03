package com.kelseyde.calvin.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayResponse {

    String gameId;
    GameResult result;
    MoveResponse move;


}
