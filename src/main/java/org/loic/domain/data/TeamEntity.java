package org.loic.domain.data;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "team")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamEntity extends PanacheMongoEntity {

    private String code;
    private String name;

}
