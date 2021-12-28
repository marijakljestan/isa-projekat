package com.backend.controller;


import com.backend.model.Reservation;
import com.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/reservation", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReservationController {

    @Autowired
    ReservationService service;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> saveReservation(@RequestBody Reservation reservation) {
        if(reservation ==null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request from client!");
        boolean saved = service.Save(reservation);
        if(!saved) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given period is occupied!");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
