package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntityAttributes extends AbstractEntity {

    private String type;

    private String name;

    private String value;

    private boolean verified;

    private String authority;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
