package org.SP2.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.SP2.entities.Crew;
import org.SP2.entities.Pirate;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PirateDTO {
    private Integer id;
    private Integer crewId;
    private String pirateName;
    private String pirateBirthday;
    private String pirateGender;
    private Integer pirateAge;
    private Long pirateBounty;


    public PirateDTO(Pirate pirate) {
        this.id = pirate.getId();
        this.crewId= pirate.getCrew() != null ? pirate.getCrew().getId() : null;
        this.pirateName = pirate.getPirateName();
        this.pirateBirthday = pirate.getPirateBirthday();
        this.pirateGender = pirate.getPirateGender();
        this.pirateAge = pirate.getPirateAge();
        this.pirateBounty = pirate.getPirateBounty();

    }

    public PirateDTO(Integer id, Integer crewId, String pirateName, String pirateBirthday,
                   String pirateGender, Integer pirateAge, Long pirateBounty) {
        this.id = id;
        this.crewId = crewId;
        this.pirateName = pirateName;
        this.pirateBirthday = pirateBirthday;
        this.pirateGender = pirateGender;
        this.pirateAge = pirateAge;
        this.pirateBounty = pirateBounty;
    }

    public static List<PirateDTO> toDTOList(List<Pirate> resultList) {
        return resultList.stream().map(PirateDTO::new).toList();
    }

    public static List<Pirate> toEntityList(List<PirateDTO> dtoList, Crew crew) {
        return dtoList.stream()
                .map(dto -> new Pirate(dto, crew)) // reuse your constructor
                .toList();
    }

    public Pirate toEntity(Crew crew) {
        return new Pirate(this, crew);
    }

}
