package org.SP2.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.SP2.dtos.CrewDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table (name = "crew")
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "crew_name", nullable = false, unique = true)
    private String crewName;

    @Setter
    @Column(name = "crew_ship", nullable = false)
    private String crewShip;

    @Setter
    @Column(name = "crew_jolly_roger", nullable = false, unique = true)
    private String crewJollyRoger;

    @Setter
    @Column(name = "crew_captain", nullable = false, unique = true)
    private String crewCaptain;

    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonManagedReference
    private List<Pirate> pirates = new ArrayList<>();

    public Crew(CrewDTO crewDTO) {
        this.id = crewDTO.getId();
        this.crewName = crewDTO.getName();
        this.crewShip = crewDTO.getShip();
        this.crewCaptain = crewDTO.getCaptain();
        this.crewJollyRoger = crewDTO.getJollyRoger();
        this.pirates = crewDTO.getPirates().stream()
                .map(pirateDTO -> new Pirate(pirateDTO, this))
                .collect(Collectors.toList());
    }

}
