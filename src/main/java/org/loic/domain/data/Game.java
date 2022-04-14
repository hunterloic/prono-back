package org.loic.domain.data;

import java.util.List;

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
}
