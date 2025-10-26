package org.SP2.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.SP2.dtos.PirateDTO;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "pirate")
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pirate_id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "pirate_name", nullable = false, unique = true)
    private String pirateName;

    @Column(name = "pirate_age", nullable = false)
    private Integer pirateAge;

    @Column(name = "pirate_gender", nullable = false)
    private String pirateGender;

    @Column(name = "pirate_bounty", nullable = false)
    private Long pirateBounty;

    @Column(name = "pirate_birthday", nullable = false)
    private String pirateBirthday;

    @ManyToOne
    @JoinColumn(name = "crew_id", nullable = false)
    @ToString.Exclude
    @JsonManagedReference
    private Crew crew;

    public Pirate(PirateDTO pirateDTO, Crew crew) {
        this.id = pirateDTO.getId();
        this.crew = crew;
        this.pirateName = pirateDTO.getPirateName();
        this.pirateAge = pirateDTO.getPirateAge();
        this.pirateGender = pirateDTO.getPirateGender();
        this.pirateBounty = pirateDTO.getPirateBounty();
        this.pirateBirthday = pirateDTO.getPirateBirthday();
    }

    public Pirate(String pirateName, String pirateBirthday, String pirateGender,
                  Integer pirateAge, Long pirateBounty, Crew crew) {
        this.pirateName = pirateName;
        this.pirateBirthday = pirateBirthday;
        this.pirateGender = pirateGender;
        this.pirateAge = pirateAge;
        this.pirateBounty = pirateBounty;
        this.crew = crew;
    }
}