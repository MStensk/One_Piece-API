package org.SP2.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Setter;

public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "crew_name", nullable = false, unique = true)
    private String crewName;

    @Setter
    @Column(name = "crew_ship", nullable = false, unique = true)
    private String crewShip;

    @Setter
    @Column(name = "crew_jolly_roger", nullable = false, unique = true)
    private String crewJollyRoger;

    @Setter
    @Column(name = "crew_captain", nullable = false, unique = true)
    private String crewCaptain;

}
