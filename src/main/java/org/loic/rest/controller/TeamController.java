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
import org.loic.domain.data.Team;
import org.loic.rest.json.request.TeamUpdate;

import io.quarkus.security.Authenticated;

@Path("/teams")
@Authenticated
public class TeamController {

    @GET
    public List<Team> list() {
        return Team.findAll().list();
    }

    @PUT
    @RolesAllowed("admin")
    public List<Team> update(Set<TeamUpdate> teams) {

        Team.persist(teams.stream()
                .filter(Predicate.not(TeamUpdate::isDeleted))
                .filter(TeamUpdate::isNew)
                .map(t -> new Team(t.getCode(), t.getName()))
                .collect(Collectors.toSet()));

        teams.stream()
                .filter(TeamUpdate::isDeleted)
                .filter(Predicate.not(TeamUpdate::isNew))
                .forEach(team -> Team.deleteById(new ObjectId(team.getId())));

        teams.stream()
                .filter(Predicate.not(TeamUpdate::isDeleted))
                .filter(Predicate.not(TeamUpdate::isNew))
                .forEach(team -> {
                    Team teamToUpdate = Team.findById(new ObjectId(team.getId()));
                    teamToUpdate.setCode(team.getCode());
                    teamToUpdate.setName(team.getName());
                    teamToUpdate.update();
                });

        return Team.findAll().list();
    }

}
