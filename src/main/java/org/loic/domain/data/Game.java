package org.loic.domain.data;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "games")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Game extends PanacheMongoEntity {

    private final static Predicate<Game> gameIsOverPredicate = (game) -> {
        boolean gameResultIsSet = game.getTeams().stream().map(Game.Team::getGoal).map(Objects::isNull)
                .allMatch(goalIsNull -> !goalIsNull);
        return game.getStartTime() < Instant.now().getEpochSecond() && gameResultIsSet;
    };

    private long startTime;
    private Category category;
    private List<Team> teams;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category {
        public org.bson.types.ObjectId id;
        private int order;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Team {
        public org.bson.types.ObjectId id;
        private String code;
        private String name;
        private Integer goal;
    }

    public static List<Game> findOverGames() {
        List<Game> allGames = Game.findAll().list();
        return allGames.stream().filter(gameIsOverPredicate).collect(Collectors.toList());
    }
}
