package org.loic.domain.data;

import java.util.List;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MongoEntity(collection = "groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Group extends PanacheMongoEntity {
    private String name;
    private String password;
    private List<Member> members;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Member {
        private String userName;
    }

}
