package com.kelseyde.calvin.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class NewGameResponse {

    private String gameId;

    private MoveResponse move;

}
