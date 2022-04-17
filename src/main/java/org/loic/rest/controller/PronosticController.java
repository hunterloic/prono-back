package org.loic.rest.controller;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.bson.types.ObjectId;
import org.loic.domain.data.Pronostic;
import org.loic.rest.json.request.PronosticUpdate;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;

@Path("/pronostic")
@Authenticated
public class PronosticController {
    @Inject
    SecurityIdentity securityIdentity;

    @GET
    public List<Pronostic> list() {

        return Pronostic.findByUserName(securityIdentity.getPrincipal()
                .getName());
    }

    @PUT
    public List<Pronostic> update(Set<PronosticUpdate> pronostics) {

        String userName = securityIdentity.getPrincipal()
                .getName();

        Pronostic.persist(pronostics.stream()
                .filter(Predicate.not(PronosticUpdate::isDeleted))
                .filter(PronosticUpdate::isNew)
                .map(pronostic -> new Pronostic(userName,
                        pronostic.getGameId(), pronostic.getTeamId(), pronostic.getPronostic()))
                .collect(Collectors.toSet()));

        pronostics.stream()
                .filter(PronosticUpdate::isDeleted)
                .filter(Predicate.not(PronosticUpdate::isNew))
                .forEach(pronostic -> Pronostic.deleteById(new ObjectId(pronostic.getId())));

        pronostics.stream()
                .filter(Predicate.not(PronosticUpdate::isDeleted))
                .filter(Predicate.not(PronosticUpdate::isNew))
                .forEach(pronostic -> {
                    Pronostic pronosticToUpdate = Pronostic.findById(new ObjectId(pronostic.getId()));
                    pronosticToUpdate.setGameId(pronostic.getGameId());
                    pronosticToUpdate.setTeamId(pronostic.getTeamId());
                    pronosticToUpdate.setPronostic(pronostic.getPronostic());
                    pronosticToUpdate.update();
                });

        return Pronostic.findByUserName(userName);

    }
}
