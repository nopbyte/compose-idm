package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;

@Entity
public class Global extends AbstractEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7285545921282723216L;

    @Column
    private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
    
	
}
