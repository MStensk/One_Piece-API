package org.SP2.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "pirate")
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pirate_id", nullable = false, unique = true)
    private Integer id;

    @Setter
    @Column(name = "pirate_name", nullable = false, unique = true)
    private String pirateName;

    @Setter
    @Column(name = "pirate_age", nullable = false, unique = true)
    private Integer pirateAge;

    @Setter
    @Column(name = "pirate_gender", nullable = false, unique = true)
    private String pirateGender;

    @Setter
    @Column(name="pirate_bounty", nullable = false, unique = true)
    private Integer pirateBounty;

    @Setter
    @Column(name="pirate_birthday", nullable = false, unique = true)
    private String pirateBirthday;

    @Setter
    @ManyToOne
    @JoinColumn(name = "crew_id", nullable = false)
    private Crew crew;
}
