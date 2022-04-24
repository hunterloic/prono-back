package org.loic.rest.json.response;

import java.util.List;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private ObjectId id;
    private String name;
    private List<Member> members;

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Member {
        private String userName;
    }
}
