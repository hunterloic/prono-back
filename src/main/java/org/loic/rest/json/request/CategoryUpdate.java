package org.loic.rest.json.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryUpdate extends ManagedRessource {

    @NotNull(message = "Category order is mandatory")
    private int order;

    @NotBlank(message = "Category name is mandatory")
    private String name;

    public boolean hasBlankName() {
        return StringUtils.isBlank(name);
    }

}
