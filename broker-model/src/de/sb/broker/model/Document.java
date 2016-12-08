package de.sb.broker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;


@Entity
@Table(name="Document", schema="brokerDB")
@PrimaryKeyJoinColumn(name="documentIdentity")
public class Document extends BaseEntity {
	/* TODO implement class
	 * documentIdentity BIGINT NOT NULL,
	 * content MEDIUMBLOB NOT NULL,
	 * contentHash BINARY(32) NOT NULL,
	 * contentType CHAR(64) NOT NULL,
	 */
	
	    
	    @Column(nullable=false, updatable=true)
		@NotNull
		@XmlElement
		@Size
	    private String type;	
		
		@Column(updatable=false, nullable=false, insertable=true)
		@NotNull
		@Size(min=1)
	    private byte[] content;
		
		@Column (updatable=false, nullable=false, insertable=true)
		@NotNull
		@Size(min=32, max=32)
	    private byte[] hash;
	    
		
	    public String getType() {
	        return type;
	    }
	    
	    public void setType(String type) {
	    	this.type = type;
	    }
	    
	    public byte[] getContent() {
			return content;
		}

	    public byte[] getHash() {
	        return hash;
	    }
	}
