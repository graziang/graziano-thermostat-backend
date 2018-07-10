package giuseppe.graziano.thermostat.service;

import giuseppe.graziano.thermostat.exception.NotFoundException;
import giuseppe.graziano.thermostat.model.data.SensorStats;
import giuseppe.graziano.thermostat.model.data.Measurement;
import giuseppe.graziano.thermostat.model.data.Sensor;
import giuseppe.graziano.thermostat.model.data.Thermostat;
import giuseppe.graziano.thermostat.model.repository.MeasurementRepository;
import giuseppe.graziano.thermostat.model.repository.SensorRepository;
import giuseppe.graziano.thermostat.model.repository.ThermostatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ThermostatService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ThermostatService.class);


    @Autowired
    ThermostatRepository thermostatRepository;

    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    MeasurementRepository measurementRepository;


    private List<Measurement> recentMeasurements = new ArrayList<>();


    public Thermostat initialize(){
        Thermostat td = new Thermostat("Piano superiore", "Piano con camere");
        Sensor s1 = new Sensor("Mamma e Papà", "mamma desc");
        Sensor s2 = new Sensor("Lorenza", "loo desc");
        Sensor s3 = new Sensor("Giuseppe", "loo desc");
        Sensor s4 = new Sensor("Bagno", "loo desc");
        Sensor s5 = new Sensor("Corridoio", "loo desc");

        s1.setThermostat(td);
        s2.setThermostat(td);
        s3.setThermostat(td);
        s4.setThermostat(td);
        s5.setThermostat(td);

        td.getSensors().add(s1);
        td.getSensors().add(s2);
        td.getSensors().add(s3);
        td.getSensors().add(s4);
        td.getSensors().add(s5);

        thermostatRepository.save(td);

        return td;
    }

    public Sensor addSensor(Long id, Sensor sensor) throws NotFoundException {

        Thermostat thermostat = thermostatRepository.findThermostatById(id);

        if(thermostat == null){
            String errorMessage = "Thermostat id not found: [id: " + id + "]";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        thermostat.getSensors().add(sensor);
        sensor.setThermostat(thermostat);

        this.thermostatRepository.save(thermostat);

        return sensor;
    }

    public List<Thermostat> getThermostats(){
        return thermostatRepository.findAll();
    }

    public List<Sensor> getSensors(Long id){


        if(id == null){
            return this.sensorRepository.findAll();
        }

        return this.sensorRepository.findSensorsByThermostatId(id);
    }

    public Sensor setSensorState(Long id, boolean state) throws NotFoundException {

        Sensor sensor = this.sensorRepository.findSensorById(id);

        if (sensor == null) {
            String errorMessage = "Sensor id not found: [id: " + id + "]";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        sensor.setActive(state);
        this.sensorRepository.save(sensor);
        return sensor;
    }

    public Thermostat turnThermostatOnOff(Long id, boolean state)  throws NotFoundException {

        Thermostat thermostat = this.thermostatRepository.findThermostatById(id);

        if(thermostat == null){
            String errorMessage = "Sensor id not found: [id: " + id + "]";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        thermostat.setStateOn(state);
        this.thermostatRepository.save(thermostat);
        return thermostat;

    }

    public Measurement getLastMeasurements(Long id) throws NotFoundException{


        for (Measurement m: this.recentMeasurements) {
            if (m.getSensor().getId() == id) {
                return m;

            }
        }

        String errorMessage = "No recent measurements for sensor: [id: " + id + "]";
        log.error(errorMessage);
        throw new NotFoundException(errorMessage);
    }


    public List<Measurement> getMeasurements (String dateStart, String dateEnd) {

        List<Measurement> measurements;

        if (dateStart != null && dateEnd != null) {
            measurements = this.measurementRepository.findByDateBetween(new Date(Long.valueOf(dateStart)), new Date(Long.valueOf(dateEnd)));
        } else if (dateStart != null) {
            measurements = this.measurementRepository.findByDateAfter(new Date(Long.valueOf(dateStart)));
        } else if (dateEnd != null) {
            measurements = this.measurementRepository.findByDateBefore(new Date(Long.valueOf(dateEnd)));
        } else {
            measurements = this.measurementRepository.findAll();
        }

        return measurements;
    }


    public SensorStats getMeasurementsStats (String dateStart, String dateEnd, Long id) {

        List<Measurement> measurements;

        if (dateStart != null && dateEnd != null) {
            measurements = this.measurementRepository.findByDateBetweenAndSensorId(new Date(Long.valueOf(dateStart)), new Date(Long.valueOf(dateEnd)), id);
        } else if (dateStart != null) {
            measurements = this.measurementRepository.findByDateAfterAndSensorId(new Date(Long.valueOf(dateStart)), id);
        } else if (dateEnd != null) {
            measurements = this.measurementRepository.findByDateBeforeAndSensorId(new Date(Long.valueOf(dateEnd)), id);
        } else {
            measurements = this.measurementRepository.findAll();
        }

        if(measurements == null || measurements.size() == 0){
            return null;
        }


        Measurement maxM = null;
        Measurement minM = null;
        float avgTemperature = 0;

        for (Measurement m: measurements){
            if(minM == null && maxM == null){
                maxM = m;
                minM = m;
            }

            if(m.getTemperature() < minM.getTemperature()){
                minM = m;
            }

            if(m.getTemperature() > maxM.getTemperature()){
                maxM = m;
            }

            avgTemperature += m.getTemperature();

        }

        avgTemperature = avgTemperature /((float) measurements.size());


        SensorStats stats = new SensorStats();

        stats.setDateStart(dateStart);
        stats.setDateEnd(dateEnd);
        stats.setSensor(maxM.getSensor());
        stats.setMaxMeasurement(maxM);
        stats.setMinMeasurement(minM);
        stats.setMaxTemperature(maxM.getTemperature());
        stats.setMinTemperature(minM.getTemperature());
        stats.setAvgTemperature(avgTemperature);


        return stats;
    }

    public List<Measurement> addMeasurement(Map<String, Float> measurement) {


        Date date = new Date();
        List<Sensor> sensors = this.sensorRepository.findAll();

        List<Measurement> measurements = new ArrayList<>();

        for (Sensor sensor: sensors){

            String stringID = String.valueOf(sensor.getId());
            if(measurement.containsKey(stringID) && sensor.isActive()) {
                float temperature = measurement.get(stringID);
                Measurement m = new Measurement(sensor, temperature);
                m.setDate(date);
                measurements.add(m);
            }
            else {
                // log.error("Wrong sensor ID: " + stringID);
            }
        }

        this.recentMeasurements = measurements;



        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minutes = calendar.get(Calendar.MINUTE);

        if (minutes % 15 == 0) {
            this.measurementRepository.saveAll(measurements);
        }
        return measurements;
    }



    public List<Measurement> cleanMeasurements(){


        measurementRepository.deleteAll();
        return measurementRepository.findAll();
    }


    public List<Thermostat> cleanAllTable(){

        thermostatRepository.deleteAll();
        sensorRepository.deleteAll();
        measurementRepository.deleteAll();

        return thermostatRepository.findAll();
    }



}
