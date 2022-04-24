package org.loic.rest.controller;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.loic.domain.data.Group;
import org.loic.rest.json.request.GroupUpdate;
import org.loic.rest.json.response.GroupResponse;

import io.quarkus.security.Authenticated;

@Path("/group")
@Authenticated
public class GroupController {

    Function<Group.Member, GroupResponse.Member> memberResponseMapper = (m) -> {
        return new GroupResponse.Member(m.getUserName());
    };

    Function<Group, GroupResponse> groupResponseMapper = (g) -> {
        return new GroupResponse(g.id, g.getName(),
                g.getMembers().stream().map(memberResponseMapper).collect(Collectors.toList()));
    };

    Function<GroupUpdate.Member, Group.Member> memberDataMapper = (m) -> {
        return new Group.Member(m.getUserName());
    };

    Function<GroupUpdate, Group> groupDataMapper = (g) -> {
        return new Group(g.getName(), g.getPassword(),
                g.getMembers().stream().map(memberDataMapper).collect(Collectors.toList()));
    };

    Consumer<GroupUpdate> groupDeleter = (g) -> {
        Group.deleteById(new ObjectId(g.getId()));
    };
    Consumer<GroupUpdate> groupUpdater = (g) -> {
        Group groupToUpdate = Group
                .findById(new ObjectId(g.getId()));
        groupToUpdate.setName(g.getName());
        if (StringUtils.isNotBlank(g.getPassword())) {
            groupToUpdate.setPassword(g.getPassword());
        }
        groupToUpdate.setMembers(
                g.getMembers().stream().map(memberDataMapper).collect(Collectors.toList()));
        groupToUpdate.update();
    };

    @GET
    public List<GroupResponse> list() {
        Stream<Group> groups = Group.findAll().stream();
        return groups.map(groupResponseMapper).collect(Collectors.toList());
    }

    @PUT
    @RolesAllowed("admin")
    public List<GroupResponse> update(Set<GroupUpdate> groups) {

        Group.persist(groups.stream()
                .filter(Predicate.not(GroupUpdate::isDeleted))
                .filter(GroupUpdate::isNew)
                .map(groupDataMapper)
                .collect(Collectors.toSet()));

        groups.stream()
                .filter(GroupUpdate::isDeleted)
                .filter(Predicate.not(GroupUpdate::isNew))
                .forEach(groupDeleter);

        groups.stream()
                .filter(Predicate.not(GroupUpdate::isDeleted))
                .filter(Predicate.not(GroupUpdate::isNew))
                .forEach(groupUpdater);

        Stream<Group> groupsUpdated = Group.findAll().stream();
        return groupsUpdated.map(groupResponseMapper).collect(Collectors.toList());
    }
}
