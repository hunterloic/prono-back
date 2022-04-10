package org.loic.rest.controller;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.bson.types.ObjectId;
import org.loic.domain.data.CategoryEntity;
import org.loic.rest.json.request.CategoryUpdate;

import io.quarkus.security.Authenticated;

@Path("/categories")
@Authenticated
public class CategoryController {

    @GET
    public List<CategoryEntity> list() {
        List<CategoryEntity> categories = CategoryEntity.findAll().list();
        categories.sort((o1, o2) -> Integer.parseInt(o1.getOrder()) - Integer.parseInt(o2.getOrder()));
        return categories;
    }

    @PUT
    @RolesAllowed("admin")
    public List<CategoryEntity> update(Set<CategoryUpdate> categories) {

        final Set<CategoryEntity> categoriesToAdd = categories.stream()
                .filter(Predicate.not(CategoryUpdate::isDeleted))
                .filter(CategoryUpdate::hasBlankId)
                .filter(Predicate.not(CategoryUpdate::hasBlankOrder))
                .filter(Predicate.not(CategoryUpdate::hasBlankName))
                .map(t -> new CategoryEntity(t.getOrder(), t.getName()))
                .collect(Collectors.toSet());
        CategoryEntity.persist(categoriesToAdd);

        categories.stream()
                .filter(CategoryUpdate::isDeleted)
                .filter(Predicate.not(CategoryUpdate::hasBlankId))
                .forEach(team -> CategoryEntity.deleteById(new ObjectId(team.getId())));

        categories.stream()
                .filter(Predicate.not(CategoryUpdate::isDeleted))
                .filter(Predicate.not(CategoryUpdate::hasBlankId))
                .forEach(team -> {
                    CategoryEntity teamToUpdate = CategoryEntity.findById(new ObjectId(team.getId()));
                    teamToUpdate.setOrder(team.getOrder());
                    teamToUpdate.setName(team.getName());
                    teamToUpdate.update();
                });

        List<CategoryEntity> categoriesUpdated = CategoryEntity.findAll().list();
        categoriesUpdated.sort((o1, o2) -> Integer.parseInt(o1.getOrder()) - Integer.parseInt(o2.getOrder()));
        return categoriesUpdated;
    }

}
