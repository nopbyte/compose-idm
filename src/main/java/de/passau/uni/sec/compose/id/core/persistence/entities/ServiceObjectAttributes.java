package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ServiceObjectAttributes extends AbstractEntityAttributes {

    @ManyToOne
    @JoinColumn(name = "serviceObject_fk")
    private ServiceObject serviceObject;

    public ServiceObject getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(ServiceObject serviceObject) {
        this.serviceObject = serviceObject;
    }
}
