package org.loic.rest.json.response;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TeamUpdate {
    private String id;
    private String name;
    private String code;
    private boolean deleted;

    public boolean isBlankId() {
        return StringUtils.isBlank(id);
    }

    public boolean isBlankName() {
        return StringUtils.isBlank(name);
    }

    public boolean isBlankCode() {
        return StringUtils.isBlank(code);
    }

}
