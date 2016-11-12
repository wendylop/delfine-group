package de.sb.broker.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.sb.broker.model.Person;
import de.sb.java.Copyright;
import de.sb.java.net.HttpAuthenticationCodec;


/**
 * This life-cycle provider is a singleton adapter and deploys the following services for REST
 * services:
 * <ul>
 * <li><b>Entity manager life-cycle management</b>: Entity managers are created upon the begin of
 * any HTTP request, and closed at the end of it. This implies that the entity manager's are active
 * during entity marshaling.</li>
 * <li><b>Request-scoped transaction demarcation</b>: Additionally, the design allows for continuous
 * transaction coverage, similar to JDBC. The idea is that a transaction is started automatically
 * upon request begin, and at it's end the last active transaction is automatically rolled back.
 * Services should immediately start a new transaction after committing an existing one.</li>
 * <li><b>HTTP Basic authentification</b>: Returns the requesting user for a given HTTP Basic
 * "Authorization" header value. Triggers HTTP 400/401 responses in case the requester cannot be
 * authenticated.</li>
 * <li><b>Exception mapping</b>: {@link WebApplicationException} instances are mapped to their
 * respective HTTP responses, while all other exception types are mapped to HTTP 500 Internal Server
 * Error. HTTP Codes of 500 and higher also cause the exception to be logged.</li>
 * </ul>
 * Note that the use of a thread local variable for entity manager injection is based on the
 * precondition that any HTTP request is processed within a single thread. This assumption may be
 * broken in some environments, but works nicely in most, like Jersey.
 */
@Provider
@Copyright(year=2013, holders="Sascha Baumeister")
public class LifeCycleProvider implements ContainerRequestFilter, ContainerResponseFilter, ExceptionMapper<Throwable> {
	static private volatile EntityManagerFactory BROKER_FACTORY;
	static private final ThreadLocal<EntityManager> BROKER_THREAD_LOCAL = new ThreadLocal<>();
	static private final Object MONITOR = new Object();
	static private final String PERSON_BY_ALIAS = "select p from Person as p where p.alias = :alias";


	/**
	 * Returns the (lazy initialized) broker factory.
	 * @return the entity manager factory
	 * @throws RuntimeException if there is a problem configuring the factory
	 */
	static private EntityManagerFactory brokerFactory () throws RuntimeException {
		synchronized (MONITOR) {
			if (BROKER_FACTORY == null) {
				BROKER_FACTORY = Persistence.createEntityManagerFactory("broker");
			}
		}
		return BROKER_FACTORY;
	}


	/**
	 * Returns the broker manager associated with the current thread.
	 * @return the entity manager
	 * @throws IllegalStateException if there is no entity manager associated with the current
	 *         thread
	 */
	static public EntityManager brokerManager () throws IllegalStateException {
		final EntityManager entityManager = BROKER_THREAD_LOCAL.get();
		if (entityManager == null) throw new IllegalStateException();
		return entityManager;
	}


	/**
	 * Returns the authenticated requester (a person) for the given RFC 2617 "Basic" authentication.
	 * This operation first decodes user alias and password from an authentication value. Then an
	 * SHA-256 hash-code is calculated for the password. Finally, the latter is used in conjunction
	 * with the user alias to query and return a suitable Person entity from the database.
	 * @param authentication the HTTP Basic "Authorization" header value, or {@code null} for none
	 * @return the authenticated requestor
	 * @throws ClientErrorException (HTTP 400) if the given HTTP Basic "Authorization" header is
	 *         malformed
	 * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
	 * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
	 * @throws IllegalStateException (HTTP 500) if the entity manager associated with the current
	 *         thread is not open
	 * @see HttpAuthenticationCodec#decode(String)
	 */
	static public Person authenticate (final String authentication) throws ClientErrorException, NotAuthorizedException, PersistenceException, IllegalStateException {
		if (authentication == null) throw new NotAuthorizedException("Basic");

		final Map<String,String> credentials;
		try {
			credentials = HttpAuthenticationCodec.decode(authentication);
		} catch (final IllegalArgumentException exception) {
			throw new ClientErrorException(BAD_REQUEST);
		}

		final String mode = credentials.get("mode");
		final String username = credentials.get("username");
		final String password = credentials.get("password");
		if (!"basic".equals(mode) | username == null | password == null) throw new NotAuthorizedException("Basic");

		// TODO: Replace with implementation of JPA authentication by calculating the password hash from the given
		// password, creating a query using the constant below, and returning the person if it matches the password hash.
		// If there is none, or if it fails the password hash check, then throw NotAuthorizedException("Basic"). Note
		// that this exception type is a specialized Subclass of ClientErrorException that is capable of storing a
		// challenge, in this case for Basic Authorization. 
		throw new AssertionError(PERSON_BY_ALIAS);
	}


