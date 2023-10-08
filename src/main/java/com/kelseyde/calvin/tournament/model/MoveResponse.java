package com.kelseyde.calvin.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoveResponse {
    String from;
    String to;
    String promotion;

    public PlayRequest toPlayRequest(String gameId, int thinkTime) {
        return PlayRequest.builder()
                .gameId(gameId)
                .from(from)
                .to(to)
                .promotion(promotion)
                .thinkTimeMs(thinkTime)
                .build();
    }

}
