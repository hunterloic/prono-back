package org.loic.rest.json.request;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GroupUpdate extends ManagedRessource {
    private String name;
    private String password;
    private List<Member> members;

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Member {
        @NotBlank(message = "User name is mandatory")
        private String userName;
    }
}
