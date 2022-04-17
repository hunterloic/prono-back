package org.loic.rest.controller;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.bson.types.ObjectId;
import org.loic.domain.data.Game;
import org.loic.domain.data.Game.Category;
import org.loic.domain.data.Game.Team;
import org.loic.rest.json.request.GameUpdate;

import io.quarkus.security.Authenticated;

@Path("/game")
@Authenticated
public class GameController {

    @GET
    public List<Game> list() {
        return Game.findAll().list();
    }

    @PUT
    @RolesAllowed("admin")
    public List<Game> update(Set<GameUpdate> games) {

        BiFunction<Game, GameUpdate, Game> gameInitializator = (game, gameUpdate) -> {

            List<Team> teams = gameUpdate.getTeams().stream()
                    .map(t -> new Team(
                            new ObjectId(t.getId()), t.getCode(), t.getName(), t.getGoal()))
                    .collect(Collectors.toList());

            Category category = new Category(new ObjectId(gameUpdate.getCategory().getId()),
                    gameUpdate.getCategory().getOrder(),
                    gameUpdate.getCategory().getName());

            game.setStartTime(gameUpdate.getStartTime());
            game.setTeams(teams);
            game.setCategory(category);

            return game;
        };

        final Set<Game> teamsToAdd = games.stream()
                .filter(Predicate.not(
                        GameUpdate::isDeleted))
                .filter(GameUpdate::hasBlankId)
                .map(gameUpdate -> {
                    Game game = new Game();
                    return gameInitializator.apply(game, gameUpdate);
                })
                .collect(Collectors.toSet());
        Game.persist(teamsToAdd);

        games.stream()
                .filter(GameUpdate::isDeleted)
                .filter(Predicate.not(
                        GameUpdate::hasBlankId))
                .forEach(game -> Game.deleteById(new ObjectId(game.getId())));

        games.stream()
                .filter(Predicate.not(
                        GameUpdate::isDeleted))
                .filter(Predicate.not(
                        GameUpdate::hasBlankId))
                .forEach(gameUpdate -> {
                    Game game = Game.findById(new ObjectId(gameUpdate.getId()));
                    game = gameInitializator.apply(game, gameUpdate);
                    game.update();
                });

        return Game.findAll().list();
    }

}
