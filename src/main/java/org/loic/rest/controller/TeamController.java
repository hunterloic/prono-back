package org.loic.rest.controller;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.bson.types.ObjectId;
import org.loic.domain.data.TeamEntity;
import org.loic.rest.json.response.TeamUpdate;

@Path("/teams")
public class TeamController {

    @GET
    public List<TeamEntity> list() {
        return TeamEntity.findAll().list();
    }

    @PUT
    public List<TeamEntity> update(Set<TeamUpdate> teams) {

        final Set<TeamEntity> teamsToAdd = teams.stream()
                .filter(Predicate.not(TeamUpdate::isDeleted))
                .filter(TeamUpdate::isBlankId)
                .filter(Predicate.not(TeamUpdate::isBlankCode))
                .filter(Predicate.not(TeamUpdate::isBlankName))
                .map(t -> new TeamEntity(t.getName(), t.getCode()))
                .collect(Collectors.toSet());
        TeamEntity.persist(teamsToAdd);

        teams.stream()
                .filter(TeamUpdate::isDeleted)
                .filter(Predicate.not(TeamUpdate::isBlankId))
                .forEach(team -> TeamEntity.deleteById(new ObjectId(team.getId())));

        teams.stream()
                .filter(Predicate.not(TeamUpdate::isDeleted))
                .filter(Predicate.not(TeamUpdate::isBlankId))
                .forEach(team -> {
                    TeamEntity teamToUpdate = TeamEntity.findById(new ObjectId(team.getId()));
                    teamToUpdate.setCode(team.getCode());
                    teamToUpdate.setName(team.getName());
                    teamToUpdate.update();
                });

        return TeamEntity.findAll().list();
    }

}
