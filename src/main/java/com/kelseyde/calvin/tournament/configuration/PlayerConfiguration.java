package com.kelseyde.calvin.tournament.configuration;

import com.kelseyde.calvin.tournament.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerConfiguration {

    @Value("${tournament.player-one.version}")
    private String playerOneVersion;

    @Value("${tournament.player-one.url}")
    private String playerOneUrl;

    @Value("${tournament.player-two.version}")
    private String playerTwoVersion;

    @Value("${tournament.player-two.url}")
    private String playerTwoUrl;

    public Player getPlayerOne() {
        return new Player(playerOneVersion, playerOneUrl);
    }

    public Player getPlayerTwo() {
        return new Player(playerTwoVersion, playerTwoUrl);
    }

}
