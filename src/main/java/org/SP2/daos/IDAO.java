package org.SP2.daos;

import org.SP2.entities.Crew;
import org.SP2.entities.Pirate;

import java.util.List;
import java.util.Optional;

public interface IDAO {
    // === CRUD ===
    List<Crew> getAllCrews();
    Optional<Crew> getCrewById(int id);
    Crew createCrew(Crew crew);
    Crew updateCrew(Crew crew);
    boolean deleteCrew(int id);

    // === Manage pirates ===
    Crew addPirate(Crew crew, Pirate pirate);
    Crew removePirate(Crew crew, Pirate pirate);
    List<Pirate> getPiratesForCrew(Crew crew);

}
