package de.sb.broker.rest;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.sb.broker.model.Person;

public class PersonServiceTest extends ServiceTest{
	
	@Test
	public void testCriteriaQueries() {
		
		Person person = this.createValidPersonEntity();
		
		EntityManager em = LifeCycleProvider.brokerManager();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();

		this.getWasteBasket().add(person.getIdentity());
		WebTarget webTarget = newWebTarget("ines", "").path("entities/");
		assertEquals(200, webTarget.request().get().getStatus());
		
		webTarget = newWebTarget("ines","").path("entities/").queryParam("alias", "ines");
		Response response = webTarget.request().get();
		List<Person> all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
	}

	private Person createValidPersonEntity() {
		// TODO Auto-generated method stub
		return null;
	}

}
