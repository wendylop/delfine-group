package de.sb.broker.model;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="PERSON",schema="")
@PrimaryKeyJoinColumn
public class Person extends BaseEntity{
	
	@NotNull
	@Size(min=1,max=16)
	private String alias;
	
	@NotNull
	@Size(min=32,max=32)
	private byte[] passwordHash;
	
	@NotNull
	private Group group;
	
	@Valid
	private final Name name;
	
	@Valid
	private final Address address;

	@Valid
	private final Contact contact;
	
	private final Set<Auction> auctions;
	private final Set<Bid> bids;
	
	public Person(){
		
		this.alias = "";
		this.passwordHash = passwordHash("");
		this.group = Group.USER;
		this.name = new Name();
		this.address = new Address();
		this.contact = new Contact();
		this.auctions  = new HashSet<Auction>();
		this.bids = new HashSet<Bid>();
		
	}

			
	public static enum Group {
		ADMIN, USER
	}

	public String getAlias(){
		return alias;
	}
	
	//TODO set alias
	
	public byte[] getPasswordHash() {
		return passwordHash;
	}
	
	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public Group getGroup(){
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}

	public Name getName(){
		return this.name;
	}
	public Address getAdress(){
		return address;
	}
	
	public Contact getContact(){
		return contact;
	}
		
	public static byte[] passwordHash(String password){
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				return md.digest(password.getBytes("UTF-8"));
			} catch (NoSuchAlgorithmException | UnsupportedEncodingException e ) {
				throw new AssertionError(e);
			}
			
	}

	

}