	/**
	 * Creates a new instance, and forces entity manager factory initialization if it has not
	 * happened yet.
	 * @throws RuntimeException if there is a problem configuring a persistence unit
	 */
	public LifeCycleProvider () throws RuntimeException {
		brokerFactory();
	}


	/**
	 * Maps the given exception to a HTTP response. In case of a WebApplicationException instance,
	 * it's associated response is returned. Otherwise, a generic HTTP 500 response is returned. In
	 * all cases the exception is logged if it indicates a server side problem. Note that this
	 * operation is designed to be called by the JAX-RS runtime, not for use within REST services.
	 * @param exception the exception to be mapped
	 * @return the mapped response
	 * @throws NullPointerException if the given argument is {@code null}
	 */
	public Response toResponse (final Throwable exception) throws NullPointerException {
		final Response response = exception instanceof WebApplicationException
			? ((WebApplicationException) exception).getResponse()
			: Response.status(INTERNAL_SERVER_ERROR).build();

		Logger.getGlobal().log(logLevel(response.getStatusInfo()), exception.getMessage(), exception);
		return response;
	}


	/**
	 * This operation is executed before an HTTP request is processed. It creates a new tournament
	 * entity manager and stores it within a thread local variable, for access by any component
	 * working within the same thread. Note that this operation is designed to be called by the
	 * JAX-RS runtime, not for use within REST services.
	 * @param requestContext the (optional) JAX-RS request context
	 */
	public void filter (final ContainerRequestContext requestContext) {
		final EntityManager entityManager = brokerFactory().createEntityManager();
		entityManager.getTransaction().begin();
		BROKER_THREAD_LOCAL.set(entityManager);
	}


	/**
	 * This operation executes after an HTTP request has been processed, but before the entity
	 * stream has been written. It decorates the entity stream so that the decorator triggers when
	 * the entity stream has been written, in order to close and remove the current thread's
	 * tournament entity manager. Note that this operation is designed to be called by the JAX-RS
	 * runtime, not for use within REST services. Also note that this technology relies on the
	 * entity stream (rather, the decorator wrapping it) being closed regardless of the presence of
	 * absence of a response entity; in other words, we must rely on correct resource management by
	 * the JAX-RS implementation.
	 * @param requestContext the JAX-RS request context
	 * @param responseContext the JAX-RS response context
	 * @throws NullPointerException if any of the given arguments is {@code null}
	 */
	public void filter (final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws NullPointerException {
		final FilterOutputStream triggerStream = new FilterOutputStream(responseContext.getEntityStream()) {
			public void close () throws IOException {
				try {
					super.close();
				} finally {
					final EntityManager entityManager = BROKER_THREAD_LOCAL.get();
					BROKER_THREAD_LOCAL.remove();

					if (entityManager != null && entityManager.isOpen()) {
						try {
							if (entityManager.getTransaction().isActive()) entityManager.getTransaction().rollback();
						} finally {
							entityManager.close();
						}
					}
				}
			}
		};
		responseContext.setEntityStream(triggerStream);
	}


	/**
	 * Returns the log level appropriate for the given HTTP response status.
	 * @param status the HTTP response status
	 * @return the log level
	 * @throws NullPointerException if the given status is {@code null}
	 */
	static private Level logLevel (final Response.StatusType status) throws NullPointerException {
		switch (status.getFamily()) {
			case SERVER_ERROR:
				return Level.WARNING;
			case CLIENT_ERROR:
				return Level.INFO;
			default:
				return Level.FINE;
		}
	}
}