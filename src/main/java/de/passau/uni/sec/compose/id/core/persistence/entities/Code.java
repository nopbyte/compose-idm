package de.passau.uni.sec.compose.id.core.persistence.entities;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedList;

@Entity
public class Code extends AbstractEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7285545921282723216L;

    @Column
    private String type;

    @Column
    private String code;
    
    @Column
    private String reference;

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getReference()
	{
		return reference;
	}

	public void setReference(String reference)
	{
		this.reference = reference;
	}
    
    
    
	
}
