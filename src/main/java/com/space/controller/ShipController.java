package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    /**
     * Get ships by params
     **/
    @RequestMapping(value = "/rest/ships", method = RequestMethod.GET)
    private List<Ship> getAllShips(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String planet,
                                   @RequestParam(required = false) ShipType shipType,
                                   @RequestParam(required = false) Long after,
                                   @RequestParam(required = false) Long before,
                                   @RequestParam(required = false) Boolean isUsed,
                                   @RequestParam(required = false) Double minSpeed,
                                   @RequestParam(required = false) Double maxSpeed,
                                   @RequestParam(required = false) Integer minCrewSize,
                                   @RequestParam(required = false) Integer maxCrewSize,
                                   @RequestParam(required = false) Double minRating,
                                   @RequestParam(required = false) Double maxRating,
                                   @RequestParam(required = false) ShipOrder order,
                                   @RequestParam(defaultValue = "0", required = false) Integer pageNumber,
                                   @RequestParam(defaultValue = "3", required = false) Integer pageSize) {

        return shipService.findAllShipsByParameters(name,
                planet,
                shipType,
                after, before,
                isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating,
                pageNumber,
                pageSize,
                order);
    }

    /**
     * Get ships count by params
     **/
    @RequestMapping(value = "/rest/ships/count", method = RequestMethod.GET)
    private Integer getShipsCount(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) String planet,
                                  @RequestParam(required = false) ShipType shipType,
                                  @RequestParam(required = false) Long after,
                                  @RequestParam(required = false) Long before,
                                  @RequestParam(required = false) Boolean isUsed,
                                  @RequestParam(required = false) Double minSpeed,
                                  @RequestParam(required = false) Double maxSpeed,
                                  @RequestParam(required = false) Integer minCrewSize,
                                  @RequestParam(required = false) Integer maxCrewSize,
                                  @RequestParam(required = false) Double minRating,
                                  @RequestParam(required = false) Double maxRating) {

        return shipService.findShipsCount(name,
                planet,
                shipType,
                after,
                before,
                isUsed,
                minSpeed, maxSpeed,
                minCrewSize, maxCrewSize,
                minRating, maxRating);
    }

    /**
     * Creating ship
     **/
    @RequestMapping(value = "/rest/ships/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    private ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        return shipService.createShip(ship);
    }

    /**
     * Get ship by id
     **/
    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.GET)
    private ResponseEntity<Ship> getShipById(@PathVariable("id") Long id) {
        return shipService.findShip(id);
    }

    /**
     * Update ship
     **/
    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.POST)
    private ResponseEntity<Ship> updateShipById(@PathVariable("id") Long id, @RequestBody Ship ship) {
        return shipService.updateShip(id, ship);
    }

    /**
     * Delete ship
     **/
    @RequestMapping(value = "/rest/ships/{id}", method = RequestMethod.DELETE)
    private ResponseEntity deleteShipById(@PathVariable("id") Long id) {
        return shipService.deleteShip(id);
    }


}