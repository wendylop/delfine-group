package de.sb.broker.model;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.sun.istack.internal.NotNull;

@Entity
@Table(schema = "broker", name = "Person")
@PrimaryKeyJoinColumn(name = "personIdentity")
public class Person extends BaseEntity{
	
	@Column(nullable = false, updatable = false, length = 16, unique = true)
	@Size (min=1, max=16)
	@NotNull
	private String alias;
	
	@Column(nullable = false, updatable = true, length = 32)
	@Size (min=32, max=32)
	@NotNull
	private byte[] passwordHash;
	
	@Column(name = "groupAlias", nullable = false, updatable = true)
	@Enumerated(EnumType.STRING)
	@NotNull
	private Group group;
	
	@Embedded
	@Valid
	@NotNull
	private final Name name;
	
	@Embedded
	@Valid
	@NotNull
	private final Address address;
	
	@Embedded
	@Valid
	@NotNull
	private final Contact contact;
	
	@OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE)
	//@NotNull
	private final Set<Auction> auctions;
	
	@OneToMany(mappedBy = "bidder", cascade = CascadeType.REMOVE)
	//@NotNull
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
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
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
	public Address getAddress(){
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

