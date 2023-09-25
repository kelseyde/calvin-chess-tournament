package com.kelseyde.calvin.tournament.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayResponse {

    GameResult result;
    MoveResponse move;

    @Data
    @Builder
    public static class MoveResponse {
        String from;
        String to;
        String promotion;
    }

    public PlayRequest toPlayRequest() {
        return PlayRequest.builder()
                .from(move.from)
                .to(move.to)
                .promotion(move.promotion)
                .build();
    }


}
