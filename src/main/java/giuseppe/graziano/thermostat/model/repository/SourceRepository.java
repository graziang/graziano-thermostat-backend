package giuseppe.graziano.thermostat.model.repository;

import giuseppe.graziano.thermostat.model.data.Source;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceRepository extends JpaRepository<Source, Integer> {

    Source findSourceById(Long id);

}