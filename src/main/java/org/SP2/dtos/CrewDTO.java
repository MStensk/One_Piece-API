package org.SP2.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.SP2.entities.Pirate;
import org.SP2.entities.Crew;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrewDTO {

    private Integer id;
    private String name;
    private String ship;
    private String jollyRoger;
    private String captain;
    private List<PirateDTO> pirates;

    public CrewDTO(Crew crew) {
        this.id = crew.getId();
        this.name = crew.getCrewName();
        this.ship = crew.getCrewShip();
        this.jollyRoger = crew.getCrewJollyRoger();
        this.captain = crew.getCrewCaptain();
        this.pirates = crew.getPirates().stream().map(PirateDTO::new).toList();

    }

    public CrewDTO(Integer id, String name, String ship, String jollyRoger, String captain, List<PirateDTO> pirates) {
        this.id = id;
        this.name = name;
        this.ship = ship;
        this.jollyRoger = jollyRoger;
        this.captain = captain;
        this.pirates = pirates;
    }

    public static List<CrewDTO> toDTOList(List<Crew> resultList) {
        return resultList.stream().map(CrewDTO::new).toList();
    }

    public static List<Crew> toEntityList(List<CrewDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> {
                    Crew crew = new Crew();
                    crew.setId(dto.getId());
                    crew.setCrewName(dto.getName());
                    crew.setCrewShip(dto.getShip());
                    crew.setCrewJollyRoger(dto.getJollyRoger());

                    if (dto.getPirates() != null) {
                        dto.getPirates().forEach(pirateDTO -> {
                            Pirate pirate = new Pirate(pirateDTO, crew);
                            crew.getPirates().add(pirate);
                        });
                    }

                    return crew;
                }).toList();
    }

    public Crew toEntity() {
        Crew crew = new Crew();
        crew.setId(this.id);
        crew.setCrewName(this.name);
        crew.setCrewShip(this.ship);
        crew.setCrewJollyRoger(this.jollyRoger);
        crew.setCrewCaptain(this.captain);

        if (this.pirates != null) {
            this.pirates.forEach(pirateDTO -> {
                Pirate pirate = new Pirate(pirateDTO, crew);
                crew.getPirates().add(pirate);
            });
        }
        return crew;
    }

}
