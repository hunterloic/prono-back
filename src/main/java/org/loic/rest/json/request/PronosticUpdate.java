package org.loic.rest.json.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PronosticUpdate extends ManagedRessource {
    private String gameId;
    private String teamId;
    private int pronostic;
}
