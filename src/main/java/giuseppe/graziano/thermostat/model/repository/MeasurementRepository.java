package giuseppe.graziano.thermostat.model.repository;

import giuseppe.graziano.thermostat.model.data.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {

    List<Measurement> findByDateBetween(Date startDate, Date endDate);

    List<Measurement> findByDateAfter(Date startDate);

    List<Measurement> findByDateBefore(Date endDate);


    List<Measurement> findByDateBetweenAndSensorId(Date startDate, Date endDate, Long id);

    List<Measurement> findByDateAfterAndSensorId(Date startDate, Long id);

    List<Measurement> findByDateBeforeAndSensorId(Date endDate, Long id);

    List<Measurement> findBySensorId(Long id);

    List<Measurement> deleteAllByDateBefore(Date date);

    Measurement findFirstBySensorIdOrderByDateDesc(Long id);

}