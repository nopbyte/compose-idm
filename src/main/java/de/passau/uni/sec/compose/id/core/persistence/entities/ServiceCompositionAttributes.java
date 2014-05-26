package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ServiceCompositionAttributes extends AbstractEntityAttributes {

    @ManyToOne
    @JoinColumn(name = "serviceComposition_fk")
    private ServiceComposition serviceComposition;

    public ServiceComposition getServiceComposition() {
        return serviceComposition;
    }

    public void setServiceComposition(ServiceComposition serviceComposition) {
        this.serviceComposition = serviceComposition;
    }
}
