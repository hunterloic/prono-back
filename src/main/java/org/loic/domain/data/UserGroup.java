package org.loic.domain.data;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "userGroup")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup extends PanacheMongoEntity {
    private String groupId;
    private String userName;
}
