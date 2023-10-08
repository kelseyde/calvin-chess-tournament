package com.kelseyde.calvin.tournament.service;

import com.kelseyde.calvin.tournament.configuration.PlayerConfiguration;
import com.kelseyde.calvin.tournament.model.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentService {

    @Value("${tournament.game-limit}")
    private Integer gameLimit;

    @Value("${tournament.move-limit}")
    private Integer moveLimit;

    @Value("${tournament.min-think-time-ms}")
    private Integer minThinkTimeMs;

    @Value("${tournament.max-think-time-ms}")
    private Integer maxThinkTimeMs;

    private final PlayerConfiguration playerConfiguration;

    private final RestTemplate restTemplate;

    private final Random random = new Random();

    private final List<PlayRequest> STARTING_MOVES = List.of(
            PlayRequest.builder().from("e2").to("e4").promotion("0").build(),
            PlayRequest.builder().from("d2").to("d4").promotion("0").build(),
            PlayRequest.builder().from("c2").to("c4").promotion("0").build(),
            PlayRequest.builder().from("g1").to("f3").promotion("0").build()
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

        while (gameCount <= gameLimit) {

            log.info("Starting game {}...", gameCount);

            int whitePlayerRandom = new Random().nextInt(2);
            Player whitePlayer = players.get(whitePlayerRandom);
            Player blackPlayer = whitePlayerRandom == 1 ? players.get(0) : players.get(1);
            log.info("Player {} is white, player {} is black", whitePlayer.getVersion(), blackPlayer.getVersion());

            NewGameResponse whiteNewGameResponse = restTemplate.getForEntity(whitePlayer.getUrl() + "/new/white", NewGameResponse.class).getBody();
            String whiteGameId = whiteNewGameResponse.getGameId();
            log.debug("White game {} ID: {}", gameCount, whiteGameId);

            NewGameResponse blackNewGameResponse =  restTemplate.getForEntity(blackPlayer.getUrl() + "/new/black", NewGameResponse.class).getBody();
            String blackGameId = blackNewGameResponse.getGameId();
            log.debug("Black game {} ID: {}", gameCount, blackGameId);

            GameResult result = GameResult.IN_PROGRESS;

            MoveResponse move = whiteNewGameResponse.getMove();
            int moveCount = 1;

            while (moveCount <= moveLimit) {

                PlayRequest whiteMoveRequest = move.toPlayRequest(blackGameId, getThinkTime());
                log.info("White ({}) plays {}", whitePlayer.getVersion(), whiteMoveRequest.toMoveString());
                log.debug("Sending request to black: {}", whiteMoveRequest);

                PlayResponse blackMoveResponse = restTemplate.postForObject(blackPlayer.getUrl() + "/play", whiteMoveRequest, PlayResponse.class);
                result = blackMoveResponse.getResult();
                blackGameId = blackMoveResponse.getGameId();
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
                move = blackMoveResponse.getMove();

                PlayRequest blackMoveRequest = move.toPlayRequest(whiteGameId, getThinkTime());
                log.info("Black ({}) plays {}", blackPlayer.getVersion(), blackMoveRequest.toMoveString());
                log.debug("Sending request to white: {}", blackMoveRequest);

                PlayResponse whiteMoveResponse = restTemplate.postForObject(whitePlayer.getUrl() + "/play", blackMoveRequest, PlayResponse.class);
                result = whiteMoveResponse.getResult();
                if (!result.equals(GameResult.IN_PROGRESS)) {
                    if (result.isWin()) {
                        if (whitePlayer.getVersion().equals(player1.getVersion())) {
                            player1Wins++;
                        } else {
                            player2Wins++;
                        }
                    } else {
                        draws++;
                    }
                    break;
                }
                move = whiteMoveResponse.getMove();

                moveCount++;
                if (moveCount > moveLimit) {
                    log.info("Terminating game after 100 moves.");
                    draws++;
                }
            }
            log.info("Game over! Move count {}, Result: {}", moveCount, result);
            log.info("Current standings: {} wins {}, {} wins {}, draws {}",
                    player1.getVersion(), player1Wins, player2.getVersion(), player2Wins, draws);
            gameCount++;

        }

        log.info("Tournament over! Results:");
        log.info("{} wins: {}", player1.getVersion(), player1Wins);
        log.info("{} wins: {}", player2.getVersion(), player2Wins);
        log.info("Draws: {}", draws);

    }

    private int getThinkTime() {
        return random.nextInt(minThinkTimeMs, maxThinkTimeMs);
    }

    @PostConstruct
    public void postConstruct() {
        run();
    }


}
