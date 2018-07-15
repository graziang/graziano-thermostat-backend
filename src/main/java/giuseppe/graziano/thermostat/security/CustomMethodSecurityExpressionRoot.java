package giuseppe.graziano.thermostat.security;

import giuseppe.graziano.thermostat.model.data.Thermostat;
import giuseppe.graziano.thermostat.model.data.User;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    //
    public boolean hasThermostat(Long OrganizationId) {
        final User user = ((MyUserPrincipal) this.getPrincipal()).getUser();

        for (Thermostat thermostat: user.getThermostats()) {
            if(thermostat.getId() == user.getId()){
                return true;
            }
        }

        return false;
    }

    //

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }

}
