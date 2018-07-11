package giuseppe.graziano.thermostat.service;

import giuseppe.graziano.thermostat.exception.NotFoundException;
import giuseppe.graziano.thermostat.model.data.*;
import giuseppe.graziano.thermostat.model.repository.MeasurementRepository;
import giuseppe.graziano.thermostat.model.repository.SensorRepository;
import giuseppe.graziano.thermostat.model.repository.ThermostatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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


    private Map<Long, List<Measurement>> recentMeasurements = new HashMap<>();


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


        td.setManualMode(new ManualMode());
        thermostatRepository.save(td);

        return td;
    }

    public Sensor addSensor(Long id, Sensor sensor) throws NotFoundException {

        Thermostat thermostat = this.getThermostat(id);

        thermostat.getSensors().add(sensor);
        sensor.setThermostat(thermostat);

        this.thermostatRepository.save(thermostat);

        return sensor;
    }

    public List<Thermostat> getThermostats(){
        return thermostatRepository.findAll();
    }

    public Thermostat getThermostat(Long id)  throws NotFoundException {

        Thermostat thermostat = this.thermostatRepository.findThermostatById(id);

        if(thermostat == null){
            String errorMessage = "Thermostat id not found: [id: " + id + "]";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return thermostat;

    }

    public List<Sensor> getSensors(Long id){


        if(id == null){
            return this.sensorRepository.findAll();
        }

        return this.sensorRepository.findSensorsByThermostatId(id);
    }


    public Sensor getSensor(Long id) throws NotFoundException {


        Sensor sensor = this.sensorRepository.findSensorById(id);

        if (sensor == null) {
            String errorMessage = "Sensor id not found: [id: " + id + "]";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return sensor;
    }

    public Sensor setSensorState(Long id, boolean state) throws NotFoundException {

        Sensor sensor = getSensor(id);
        sensor.setActive(state);
        this.sensorRepository.save(sensor);
        return sensor;
    }

    public Thermostat turnThermostatOnOff(Long id, boolean state)  throws NotFoundException {

        Thermostat thermostat = getThermostat(id);
        thermostat.setStateOn(state);
        this.thermostatRepository.save(thermostat);
        return thermostat;

    }

    public Thermostat setThermostatTemperature(Long id, float temperature)  throws NotFoundException {

        Thermostat thermostat = getThermostat(id);
        thermostat.setTemperature(temperature);
        this.thermostatRepository.save(thermostat);
        return thermostat;

    }



    public List<Measurement> getLastMeasurements(Long id) throws NotFoundException{

        Thermostat thermostat = getThermostat(id);

        if(this.recentMeasurements.containsKey(thermostat.getId())){
           return this.recentMeasurements.get(thermostat.getId());
        }

        String errorMessage = "No recent measurements for thermostat: [id: " + id + "]";
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

    public List<Measurement> addMeasurement(Long id, Map<String, Float> measurement) throws NotFoundException {

        Thermostat thermostat = getThermostat(id);

        Date date = new Date();
        Set<Sensor> sensors = thermostat.getSensors();

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

        this.recentMeasurements.put(id, measurements);



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

    public Thermostat setThermostatMode(Long id, String mode) throws NotFoundException{

        Thermostat thermostat = this.getThermostat(id);
        if(Thermostat.MANUAL_MODE.equals(mode)){
            thermostat.setMode(Thermostat.MANUAL_MODE);
        }

        this.thermostatRepository.save(thermostat);
        return thermostat;
    }

    public Thermostat setThermostatCalulateSensor(Long id, boolean avg, Long sensorId) throws NotFoundException{

        Thermostat thermostat = this.getThermostat(id);
        thermostat.getManualMode().setAvg(avg);

        Sensor sensor = this.getSensor(sensorId);
        thermostat.getManualMode().setSensorId(sensor.getId());

        this.thermostatRepository.save(thermostat);
        return thermostat;
    }


    @Scheduled(fixedDelay = 60 * 1000)
    private void calculate(){

        List<Thermostat> thermostats = getThermostats();

        for (Thermostat thermostat: thermostats){

            ManualMode manualMode = thermostat.getManualMode();
            if(manualMode.equals(Thermostat.MANUAL_MODE)){
              //  if(thermostat.isActive()){

                    List<Measurement> measurements = new ArrayList<>();

                    if(this.recentMeasurements.containsKey(thermostat.getId())){
                        measurements = this.recentMeasurements.get(thermostat.getId());
                    }

                    float avgTemperature = 0;
                    float sensorTemperature = 0;
                    for (Measurement measurement: measurements) {

                        if(measurement.getSensor().getId() == manualMode.getSensorId()){
                            sensorTemperature = measurement.getTemperature();
                        }

                        avgTemperature += measurement.getTemperature();

                    }

                    avgTemperature = avgTemperature / measurements.size();

                    if(manualMode.isAvg()){
                        thermostat.setStateOn(avgTemperature < thermostat.getTemperature());
                    }
                    else {
                        thermostat.setStateOn(sensorTemperature < thermostat.getTemperature());
                    }

               // }
            }
            this.thermostatRepository.save(thermostat);
        }

    }

}
