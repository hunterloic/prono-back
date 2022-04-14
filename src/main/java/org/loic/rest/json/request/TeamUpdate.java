package org.loic.rest.json.request;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TeamUpdate extends ManagedRessource {

    @NotBlank(message = "Team name is mandatory")
    private String name;

    @NotBlank(message = "Team code is mandatory")
    private String code;

    public boolean hasBlankName() {
        return StringUtils.isBlank(name);
    }

    public boolean hasBlankCode() {
        return StringUtils.isBlank(code);
    }

}
