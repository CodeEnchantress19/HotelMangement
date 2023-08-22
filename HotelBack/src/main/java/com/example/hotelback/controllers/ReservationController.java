package com.example.hotelback.controllers;

import com.example.hotelback.Entities.Cabin;
import com.example.hotelback.Entities.Etat;
import com.example.hotelback.Entities.Reservation;
import com.example.hotelback.Entities.User;
import com.example.hotelback.dto.UserDto;
import com.example.hotelback.services.ReservationService;
import com.example.hotelback.services.UserService;
import com.example.hotelback.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CabinController cabinController;
    @Autowired
    private  UserService userService;

    @GetMapping("/all")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable int id) {
        Reservation reservation = reservationService.getReservationById(id);
        if (reservation != null) {
            return ResponseEntity.ok(reservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



//    @PostMapping("/add/{idCabin}/{idUser}")
//    public ResponseEntity<Reservation> createReservationWithIds(
//            @PathVariable int idCabin, @PathVariable Long idUser, @RequestBody Reservation reservation) {
//        System.out.println(idUser);
//        Cabin cabin = cabinController.getCabinById(idCabin);
//        UserDto userDto = userService.getUserById(idUser);
//
//        if (cabin == null || userDto == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        User user = UserServiceImpl.ConversionService.convertUserDtoToUser(userDto);
//
//        reservation.setCabin(cabin);
//        reservation.setClient(user);
//
//        Reservation createdReservation = reservationService.createReservation(reservation);
//
//        if (createdReservation != null) {
//            return ResponseEntity.created(URI.create("/reservations/" + createdReservation.getId_reservation()))
//                    .body(createdReservation);
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//    }


    @PostMapping("/add/{idCabin}/{idUser}")
    public ResponseEntity<Reservation> createReservationWithIds(
            @PathVariable int idCabin, @PathVariable Long idUser, @RequestBody Reservation reservation) {
        System.out.println(idUser);
        Cabin cabin = cabinController.getCabinById(idCabin);
        UserDto userDto = userService.getUserById(idUser);

        if (cabin == null || userDto == null) {
            return ResponseEntity.notFound().build();
        }

        User user = UserServiceImpl.ConversionService.convertUserDtoToUser(userDto);

        reservation.setCabin(cabin);
        reservation.setClient(user);
        reservation.setState(Etat.inProgress); // Définir l'état initial comme "inProgress"

//        createdReservation.setTotalPrice(ReservationService.calculatePrice(createdReservation.getDateDeb(),createdReservation.getDateFin(),cabin.getPrice()));
//       // createdreservation.setTotalPrice(ReservationService.calculatePrice(createdReservation.getDateDeb(),createdReservation.getDateFin(),cabin.getPrice()));
        reservation.setTotalPrice(ReservationService.calculatePrice(reservation.getDateDeb(),reservation.getDateFin(),cabin.getPrice()));
        Reservation createdReservation = reservationService.createReservation(reservation);
        if (createdReservation != null) {
            return ResponseEntity.created(URI.create("/reservations/" + createdReservation.getId_reservation()))
                    .body(createdReservation);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }




    @PutMapping("/update/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable int id, @RequestBody Reservation reservation) {
        Reservation updatedReservation = reservationService.updateReservation(id, reservation);
        if (updatedReservation != null) {
            return ResponseEntity.ok(updatedReservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable int id) {
        if (reservationService.deleteReservation(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }




//    @PostMapping("/accept/{idReservation}")
//    @PreAuthorize("hasAuthority('admin:update')")
//    public ResponseEntity<String> acceptReservation(@PathVariable int idReservation) {
//        Reservation reservation = reservationService.getReservationById(idReservation);
//
//        if (reservation != null) {
//            // Accept the current reservation
//            reservation.setState(Etat.accepted);
//            reservationService.updateReservation(idReservation, reservation);
//
//            // Check for overlapping reservations and mark them as refused
//            List<Reservation> overlappingReservations = reservationService.findOverlappingReservations(reservation);
//            for (Reservation overlapping : overlappingReservations) {
//                overlapping.setState(Etat.refused);
//                reservationService.updateReservation(overlapping.getId_reservation(), overlapping);
//            }
//
//            return ResponseEntity.ok("Reservation accepted and overlapping reservations refused.");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }


//
//    @PutMapping("/accept/{idReservation}")
//    @PreAuthorize("hasAuthority('admin:update')")
//    public ResponseEntity<String> acceptReservation(@PathVariable int idReservation) {
//        Reservation reservation = reservationService.getReservationById(idReservation);
//
//        if (reservation != null) {
//            // Accept the current reservation
//            reservation.setState(Etat.accepted);
//            reservationService.updateReservation(idReservation, reservation);
//
//            // Check for overlapping reservations and mark them as refused
//            List<Reservation> overlappingReservations = reservationService.findOverlappingReservations(reservation);
//            for (Reservation overlapping : overlappingReservations) {
//                overlapping.setState(Etat.refused);
//                reservationService.updateReservation(overlapping.getId_reservation(), overlapping);
//            }
//
//            return ResponseEntity.ok("Reservation accepted and overlapping reservations refused.");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }



    @PutMapping("/accept/{idReservation}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<String> acceptReservation(@PathVariable int idReservation) {
        Reservation reservation = reservationService.getReservationById(idReservation);

        if (reservation != null) {
            // Accept the current reservation
            reservation.setState(Etat.accepted); // Update status to "accepted"
            reservationService.updateReservation(idReservation, reservation);

            // Check for overlapping reservations and mark them as refused
            List<Reservation> overlappingReservations = reservationService.findOverlappingReservations(reservation);
            for (Reservation overlapping : overlappingReservations) {
                overlapping.setState(Etat.refused); // Update status to "refused"
                reservationService.updateReservation(overlapping.getId_reservation(), overlapping);
            }

            return ResponseEntity.ok("Reservation accepted and overlapping reservations refused.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("/user/{idUser}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long idUser) {
        List<Reservation> reservations = reservationService.getReservationByIdUser(idUser);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/cabin/{idCabin}")
    public ResponseEntity<List<Reservation>> getReservationsByCabinId(@PathVariable int idCabin) {
        List<Reservation> reservations = reservationService.getReservationByIdCabin(idCabin);
        return ResponseEntity.ok(reservations);
    }


    @GetMapping("/accepted")
    public ResponseEntity<List<Reservation>> getAcceptedReservations() {
        List<Reservation> acceptedReservations = reservationService.getAcceptedReservations();
        return ResponseEntity.ok(acceptedReservations);
    }


    @GetMapping("/refused")
    public ResponseEntity<List<Reservation>> getRefusedReservations() {
        List<Reservation> refusedReservations = reservationService.getRefusedReservations();
        return ResponseEntity.ok(refusedReservations);
    }

    @GetMapping("/inProgress")
    public ResponseEntity<List<Reservation>> getInProgressReservations() {
        List<Reservation> inProgressReservations = reservationService.getInProgressReservations();
        return ResponseEntity.ok(inProgressReservations);
    }

}
