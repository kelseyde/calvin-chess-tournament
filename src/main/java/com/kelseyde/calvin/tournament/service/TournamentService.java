package com.kelseyde.calvin.tournament.service;

import com.kelseyde.calvin.tournament.configuration.PlayerConfiguration;
import com.kelseyde.calvin.tournament.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentService {

    private static final int GAME_LIMIT = 100;
    private static final int MOVE_LIMIT = 100;

    private final PlayerConfiguration playerConfiguration;

    private final RestTemplate restTemplate;

    private final List<PlayRequest> STARTING_MOVES = List.of(
            PlayRequest.builder().from("e2").to("e4").build(),
            PlayRequest.builder().from("d2").to("d4").build(),
            PlayRequest.builder().from("c2").to("c4").build(),
            PlayRequest.builder().from("g1").to("f3").build()
    );

    public void run() {

        Player player1 = playerConfiguration.getPlayerOne();
        Player player2 = playerConfiguration.getPlayerTwo();
        List<Player> players = List.of(player1, player2);

        log.info("Starting tournament! Player 1: {}, Player 2: {})", player1.getVersion(), player2.getVersion());

        int player1Wins = 0;
        int player2Wins = 0;
        int draws = 0;

        int gameCount = 1;

        while (gameCount <= GAME_LIMIT) {

            log.info("Starting game {}...", gameCount);

            int whitePlayerRandom = new Random().nextInt(2);
            Player whitePlayer = players.get(whitePlayerRandom);
            Player blackPlayer = whitePlayerRandom == 1 ? players.get(0) : players.get(1);
            log.info("Player {} is white, player {} is black", whitePlayer.getVersion(), blackPlayer.getVersion());

            NewGameResponse newGameResponse = restTemplate.getForEntity(whitePlayer.getUrl() + "/new", NewGameResponse.class).getBody();
            String gameId = newGameResponse.getGameId();
            log.debug("Game {} ID: {}", gameCount, gameId);

            GameResult result = GameResult.IN_PROGRESS;

            PlayRequest move = STARTING_MOVES.get(new Random().nextInt(4));
            log.info("White ({}) plays {}", whitePlayer.getVersion(), move.toMoveString());
            int moveCount = 1;

            while (moveCount <= MOVE_LIMIT) {

                log.info("White ({}) plays {}", blackPlayer.getVersion(), move.toMoveString());
                PlayResponse blackResponse = restTemplate.postForObject(blackPlayer.getUrl() + "/play", move, PlayResponse.class);
                result = blackResponse.getResult();
                if (!result.equals(GameResult.IN_PROGRESS)) {
                    if (result.isWin()) {
                        if (blackPlayer.getVersion().equals(player1.getVersion())) {
                            player1Wins++;
                        } else {
                            player2Wins++;
                        }
                    } else {
                        draws++;
                    }
                    break;
                }
                move = blackResponse.toPlayRequest();
                log.info("Black ({}) plays {}", blackPlayer.getVersion(), move.toMoveString());

                PlayResponse whiteResponse = restTemplate.postForObject(whitePlayer.getUrl() + "/play", move, PlayResponse.class);
                result = blackResponse.getResult();
                if (!result.equals(GameResult.IN_PROGRESS)) {
                    if (result.isWin()) {
                        if (blackPlayer.getVersion().equals(player1.getVersion())) {
                            player1Wins++;
                        } else {
                            player2Wins++;
                        }
                    } else {
                        draws++;
                    }
                    break;
                }
                move = whiteResponse.toPlayRequest();

                moveCount++;
            }
            log.info("Game over! Result: {}", result);
            gameCount++;

        }

        log.info("Tournament over! Results:");
        log.info("{} wins: {}", player1.getVersion(), player1Wins);
        log.info("{} wins: {}", player2.getVersion(), player2Wins);
        log.info("Draws: {}", draws);

    }


}
