package giuseppe.graziano.thermostat.service;

import giuseppe.graziano.thermostat.exception.NotFoundException;
import giuseppe.graziano.thermostat.model.data.*;
import giuseppe.graziano.thermostat.model.repository.*;
import giuseppe.graziano.thermostat.security.MyUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    SourceRepository sourceRepository;


    @Autowired
    private PasswordEncoder encoder;

    private Map<Long, List<Measurement>> recentMeasurements = new HashMap<>();

    private int lastMinuteUpdate = 0;
    private int lastHourUpdate = 0;


 //   @PostConstruct
    public void test(){


        Thermostat thermostat = thermostatRepository.findThermostatById(1L);


        Program program = new Program();
        program.setName("testporg");
        program.setWeekDay(DayOfWeek.FRIDAY);
        program.setStartTime(LocalTime.now());
        program.setProgramMode(thermostat.getProgramMode());

        program.setProgramMode(thermostat.getProgramMode());
        thermostat.getProgramMode().getPrograms().add(program);
        this.thermostatRepository.save(thermostat);

    }

    //@PostConstruct
    public Thermostat initialize(){
        Thermostat td = new Thermostat("Piano superiore", "Piano con camere");
        td.setActive(true);
        Sensor s1 = new Sensor("Mamma e Papà", "mamma desc");
        Sensor s2 = new Sensor("Lorenza", "Loo desc");
        s2.setDeviceId("28ff9680b21704aa");
        s2.setActive(true);
        Sensor s3 = new Sensor("Giuseppe", "Giu desc");
        Sensor s4 = new Sensor("Bagno", "Bagno grande");
        s4.setDeviceId("28ff4b25a31704d1");
        s4.setActive(true);
        Sensor s5 = new Sensor("Corridoio", "Corridoio piano di sopra");
        s5.setDeviceId("28ff6a80b217042d");
        s5.setActive(true);

        Source sorgente1 = new Source("Metano");
        Source sorgente2 = new Source("Pellet");

        sorgente1.setThermostat(td);
        sorgente2.setThermostat(td);

        td.setSource(sorgente1);

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

        td.getSources().add(sorgente1);
        td.getSources().add(sorgente2);


        td.setManualMode(new ManualMode());
        td.setProgramMode(new ProgramMode());
        thermostatRepository.save(td);

        User user = new User();
        user.setUsername("admin");
        user.setPassword(encoder.encode("ciaociao"));
        user.setAdmin(true);
        userRepository.save(user);





        Thermostat td1 = new Thermostat("Piano inferiore", "Piano con camere");
        td1.setManualMode(new ManualMode());
        // td1.getSensors().add(s2);
        thermostatRepository.save(td1);

        Set<Thermostat> terms = new HashSet<>();
        terms.add(td);
        terms.add(td1);

        User userPeps = new User();
        userPeps.setUsername("peps");
        userPeps.setPassword(encoder.encode("ciaociao"));
        userPeps.setThermostats(terms);
        userRepository.save(userPeps);

        terms = new HashSet<>();
        terms.add(td);

        User userThermostat = new User();
        userThermostat.setUsername("grazianotermostato");
        userThermostat.setPassword(encoder.encode("grazianotermostato2018"));
        userThermostat.setThermostats(terms);
        userRepository.save(userThermostat);

        return td;
    }



    public Set<Program> getPrograms(Long id) throws NotFoundException {

        Thermostat thermostat = this.getThermostat(id);

        return thermostat.getProgramMode().getPrograms();
    }

    public Program addProgram(Long id, Program program) throws NotFoundException {

        Thermostat thermostat = this.getThermostat(id);

        program.setProgramMode(thermostat.getProgramMode());
        thermostat.getProgramMode().getPrograms().add(program);
        this.thermostatRepository.save(thermostat);
        return program;
    }

    public Program updateProgram(Long id, Program program) throws NotFoundException {

        Thermostat thermostat = this.getThermostat(id);

        List<Program> programs = new ArrayList(thermostat.getProgramMode().getPrograms());

        if(programs.indexOf(program) == -1){
            String errorMessage = "Program id not found: [id: " + id + "]";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        Program oldProgram = programs.get(programs.indexOf(program));
        oldProgram.setName(program.getName());
        oldProgram.setDescription(program.getDescription());
        oldProgram.setActive(program.isActive());
        oldProgram.setWeekDay(program.getWeekDay());
        oldProgram.setStartTime(program.getStartTime());
        oldProgram.setEndTime(program.getEndTime());
        this.thermostatRepository.save(thermostat);

        return program;
    }

    public Program deleteProgram(Long id, Program program) throws NotFoundException {
        Thermostat thermostat = this.getThermostat(id);

        List<Program> programs = new ArrayList(thermostat.getProgramMode().getPrograms());
        programs.remove(program);
        this.thermostatRepository.save(thermostat);
        return program;
    }



    public User getUser(String username) throws NotFoundException {

        User user = userRepository.findByUsername(username);

        Set<Thermostat> thermostatList = user.getThermostats();

        for (Thermostat thermostat: thermostatList){
            Set<Sensor> sensors = thermostat.getSensors();
            sensors.stream().sorted(new Comparator<Sensor>() {
                @Override
                public int compare(Sensor s1, Sensor s2) {
                    return s1.getName().compareTo(s2.getName());
                }
            });
            thermostat.setSensors(sensors);
        }

        if(user == null){
            throw new NotFoundException("User not found: [username: " + username + "]");
        }

        return user;
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

    public Thermostat updateThermostat(Long id, Thermostat thermostat) throws NotFoundException {

        Thermostat foundThermostat = getThermostat(id);

        foundThermostat.setActive(thermostat.isActive());
        foundThermostat.setMode(thermostat.getMode());
        foundThermostat.setTemperature(thermostat.getTemperature());
        foundThermostat.setManualMode(thermostat.getManualMode());

        if(!thermostat.isActive()){
            foundThermostat.setStateOn(false);
        }

        this.thermostatRepository.save(foundThermostat);
        this.calculate();

        return foundThermostat;
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

    public Sensor setSensorDeviceId(Long id,Long sensor_id, String device_id) throws NotFoundException {

        Thermostat thermostat = this.getThermostat(id);
        Sensor sensor = getSensor(sensor_id);
        sensor.setDeviceId(device_id);
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

        if(!thermostat.getSensors().contains(getSensor(sensor_id))){
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
            measurements = this.measurementRepository.findBySensorId(sensor_id);
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
            throw new NotFoundException( "No measurements found: [sensor_id:" + sensor_id + "]");
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

            String stringDeviceID = String.valueOf(sensor.getDeviceId());
            if(measurement.containsKey(stringDeviceID) && sensor.isActive()) {
                float temperature = measurement.get(stringDeviceID);
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
        //int minutes = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        /*if (minutes % 60 == 0 && minutes != lastMinuteUpdate) {
            this.measurementRepository.saveAll(measurements);
            lastMinuteUpdate = minutes;
        }*/

        if (hour != lastHourUpdate) {
            this.measurementRepository.saveAll(measurements);
            lastHourUpdate = hour;
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

    public Map<String, Object> getThermostatMap(String username) throws NotFoundException {
        Map<String, Object> thermostatMap  = new HashMap<>();
        User user = getUser(username);
        if(user.getSelectedThermostatId() != null) {
            Thermostat thermostat = this.thermostatRepository.findThermostatById(user.getSelectedThermostatId());
            List<String> sensorsIds = new ArrayList<>();
            if(thermostat.getSensors() != null){
                for (Sensor sensor: thermostat.getSensors()){
                    if(sensor.isActive()){
                        sensorsIds.add(String.valueOf(sensor.getId()));
                    }
                }
                thermostatMap.put("thermostat", String.valueOf(thermostat.getId()));
                thermostatMap.put("sensors", sensorsIds);
                thermostatMap.put("source", String.valueOf(thermostat.getSource().getId()));
                thermostatMap.put("state", thermostat.isStateOn());
            }
        }
        return  thermostatMap;
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
    public void cleanLast24HMeasurements() {
        Long timestampFrom = System.currentTimeMillis() - (1000*60*60*24);
        measurementRepository.deleteAllByDateBefore(new Date(timestampFrom));
    }

    @Transactional
    @Scheduled(fixedRate = 60 * 1000)
    public void calculate(){

        List<Thermostat> thermostats = getThermostats();

        for (Thermostat thermostat: thermostats){
            thermostat.setStateOn(false);

            ManualMode manualMode = thermostat.getManualMode();
            if(thermostat.isActive()){
                if(thermostat.getMode().equals(Thermostat.MANUAL_MODE)){

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
                    else if(manualMode.isAvg()){
                        thermostat.setStateOn(avgTemperature < thermostat.getTemperature());
                    }
                    else {
                        thermostat.setStateOn(sensorTemperature < thermostat.getTemperature());
                    }

                }

                else if(thermostat.getMode().equals(Thermostat.PROGRAM_MODE)){
                    ProgramMode mode = thermostat.getProgramMode();

                    for (Program program: mode.getPrograms()) {
                        if(isProgramOn(program)){
                            thermostat.setStateOn(true);
                            thermostat.setSource(sourceRepository.findSourceById(program.getSourceId()));
                            program.setSourceOn(true);
                            break;
                        }
                        else {
                            program.setSourceOn(false);
                        }
                    }
                }
            }

            this.thermostatRepository.save(thermostat);
        }

    }


    private boolean isProgramOn(Program program){

        if (program.isActive()) {
            LocalTime now = LocalTime.now();
            boolean isSameDay = LocalDate.now().getDayOfWeek().equals(program.getWeekDay());
            boolean isAfterStart = now.isAfter(program.getStartTime());
            boolean isBeforeEnd = now.isBefore(program.getEndTime());
            return isSameDay && isAfterStart && isBeforeEnd;
        }
        return false;
    }
}
