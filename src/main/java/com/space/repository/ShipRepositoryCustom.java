package com.space.repository;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

/**
 * Custom repository interface for implementing in repository class
 */
public interface ShipRepositoryCustom {
    List<Ship> findShipsByParameters(String name,
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
                                     ShipOrder shipOrder);
}
