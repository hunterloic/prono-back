package org.loic.rest.json.request;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ManagedRessource {
    protected String id;
    protected boolean deleted;

    public boolean isNew() {
        return StringUtils.isBlank(id);
    }
}
