package org.loic.rest.json.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Team {

    private String teamId;
    private String name;
    private String code;
}
