package org.loic.rest.json.request;

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

    public boolean hasBlankId() {
        return StringUtils.isBlank(id);
    }

    public boolean hasBlankName() {
        return StringUtils.isBlank(name);
    }

    public boolean hasBlankCode() {
        return StringUtils.isBlank(code);
    }

}
