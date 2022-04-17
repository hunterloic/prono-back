package org.loic.domain.data;

import java.util.List;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "pronostic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pronostic extends PanacheMongoEntity {
    private String userName;
    private String gameId;
    private String teamId;
    private int pronostic;

    public static List<Pronostic> findByUserName(String userName) {
        return Pronostic.find("userName = ?1", userName).list();
    }

}
