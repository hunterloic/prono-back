package org.loic.rest.json.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GameUpdate extends ManagedRessource {

    private String id;

    @NotNull(message = "Game startTime is mandatory")
    private long startTime;

    @NotNull(message = "Game category is mandatory")
    private Category category;

    @NotNull(message = "Game team is mandatory")
    @Size(min = 2, max = 2, message = "Two teams are required")
    private List<Team> teams;

    public boolean hasBlankId() {
        return StringUtils.isBlank(id);
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Category {
        @NotBlank(message = "Category id is mandatory")
        private String id;

        @NotNull(message = "Category id is mandatory")
        private int order;

        @NotBlank(message = "Category name is mandatory")
        private String name;
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Team {

        @NotBlank(message = "Team id is mandatory")
        private String id;

        @NotBlank(message = "Team code is mandatory")
        private String code;

        @NotBlank(message = "Team name is mandatory")
        private String name;

        private Integer goal;
    }

}
