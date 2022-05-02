package org.loic.rest.controller;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.loic.domain.data.Game;
import org.loic.domain.data.Pronostic;

import io.quarkus.security.identity.SecurityIdentity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Path("/ladder")
// @Authenticated
public class LadderController {

    private final static int POINTS_MATCH_WINNER = 1;
    private final static int POINTS_MATCH_SCORE = 3;

    @Inject
    SecurityIdentity securityIdentity;

    private final Predicate<List<Game.Team>> drawResultPredicate = (teams) -> {
        if (teams.stream().map(Game.Team::getGoal).anyMatch(Objects::isNull)) {
            return false;
        }

        return teams.stream().map(Game.Team::getGoal).distinct().count() <= 1;

    };

    private final Function<List<Game.Team>, Optional<Game.Team>> winnerResultTeam = (teams) -> {
        if (teams.stream().map(Game.Team::getGoal).anyMatch(Objects::isNull)) {
            return Optional.empty();
        }

        if (teams.stream().map(Game.Team::getGoal).distinct().count() <= 1) {
            return Optional.empty();
        }

        return teams.stream().max(Comparator.comparing((Game.Team team) -> {
            if (team.getGoal() == null) {
                return -1;
            }
            return team.getGoal();
        }));
    };

    private final Predicate<List<Pronostic>> drawPronosticPredicate = (pronostics) -> {
        if (pronostics.stream().map(Pronostic::getPronostic).anyMatch(Objects::isNull)) {
            return false;
        }

        return pronostics.stream().map(Pronostic::getPronostic).distinct().count() <= 1;
    };

    private final Function<List<Pronostic>, Optional<Pronostic>> winnerPronosticTeam = (pronostics) -> {
        if (pronostics.stream().map(Pronostic::getPronostic).anyMatch(Objects::isNull)) {
            return Optional.empty();
        }

        if (pronostics.stream().map(Pronostic::getPronostic).distinct().count() <= 1) {
            return Optional.empty();
        }

        return pronostics.stream().max(Comparator.comparing((Pronostic pronostic) -> {
            if (pronostic.getPronostic() == null) {
                return null;
            }
            return pronostic.getPronostic();
        }));
    };

    private final Predicate<Game> gameIsOverPredicate = (game) -> {
        boolean gameResultIsSet = game.getTeams().stream().map(Game.Team::getGoal).map(Objects::isNull)
                .allMatch(goalIsNull -> !goalIsNull);
        return game.getStartTime() < Instant.now().getEpochSecond() && gameResultIsSet;
    };

    @GET
    @Path("/user")
    public int pointWithUserName() {

        String userName = securityIdentity.getPrincipal().getName();

        List<Game> games = Game.findAll().list();
        List<Pronostic> pronostics = Pronostic.findByUserName(userName);

        return games.stream().filter(gameIsOverPredicate).map(g -> {

            List<Pronostic> gamePronostics = pronostics.stream()
                    .filter(p -> p.getGameId().equals(g.id.toString()) && p.getPronostic() != null)
                    .collect(Collectors.toList());
            if (gamePronostics.size() != g.getTeams().size()) {
                return 0;
            }

            boolean drawResult = drawResultPredicate.test(g.getTeams());
            Optional<Game.Team> winnerTeamResult = winnerResultTeam.apply(g.getTeams());

            boolean drawPronostic = drawPronosticPredicate.test(gamePronostics);
            Optional<Pronostic> winnerTeamPronostic = winnerPronosticTeam.apply(gamePronostics);

            boolean resultMatch = drawResult && drawPronostic
                    || (winnerTeamPronostic.isPresent() && winnerTeamResult
                            .map(teamResult -> teamResult.getId().toString()
                                    .equals(winnerTeamPronostic.map(teamPronostic -> teamPronostic.getTeamId()).get()))
                            .orElse(false));

            int point = 0;
            if (resultMatch) {
                boolean scoreMatch = g.getTeams().stream()
                        .map(teamResult -> teamResult.getGoal() == gamePronostics.stream()
                                .filter(gamePronostic -> gamePronostic.getTeamId()
                                        .equals(teamResult.getId().toString()))
                                .map(teamPronostic -> teamPronostic.getPronostic()).findFirst().get())
                        .reduce(true, (a, b) -> a && b);

                point = scoreMatch ? POINTS_MATCH_SCORE : POINTS_MATCH_WINNER;
            }
            return point;

        }).reduce(0, Integer::sum);
    }

    @AllArgsConstructor
    @Data
    public static class User {
        private String name;
        private int age;
    }
}
