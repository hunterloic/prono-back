package org.loic.rest.json.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LadderGroupResponse {

    private Group group;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Group {
        private String name;
        private List<Member> members = new ArrayList<Member>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Member {
        private String userName;
        private int points;
    }
}
