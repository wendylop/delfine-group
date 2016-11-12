package de.sb.broker.rest;

import static java.util.logging.Level.INFO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import com.sun.net.httpserver.HttpServer;


/**
 * JUnit base class for broker service tests. It realizes once-per-all-tests embedded HTTP container
 * start/stop on {@link http://localhost:8001/services} for REST service access, and
 * once-per-any-test entity cleanup based on {@linkplain EntityService}. Note that the HTTP
 * container can alternatively be started as a separate application.
 */
public class ServiceTest {
	static private final URI SERVICE_URI = URI.create("http://localhost:8001/services");
	static private HttpServer HTTP_CONTAINER = null;

	private final Set<Long> wasteBasket = new HashSet<>();


	/**
	 * Returns the waste basket.
	 * @return the identities of entities to be deleted after each test
	 */
	public Set<Long> getWasteBasket() {
		return this.wasteBasket;
	}


	/**
	 * Removes any entity with an identity contained within the waste basket, and clears the latter.
	 * This operation is run once after each of the test methods in it's class have been run.
	 */
	@After
	public void emptyWasteBasket () {
		while (!this.wasteBasket.isEmpty()) {
			synchronized(this.wasteBasket) {
				for (final Iterator<Long> iterator = this.wasteBasket.iterator(); iterator.hasNext(); ) {
					try {
						final WebTarget webTarget = newWebTarget("ines", "ines").path("entities/" + iterator.next());
						final int status = webTarget.request().delete().getStatus();
						if (status == 204 | status == 404) iterator.remove();
					} catch (final Exception exception) {
						// try again
					}
				}
			}
		}
	}


	/**
	 * Creates a new base target for the given credentials.
	 * @param alias the user alias
	 * @param password the user password
	 * @return the base target
	 */
	static public WebTarget newWebTarget(final String alias, final String password) {
		final ClientConfig configuration = new ClientConfig()
			.register(HttpAuthenticationFeature.basic(alias, password))
			.register(MoxyJsonFeature.class)
			.register(MoxyXmlFeature.class)
			.register(EntityFilteringFeature.class);

		return ClientBuilder.newClient(configuration).target(SERVICE_URI);
	}


	/**
	 * Creates an embedded REST service container. This operation is run once before any of the test
	 * methods in it's class are run.
	 */
	@BeforeClass
	static public void startEmbeddedHttpContainer () {
		final ResourceConfig configuration = new ResourceConfig()
			.packages(ServiceTest.class.getPackage().toString())
			.register(MoxyJsonFeature.class)	// edit "network.http.accept.default" in Firefox's "about:config"
			.register(MoxyXmlFeature.class)		// to make "application/json" preferable to "application/xml"
			.register(EntityFilteringFeature.class);

		HTTP_CONTAINER = JdkHttpServerFactory.createHttpServer(SERVICE_URI, configuration);
		Logger.getGlobal().log(INFO, "Embedded HTTP container running on service address {0}.", SERVICE_URI);
	}


	/**
	 * Creates an embedded REST service container running on TCP port 8001. This operation is run
	 * once after all the test methods in it's class have been run.
	 */
	@AfterClass
	static public void stopEmbeddedHttpContainer () {
		HTTP_CONTAINER.stop(0);
		Logger.getGlobal().log(INFO, "Embedded HTTP container stopped.");
	}


	

	/**
	 * Application entry point.
	 * @param args the runtime arguments
	 * @throws IOException if there is an I/O related problem
	 */
	static public void main (final String[] args) throws IOException {
		startEmbeddedHttpContainer();
		try {
			Logger.getGlobal().log(INFO, "Enter \"quit\" to stop.");
			final BufferedReader charSource = new BufferedReader(new InputStreamReader(System.in));
			while (!"quit".equals(charSource.readLine()));
		} finally {
			stopEmbeddedHttpContainer();
		}
	}
}