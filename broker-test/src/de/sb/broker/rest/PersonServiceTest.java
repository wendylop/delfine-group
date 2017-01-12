package de.sb.broker.rest;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.junit.Test;
import de.sb.broker.model.Auction;
import de.sb.broker.model.Bid;
import de.sb.broker.model.Person;

public class PersonServiceTest extends ServiceTest{

	@Test
	public void testCriteriaQueries() {

		//1. given
		
		
		//this.getWasteBasket().add(person.getIdentity());
		WebTarget webTarget = newWebTarget("test", "test").path("entities/");
		Response response = webTarget.request().get();
		assertEquals(200, response.getStatus());
		
		webTarget = newWebTarget("test", "test").path("entities/").queryParam("alias", "passwort");
		//Response response = webTarget.request().get();
		List<Person> all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		
	}
	

	@Test
	public void testIdentityQueries() {
	
			
			//test existing person in database
			WebTarget validUserWebTarget = newWebTarget("ines","ines").path("people/1");
			Response response = validUserWebTarget.request().get();
			//test http status
			assertEquals(200, response.getStatus());
			//test name is correct
			//TODO extra methode
			Person person = response.readEntity(Person.class);
			assertEquals("Ines", person.getName().getGiven());
			assertEquals("Bergmann", person.getName().getFamily());
			//test address and contact 
			assertEquals("Berlin", person.getAddress().getCity());
			assertEquals("Wiener Strasse 42", person.getAddress().getStreet());
			assertEquals("10999", person.getAddress().getPostCode());
			assertEquals("ines.bergmann@web.de", person.getContact().getEmail());
			assertEquals("0172/2345678", person.getContact().getPhone());
					
					
			// test for invalid authentication 
			//non-existent user
			WebTarget nonExistentWebTarget = newWebTarget("test", "test").path("people/1");
			assertEquals(401, nonExistentWebTarget.request().get().getStatus());
			//invalid password for existing user
			WebTarget invalidPaswordWebTarget = newWebTarget("ines", "test").path("people/1");
			assertEquals(401, invalidPaswordWebTarget.request().get().getStatus());
			
		
	}
		


	
	@Test
	public void testRequester() {
				
		WebTarget webtarget = newWebTarget("test", "test").path("people/requester/");
		final Response response = webtarget.request().get();
		Person p = response.readEntity(Person.class);
		assertEquals("Test", p.getName().getGiven());
	}

	@Test
	public void testLifeCycle() {
		PersonService p = new PersonService();
		Person p1 = new Person();
		p1.getName().setGiven("Paul");
		p1.getName().setFamily("Test");
		p1.getAddress().setStreet("Blumenstrasse");
		p1.getAddress().setCity("Berlin");
		p1.getAddress().setPostCode("10249");
		p1.getContact().setEmail("paul@web.de");
		p1.getContact().setPhone("12345");
		
		p.createOrUpdatePerson(p1, "paul", "test");
		
		//test existing person in database
		WebTarget validUserWebTarget = newWebTarget("paul","test").path("people/3");
		Response response = validUserWebTarget.request().get();
		//test http status
		assertEquals(200, response.getStatus());
		//test name is correct
		//TODO extra methode
		Person person = response.readEntity(Person.class);
		assertEquals("Paul", person.getName().getGiven());
		assertEquals("Test", person.getName().getFamily());
		//test address and contact 
		assertEquals("Berlin", person.getAddress().getCity());
		assertEquals("Blumenstrasse", person.getAddress().getStreet());
		assertEquals("10249", person.getAddress().getPostCode());
		assertEquals("paul@web.de", person.getContact().getEmail());
		assertEquals("12345", person.getContact().getPhone());
		
		EntityService es = new EntityService();
		es.deleteEntity("paul", p1.getIdentity());
		
		//test existing person in database
		WebTarget validUserWebTarget2 = newWebTarget("paul","test").path("people/3");
		Response response2 = validUserWebTarget2.request().get();
		//test http status
		assertEquals(401, response2.getStatus());
		
	}
	
	
	@Test
	public void testBidRelationQueries() {

	}	

}