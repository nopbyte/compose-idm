package de.passau.uni.sec.compose.id.core.persistence.entities;

import java.io.Serializable;
import java.util.Date;

public interface IEntity extends Serializable {
    public String getId();

    public void setId(String id);

    public Date getLastModified();

    public void setLastModified(Date lastModified);

}
