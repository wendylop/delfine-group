package de.sb.broker.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Person;

public class AuctionServiceTest extends ServiceTest {

	@Test
	public void testCriteriaQueries(Person Person) {

		// test for authentication
		WebTarget webTarget = newWebTarget("ines", "ines").path("/auction/");
		assertEquals(200, webTarget.request().get().getStatus());

		Person p1 = new Person();
		p1.getName().setGiven("Paul");
		p1.getName().setFamily("Test");
		p1.getAddress().setStreet("Blumenstrasse");
		p1.getAddress().setCity("Berlin");
		p1.getAddress().setPostCode("10249");
		p1.getContact().setEmail("paul@web.de");
		p1.getContact().setPhone("12345");
		p1.setAlias("paul");

		Auction auction = new Auction(Person);
		auction.setTitle("Test Auction");
		auction.setDescription("Test Description");
		auction.setUnitCount((short) 1000);
		auction.setAskingPrice(111);

		this.getWasteBasket().add(Person.getIdentity());
		this.getWasteBasket().add(auction.getIdentity());

		// "titel"
		webTarget = newWebTarget("ines", "ines").path("/auction/").queryParam("titel", "Test Auction");
		Response response = webTarget.request().get();
		List<Auction> all = response.readEntity(new GenericType<List<Auction>>() {
		});
		assertEquals("Test Description", all.get(0).getDescription());

		//"UnitCountLower"
		webTarget = newWebTarget("ines", "ines").path("/auction/").queryParam("UnitCountLower", "999");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {
		});
		assertTrue(999 <= all.get(0).getUnitCount());

		//  "UnitCountUpper"
		webTarget = newWebTarget("ines", "ines").path("/auction/").queryParam("UnitCountLower", "2000");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {
		});
		assertTrue(2000 >= all.get(0).getUnitCount());

	}

	@Test
	public void testIdentityQueries() {

		// test for invalid authentication
		WebTarget webTarget = newWebTarget("ines", "ines").path("auctions/1");
		assertEquals(401, webTarget.request().get().getStatus());

		Person p1 = new Person();
		p1.getName().setGiven("Paul");
		p1.getName().setFamily("Test");
		p1.getAddress().setStreet("Blumenstrasse");
		p1.getAddress().setCity("Berlin");
		p1.getAddress().setPostCode("10249");
		p1.getContact().setEmail("paul@web.de");
		p1.getContact().setPhone("12345");
		p1.setAlias("paul");

		Auction auction = new Auction(p1);
		auction.setTitle("Testauction");
		auction.setDescription("Test description");
		this.getWasteBasket().add(p1.getIdentity());
		this.getWasteBasket().add(auction.getIdentity());

	
		webTarget = newWebTarget("ines", "ines").path("auctions/" + auction.getIdentity());
		final Response response = webTarget.request().get();
		Auction a = response.readEntity(Auction.class);
		assertEquals("Testauction", a.getTitle());

		
		webTarget = newWebTarget("ines", "ines").path("auctions/1234567");
		final int status = webTarget.request().get().getStatus();
		assertEquals(404, status);
	}

	@Test
	public void testBidRelations() {

	}

}