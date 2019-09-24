package com.space.repository;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class ShipRepositoryCustomImpl implements ShipRepositoryCustom {


    private EntityManager em;

    public ShipRepositoryCustomImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Ship> findShipsByParameters(String name,
                                            String planet,
                                            ShipType shipType,
                                            Long after,
                                            Long before,
                                            Boolean isUsed,
                                            Double minSpeed,
                                            Double maxSpeed,
                                            Integer minCrewSize,
                                            Integer maxCrewSize,
                                            Double minRating,
                                            Double maxRating,
                                            ShipOrder shipOrder) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ship> cq = cb.createQuery(Ship.class);
        Root<Ship> ship = cq.from(Ship.class);
        List<Predicate> predicates = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-mm-dd");
        if (name != null) {
            predicates.add(cb.like(ship.get("name"), "%" + name + "%"));
        }
        if (planet != null) {
            predicates.add(cb.like(ship.get("planet"), "%" + planet + "%"));
        }
        if (shipType != null) {
            predicates.add(cb.equal(ship.get("shipType"), shipType));
        }
        if (before != null) {
            Date dateBefore = new Date(before);
            format.format(dateBefore);
            predicates.add(cb.lessThanOrEqualTo(ship.get("prodDate"), dateBefore));
        }
        if (after != null) {
            Date dateAfter = new Date(after);
            format.format(dateAfter);
            predicates.add(cb.greaterThanOrEqualTo(ship.get("prodDate"), dateAfter));
        }
        if (isUsed != null) {
            predicates.add(cb.equal(ship.get("isUsed"), isUsed));
        }
        if (minSpeed != null) {
            predicates.add(cb.greaterThanOrEqualTo(ship.get("speed"), minSpeed));
        }
        if (maxSpeed != null) {
            predicates.add(cb.lessThanOrEqualTo(ship.get("speed"), maxSpeed));
        }
        if (minCrewSize != null) {
            predicates.add(cb.greaterThanOrEqualTo(ship.get("crewSize"), minCrewSize));
        }
        if (maxCrewSize != null) {
            predicates.add(cb.lessThanOrEqualTo(ship.get("crewSize"), maxCrewSize));
        }
        if (minRating != null) {
            predicates.add(cb.greaterThanOrEqualTo(ship.get("rating"), minRating));
        }
        if (maxRating != null) {
            predicates.add(cb.lessThanOrEqualTo(ship.get("rating"), maxRating));
        }
        CriteriaQuery<Ship> scq = cq.where(predicates.toArray(new Predicate[0]));
        if (shipOrder != null) {
            scq.orderBy(cb.asc(ship.get(shipOrder.getFieldName())));
        }
        return em.createQuery(cq).getResultList();
    }
}