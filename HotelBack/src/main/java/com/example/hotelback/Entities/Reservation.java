package com.example.hotelback.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.hotelback.Entities.User;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_reservation;
    @Column
    private Date dateDeb;
    @Column
    private Date dateFin;
    @Column
    private Etat state;
    @Column
    private float totalPrice;



    @OneToOne

    @JsonIgnore
    @JoinColumn(name = "idUser")
    private User client;


//    @OneToOne
//
//    @JsonIgnore
////    @JoinColumn(name = "idcabin")
////    private Cabin cabin;


    @ManyToOne
    @JoinColumn(name = "idcabin")
    private Cabin cabin;



}
