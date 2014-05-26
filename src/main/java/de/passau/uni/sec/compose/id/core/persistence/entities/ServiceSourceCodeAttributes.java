package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ServiceSourceCodeAttributes extends AbstractEntityAttributes {

    @ManyToOne
    @JoinColumn(name = "serviceSourceCode_fk")
    private ServiceSourceCode serviceSourceCode;

    public ServiceSourceCode getServiceSourceCode() {
        return serviceSourceCode;
    }

    public void setServiceSourceCode(ServiceSourceCode serviceSourceCode) {
        this.serviceSourceCode = serviceSourceCode;
    }
}
