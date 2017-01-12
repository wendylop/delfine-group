package de.sb.broker.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@Entity
@Table(name="Document", schema="broker")
@PrimaryKeyJoinColumn(name="documentIdentity")
@XmlRootElement
@XmlType
public class Document extends BaseEntity {	
	    
	    @Column(nullable=false, updatable=true)
		@NotNull
		@XmlElement
	    private String contentType;	
		
		@Column(updatable=false, nullable=false, insertable=true)
		@NotNull
	    private byte[] content;
		
		@Column (updatable=false, nullable=false, insertable=true)
		@NotNull
		@Size(min=32, max=32)
	    private byte[] contentHash;
		
		public Document() {}
		
		public Document(byte[] content){
			this.content = content;
			this.contentHash = contentHash(content);
		}
	    
		
	    public String getType() {
	        return contentType;
	    }
	    
	    public void setType(String type) {
	    	this.contentType = type;
	    }
	    
	    public byte[] getContent() {
			return content;
		}

	    public byte[] getHash() {
	        return contentHash;
	    }
	    
		public static byte[] contentHash(byte[] content){
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				return md.digest(content);
			} catch (NoSuchAlgorithmException e ) {
				throw new AssertionError(e);
			}
			
	}
	}
