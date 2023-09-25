package com.kelseyde.calvin.tournament.configuration;

import com.kelseyde.calvin.tournament.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayerConfiguration {

    private static final String URL_TEMPLATE = "http://localhost:%s/play";

    @Value("tournament.player-one.version")
    private String playerOneVersion;

    @Value("tournament.player-one.port")
    private int playerOnePort;

    @Value("tournament.player-two.version")
    private String playerTwoVersion;

    @Value("tournament.player-two.port")
    private int playerTwoPort;

    public Player getPlayerOne() {
        return new Player(playerOneVersion, playerOnePort);
    }

    public Player getPlayerTwo() {
        return new Player(playerTwoVersion, playerTwoPort);
    }

}
