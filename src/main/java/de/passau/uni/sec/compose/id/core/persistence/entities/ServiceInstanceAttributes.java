package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ServiceInstanceAttributes extends AbstractEntityAttributes {

    @ManyToOne
    @JoinColumn(name = "serviceInstance_fk")
    private ServiceInstance serviceInstance;

    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }
}
