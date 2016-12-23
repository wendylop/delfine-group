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
		
		
		// test for invalid authentication
		WebTarget webTarget = newWebTarget("test", "test").path("people/1");
		assertEquals(401, webTarget.request().get().getStatus());
		
		// test valid entity
		//webTarget = newWebTarget("alias", "passwort").path("people/" + person.getIdentity());
		final Response response = webTarget.request().get();
		Person p = response.readEntity(Person.class);
		assertEquals("Test", p.getName().getGiven());
		
		// test invalid entity
		webTarget = newWebTarget("alias", "passwort").path("people/");
		final int status = webTarget.request().get().getStatus();
		assertEquals(404, status);
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

	}


}
