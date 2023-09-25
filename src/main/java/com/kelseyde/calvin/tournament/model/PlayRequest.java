package com.kelseyde.calvin.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayRequest {

    private String gameId;
    private String from;
    private String to;
    private String promotion;

    public String toMoveString() {
        String moveString = String.format("%s - %s", from, to);
        if (promotion != null) {
            moveString = moveString + " " + promotion;
        }
        return moveString;
    }

}
