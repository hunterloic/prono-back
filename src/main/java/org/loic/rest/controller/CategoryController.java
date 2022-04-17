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
import org.loic.domain.data.Category;
import org.loic.rest.json.request.CategoryUpdate;

import io.quarkus.security.Authenticated;

@Path("/category")
@Authenticated
public class CategoryController {

    @GET
    public List<Category> list() {
        List<Category> categories = Category.findAll().list();
        categories.sort((o1, o2) -> o1.getOrder() - o2.getOrder());
        return categories;
    }

    @PUT
    @RolesAllowed("admin")
    public List<Category> update(Set<CategoryUpdate> categories) {

        Category.persist(categories.stream()
                .filter(Predicate.not(CategoryUpdate::isDeleted))
                .filter(CategoryUpdate::isNew)
                .map(category -> new Category(category.getOrder(), category.getName()))
                .collect(Collectors.toSet()));

        categories.stream()
                .filter(CategoryUpdate::isDeleted)
                .filter(Predicate.not(CategoryUpdate::isNew))
                .forEach(category -> Category.deleteById(new ObjectId(category.getId())));

        categories.stream()
                .filter(Predicate.not(CategoryUpdate::isDeleted))
                .filter(Predicate.not(CategoryUpdate::isNew))
                .forEach(category -> {
                    Category categoryToUpdate = Category.findById(new ObjectId(category.getId()));
                    categoryToUpdate.setOrder(category.getOrder());
                    categoryToUpdate.setName(category.getName());
                    categoryToUpdate.update();
                });

        List<Category> categoriesUpdated = Category.findAll().list();
        categoriesUpdated.sort((o1, o2) -> o1.getOrder() - o2.getOrder());
        return categoriesUpdated;
    }

}
