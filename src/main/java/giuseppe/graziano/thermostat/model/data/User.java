package giuseppe.graziano.thermostat.model.data;


import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_table")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private boolean isAdmin;

    private Long selectedThermostatId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_thermostats", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "thermostat_id", referencedColumnName = "id"))
    private Set<Thermostat> thermostats;

}