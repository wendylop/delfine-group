package de.sb.broker.model;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.junit.Assert;

public class AuctionEntityTest extends EntityTest{
	private EntityManager em;
	private Validator validator;
	
	Set<ConstraintViolation<Auction>> constrainViolations;
	
	@Test
	public void testConstraints()
	{		
		validator = this.getEntityValidatorFactory().getValidator();
		//create valid person
		Person seller = new Person();
		seller.setAlias("Batman");
		seller.getName().setFamily("Bruce");
		seller.getName().setGiven("Wayne");
		seller.getAdress().setStreet("Wayne Street 1");
		seller.getAdress().setCity("Gotham");
		seller.getAdress().setPostCode("30456");
		seller.getContact().setEmail("bruce.wayne@wayne-enterprise.com");
		seller.getContact().setPhone("01506060601");
		
		Auction auction = new Auction(seller);
		auction.setTitle("Batarang for sale");
		auction.setDescription("Selling brand new Batarang");
		//auction.setSeller(seller);
		auction.setAskingPrice(8000l);
		//auction.setClosureTimestamp(System.currentTimeMillis() +30*24*60*60*1000);
		auction.setUnitCount((short) 1);
		
		constrainViolations = validator.validate(auction);
		//check if there are errors - should be 0
		assertEquals(constrainViolations.size(), 0);
		
		//set bad values
		auction.setTitle("");
		auction.setDescription("");
		//auction.setSeller(null);
		
		constrainViolations = validator.validate(auction);
		//check if there are errors - should be 3
		assertEquals(constrainViolations.size(), 3);
	}
	
	@Test
	public void testLifeCycle()
	{
		em = this.getEntityManagerFactory().createEntityManager();
		try{
			em.getTransaction().begin();
					
			//get a Person from Database
			Person person = em.find(Person.class, 1l);
			Assert.assertNotNull(person);
			
			Auction auction = new Auction(person);
			auction.setTitle("Batarang for sale");
			auction.setDescription("Selling brand new Batarang");
			//auction.setSeller(person);
			auction.setAskingPrice(8000l);
			//auction.setClosureTimestamp(System.currentTimeMillis() +30*24*60*60*1000);
			auction.setUnitCount((short) 1);
			
			em.persist(auction);
			em.getTransaction().commit();
			this.getWasteBasket().add(auction.getIdentity());
			Assert.assertNotEquals(0, auction.getIdentity());
			
			//test if Entity exists in DB
			//TODO "find" benutzen
			auction = em.getReference(Auction.class, auction.getIdentity());
			assertEquals(auction.getTitle(), "Batarang for sale");
			
			em.getTransaction().begin();
			
			//TODO "update" Testfall. immer clear benutzen
			
			em.remove(auction);
			em.getTransaction().commit();
	
			auction = em.find(Auction.class, auction.getIdentity());
	
			Assert.assertNull(auction);
		} finally {
			em.close();
		}
		
	}

}
