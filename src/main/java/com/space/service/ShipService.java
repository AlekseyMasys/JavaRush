package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 * Ship service layer
 */
@Service
public class ShipService {
    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    /**
     * Method returns List of ships that are satisfy the params
     */
    public List<Ship> findAllShipsByParameters(String name,
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
                                               Integer pageNumber,
                                               Integer pageSize,
                                               ShipOrder shipOrder) {

        List<Ship> allShips = shipRepository.findShipsByParameters(name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating,
                shipOrder);
        int startIndex = pageNumber * pageSize;
        int stopIndex = Math.min(startIndex + pageSize, allShips.size());
        return allShips.subList(startIndex, stopIndex);
    }

    /**
     * Method returns quantity of ships that are satisfy the params
     */
    public int findShipsCount(String name,
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
                              Double maxRating) {

        List<Ship> allShips = shipRepository.findShipsByParameters(name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating,
                null);
        return allShips.size();
    }

    /**
     * Method creates ship entity and forms the response
     */
    public ResponseEntity<Ship> createShip(Ship ship) {
        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }
        try {
            if (isShipValidated(ship)) {
                ship.setRating(calculateRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate().getTime()));
                shipRepository.save(ship);
                return new ResponseEntity<>(ship, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method returns ship from repository by id and forms the response
     */
    public ResponseEntity<Ship> findShip(Long id) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Ship> shipOptional = shipRepository.findById(id);
            if (shipOptional.isPresent()) {
                Ship ship = shipOptional.get();
                return new ResponseEntity<>(ship, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method deletes ship from repository by id and forms the response
     */
    public ResponseEntity deleteShip(Long id) {
        if (id <= 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Ship> shipOpt = shipRepository.findById(id);
            if (shipOpt.isPresent()) {
                shipRepository.deleteById(id);
                return new ResponseEntity(HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method updates ship in repository by id and forms the response
     */
    public ResponseEntity<Ship> updateShip(Long id, Ship ship) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<Ship> shipOpt = shipRepository.findById(id);
            if (shipOpt.isPresent()) {
                Ship shipToUpdate = shipOpt.get();
                updateShipFields(ship, shipToUpdate);
                if (isShipValidated(shipToUpdate)) {
                    shipToUpdate.setRating(calculateRating(shipToUpdate.getSpeed(), shipToUpdate.getUsed(), shipToUpdate.getProdDate().getTime()));
                    shipRepository.save(shipToUpdate);
                    return new ResponseEntity<>(shipToUpdate, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method for calculating ship rating
     */
    private Double calculateRating(Double speed, Boolean isUsed, Long prodDate) {
        int currentYear = 3019;
        Date dateTime = new Date(prodDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        int prodYear = calendar.get(Calendar.YEAR);
        double usedCoeff = isUsed ? 0.5 : 1;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.##", otherSymbols);
        String ratingString = (df.format(((80 * speed * usedCoeff) / (currentYear - prodYear + 1))));
        return Double.valueOf(ratingString);
    }

    /**
     * Method for validating ship fields before persistence layer
     */
    private boolean isShipValidated(Ship ship) {
        String name = ship.getName();
        String planet = ship.getPlanet();
        Date prodDate = ship.getProdDate();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prodDate);
        int year = calendar.get(Calendar.YEAR);
        if (name == null || name.isEmpty() || name.length() > 50) {
            return false;
        }
        if (planet == null || planet.isEmpty() || planet.length() > 50) {
            return false;
        }
        if (speed == null || speed > 0.99d || speed < 0.1) {
            return false;
        }
        if (crewSize == null || crewSize < 1 || crewSize > 9999) {
            return false;
        }
        return year >= 2800 && year <= 3019;
    }

    /**
     * Method for updating fields of ship from repository by fields of ship from request
     */
    private void updateShipFields(Ship fromShip, Ship toShip) {
        String name = fromShip.getName();
        String planet = fromShip.getPlanet();
        Date prodDate = fromShip.getProdDate();
        Boolean isUsed = fromShip.getUsed();
        Double speed = fromShip.getSpeed();
        Integer crewSize = fromShip.getCrewSize();
        ShipType shipType = fromShip.getShipType();
        if (name != null) {
            toShip.setName(name);
        }
        if (planet != null) {
            toShip.setPlanet(planet);
        }
        if (prodDate != null) {
            toShip.setProdDate(prodDate);
        }
        if (speed != null) {
            toShip.setSpeed(speed);
        }
        if (crewSize != null) {
            toShip.setCrewSize(crewSize);
        }
        if (isUsed != null) {
            toShip.setUsed(isUsed);
        }
        if (shipType != null) {
            toShip.setShipType(shipType);
        }
    }
}