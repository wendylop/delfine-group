package de.sb.broker.rest;

import static org.junit.Assert.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;
import de.sb.broker.model.Person;

public class PersonServiceTest extends ServiceTest{

	@Test
	public void testCriteriaQueries() {
		//web target parameter 
		
	}
	

	@Test
	public void testIdentityQueries() {
		
		//test existing person in database
		WebTarget validUserWebTarget = newWebTarget("ines","ines").path("people/1");
		Response response = validUserWebTarget.request().get();
		//test http status
		assertEquals(200, response.getStatus());
		//test name is correct
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
		String requesterAlias = "ines";
		//test if requesting user matches the returned user entity
		WebTarget validUserWebTarget = newWebTarget(requesterAlias,"ines").path("people/requester");
		Response response = validUserWebTarget.request().get();
		//test http status
		assertEquals(200, response.getStatus());
		
		assertEquals(requesterAlias, response.readEntity(Person.class).getAlias());
	}

	@Test
	public void testLifeCycle() {
		Person p1 = new Person();
		p1.getName().setGiven("Paul");
		p1.getName().setFamily("Test");
		p1.getAddress().setStreet("Blumenstrasse");
		p1.getAddress().setCity("Berlin");
		p1.getAddress().setPostCode("10249");
		p1.getContact().setEmail("paul@web.de");
		p1.getContact().setPhone("12345");
		p1.setAlias("paul");
		
		WebTarget webTarget = newWebTarget("ines","ines").path("people");
		Response putResponse = webTarget.request().header("Password", "test").put(Entity.xml(p1));
		//test http status
		assertEquals(200, putResponse.getStatus());
		
		Long id = putResponse.readEntity(Long.class);
		getWasteBasket().add(id);

		webTarget = newWebTarget("ines","ines").path("people/"+id);
		Response getResponse = webTarget.request().get();
		assertEquals(200, getResponse.getStatus());
		
		Person person = getResponse.readEntity(Person.class);
		assertEquals("Paul", person.getName().getGiven());
		assertEquals("Test", person.getName().getFamily());
		assertEquals("Berlin", person.getAddress().getCity());
		assertEquals("Blumenstrasse", person.getAddress().getStreet());
		assertEquals("10249", person.getAddress().getPostCode());
		assertEquals("paul@web.de", person.getContact().getEmail());
		assertEquals("12345", person.getContact().getPhone());
		
		
		//EntityService es = new EntityService();
		//es.deleteEntity("paul", p1.getIdentity());
		
		//test existing person in database
		//WebTarget validUserWebTarget2 = newWebTarget("ines","ines").path("people/3");
		//Response response2 = validUserWebTarget2.request().get();
		//test http status
		//assertEquals(401, response2.getStatus());
		
	}
	
	
	@Test
	public void testBidRelationQueries() {
		
	
	}	

}