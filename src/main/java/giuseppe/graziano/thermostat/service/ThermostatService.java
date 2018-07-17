package giuseppe.graziano.thermostat.service;

import giuseppe.graziano.thermostat.exception.NotFoundException;
import giuseppe.graziano.thermostat.model.data.*;
import giuseppe.graziano.thermostat.model.repository.MeasurementRepository;
import giuseppe.graziano.thermostat.model.repository.SensorRepository;
import giuseppe.graziano.thermostat.model.repository.ThermostatRepository;
import giuseppe.graziano.thermostat.model.repository.UserRepository;
import giuseppe.graziano.thermostat.security.MyUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    MyUserDetailsService userDetailsService;


    @Autowired
    private PasswordEncoder encoder;

    private Map<Long, List<Measurement>> recentMeasurements = new HashMap<>();


    //@PostConstruct
    public Thermostat initialize(){
        Thermostat td = new Thermostat("Piano superiore", "Piano con camere");
        Sensor s1 = new Sensor("Mamma e Papà", "mamma desc");
        Sensor s2 = new Sensor("Lorenza", "Loo desc");
        Sensor s3 = new Sensor("Giuseppe", "Giu desc");
        Sensor s4 = new Sensor("Bagno", "Bagno grande");
        Sensor s5 = new Sensor("Corridoio", "Corridoio piano di sopra");

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

        User user = new User();
        user.setUsername("admin");
        user.setPassword(encoder.encode("ciaociao"));
        user.setAdmin(true);
        userRepository.save(user);
        return td;
    }

    public User createUser(User user){

        User userOld = userRepository.findByUsername(user.getUsername());

        if(userOld != null){
            throw new UsernameNotFoundException("User already exist");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User addThermostatToUser(String username, Long id) throws NotFoundException {

        MyUserPrincipal userPrincipal = (MyUserPrincipal) this.userDetailsService.loadUserByUsername(username);
        Thermostat thermostat = this.getThermostat(id);
        User user =  userPrincipal.getUser();
        user.getThermostats().add(thermostat);
        userRepository.save(user);
        return user;
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

    public List<Thermostat> getThermostatsByUser(String username){

        MyUserPrincipal userPrincipal = (MyUserPrincipal) this.userDetailsService.loadUserByUsername(username);
        User user =  userPrincipal.getUser();
        return new ArrayList<>(user.getThermostats());
    }

    public Thermostat setThermostatState(Long id, boolean state) throws NotFoundException {

        Thermostat thermostat = getThermostat(id);
        thermostat.setActive(state);

        if(!thermostat.isActive()){
            thermostat.setStateOn(false);
        }

        this.thermostatRepository.save(thermostat);
        return thermostat;
    }

    public List<Sensor> getSensors(Long id) throws NotFoundException {

        Thermostat thermostat = getThermostat(id);
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

    public Sensor setSensorState(Long id,Long sensor_id, boolean state) throws NotFoundException {

        Thermostat thermostat = this.getThermostat(id);
        Sensor sensor = getSensor(sensor_id);
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

    private List<Measurement> getMeasurementsFromSensorId(Long id, Long sensor_id, String dateStart, String dateEnd) throws NotFoundException {

        Thermostat thermostat = getThermostat(id);

        if(thermostat.getSensors().contains(getSensor(sensor_id))){
            return new ArrayList<>();
        }

        List<Measurement> measurements;
        if (dateStart != null && dateEnd != null) {
            measurements = this.measurementRepository.findByDateBetweenAndSensorId(new Date(Long.valueOf(dateStart)), new Date(Long.valueOf(dateEnd)), sensor_id);
        } else if (dateStart != null) {
            measurements = this.measurementRepository.findByDateAfterAndSensorId(new Date(Long.valueOf(dateStart)), sensor_id);
        } else if (dateEnd != null) {
            measurements = this.measurementRepository.findByDateBeforeAndSensorId(new Date(Long.valueOf(dateEnd)), sensor_id);
        } else {
            measurements = this.measurementRepository.findAll();
        }
        return measurements;
    }


    public List<Measurement> getMeasurements (Long id, Long sensor_id, String dateStart, String dateEnd) throws NotFoundException {

        List<Measurement> measurements;


        measurements = getMeasurementsFromSensorId(id, sensor_id, dateStart, dateEnd);



        return measurements;
    }




    public SensorStats getMeasurementsStats (Long id, Long sensor_id, String dateStart, String dateEnd) throws NotFoundException {


        List<Measurement> measurements = getMeasurementsFromSensorId(id, sensor_id, dateStart, dateEnd);

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

        this.calculate();

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


    public User selectUserThermostat(String username, Long id) throws NotFoundException {

        MyUserPrincipal userPrincipal = (MyUserPrincipal) this.userDetailsService.loadUserByUsername(username);
        Thermostat thermostat = this.getThermostat(id);
        User user =  userPrincipal.getUser();
        user.setSelectedThermostatId(thermostat.getId());
        userRepository.save(user);
        return user;
    }

    public Thermostat setThermostatCalulateSensor(Long id, boolean avg, Long sensorId) throws NotFoundException{

        Thermostat thermostat = this.getThermostat(id);
        thermostat.getManualMode().setAvg(avg);

        Sensor sensor = this.getSensor(sensorId);
        thermostat.getManualMode().setSensorId(sensor.getId());

        this.thermostatRepository.save(thermostat);
        return thermostat;
    }


    @Transactional
    @Scheduled(fixedRate = 60 * 1000)
    public void calculate(){

        List<Thermostat> thermostats = getThermostats();

        for (Thermostat thermostat: thermostats){

            ManualMode manualMode = thermostat.getManualMode();
            if(thermostat.getMode().equals(Thermostat.MANUAL_MODE)){
                if(thermostat.isActive()){

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

                    avgTemperature = avgTemperature / ((float) measurements.size());

                    if(measurements.size() == 0){
                        thermostat.setStateOn(false);
                    }
                    if(manualMode.isAvg()){
                        thermostat.setStateOn(avgTemperature < thermostat.getTemperature());
                    }
                    else {
                        thermostat.setStateOn(sensorTemperature < thermostat.getTemperature());
                    }

                }
            }
            this.thermostatRepository.save(thermostat);
        }

    }

}
