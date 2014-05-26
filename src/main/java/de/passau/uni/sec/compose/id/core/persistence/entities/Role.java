package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Role extends AbstractEntity {

    public static final String ADMIN = "ADMIN";
    public static final String DEVELOPER = "DEVELOPER";
    public static final String SERVICE_PROVIDER = "SERVICE_PROVIDER";
    public static final String OBJECT_PROVIDER = "OBJECT_PROVIDER";

	@Column(nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
