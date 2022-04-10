package org.loic.rest.json.request;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryUpdate {
    private String id;
    private int order;
    private String name;
    private boolean deleted;

    public boolean hasBlankId() {
        return StringUtils.isBlank(id);
    }

    public boolean hasBlankName() {
        return StringUtils.isBlank(name);
    }

}
