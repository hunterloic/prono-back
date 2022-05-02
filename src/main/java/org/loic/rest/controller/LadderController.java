package org.loic.rest.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.loic.domain.data.Game;
import org.loic.domain.data.Group;
import org.loic.domain.data.Pronostic;
import org.loic.rest.json.response.LadderGroupResponse;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;

@Path("/ladder")
@Authenticated
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

    private BiFunction<Game, List<Pronostic>, Integer> pronosticPointsCalculator = (game, pronostics) -> {

        if (pronostics.size() != game.getTeams().size()) {
            return 0;
        }

        boolean drawResult = drawResultPredicate.test(game.getTeams());
        Optional<Game.Team> winnerTeamResult = winnerResultTeam.apply(game.getTeams());

        boolean drawPronostic = drawPronosticPredicate.test(pronostics);
        Optional<Pronostic> winnerTeamPronostic = winnerPronosticTeam.apply(pronostics);

        boolean resultMatch = drawResult && drawPronostic
                || (winnerTeamPronostic.isPresent() && winnerTeamResult
                        .map(teamResult -> teamResult.getId().toString()
                                .equals(winnerTeamPronostic.map(teamPronostic -> teamPronostic.getTeamId())
                                        .get()))
                        .orElse(false));

        int point = 0;
        if (resultMatch) {
            boolean scoreMatch = game.getTeams().stream()
                    .map(teamResult -> teamResult.getGoal() == pronostics.stream()
                            .filter(gamePronostic -> gamePronostic.getTeamId()
                                    .equals(teamResult.getId().toString()))
                            .map(teamPronostic -> teamPronostic.getPronostic()).findFirst().get())
                    .reduce(true, (a, b) -> a && b);

            point = scoreMatch ? POINTS_MATCH_SCORE : POINTS_MATCH_WINNER;
        }
        return point;
    };

    @GET
    @Path("/user")
    public int pointWithUserName(@QueryParam("userName") String userName) {

        if (StringUtils.isBlank(userName)) {
            userName = securityIdentity.getPrincipal().getName();
        }

        List<Game> games = Game.findOverGames();
        List<Pronostic> pronostics = Pronostic.findByUserName(userName);

        return games.stream().map(game -> {

            List<Pronostic> gamePronostics = pronostics.stream()
                    .filter(p -> p.getGameId().equals(game.id.toString()) && p.getPronostic() != null)
                    .collect(Collectors.toList());

            return pronosticPointsCalculator.apply(game, gamePronostics);

        }).reduce(0, Integer::sum);
    }

    @GET
    @Path("/group")
    public LadderGroupResponse ladderBygroup() {

        String userName = securityIdentity.getPrincipal().getName();

        List<Group> groups = Group.findByMember(userName);
        List<Game> games = Game.findOverGames();
        List<Pronostic> pronostics = Pronostic.findAll().list();

        LadderGroupResponse response = new LadderGroupResponse();

        groups.stream().forEach(group -> {

            LadderGroupResponse.Group ladderGroup = new LadderGroupResponse.Group();
            ladderGroup.setName(group.getName());

            group.getMembers().stream().forEach(member -> {

                LadderGroupResponse.Member ladderMember = new LadderGroupResponse.Member();
                ladderMember.setUserName(member.getUserName());

                int points = games.stream().map(game -> {

                    List<Pronostic> userAndGamePronostic = pronostics.stream()
                            .filter(pronostic -> pronostic.getUserName().equals(member.getUserName()))
                            .filter(p -> p.getGameId().equals(game.id.toString()) && p.getPronostic() != null)
                            .collect(Collectors.toList());

                    return pronosticPointsCalculator.apply(game, userAndGamePronostic);

                }).reduce(0, Integer::sum);

                ladderMember.setPoints(points);
                ladderGroup.getMembers().add(ladderMember);
            });

            response.getGroups().add(ladderGroup);
        });

        return response;
    }

}
