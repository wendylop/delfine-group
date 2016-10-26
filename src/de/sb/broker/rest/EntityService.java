package de.sb.broker.rest;

import static de.sb.broker.model.Person.Group.ADMIN;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import de.sb.broker.model.BaseEntity;
import de.sb.broker.model.Person;
import de.sb.java.TypeMetadata;


/**
 * JAX-RS based REST service implementation for polymorphic entity resources. The following path and
 * method combinations are supported:
 * <ul>
 * <li>DELETE entities/{identity}: Deletes the entity matching the given identity.</li>
 * </ul>
 */
@Path("entities")
@TypeMetadata(copyright = "2013-2015 Sascha Baumeister, all rights reserved", version = "1.0.0", authors = "Sascha Baumeister")
public class EntityService {

	/**
	 * Deletes the entity matching the given identity, or does nothing if no such entity exists.
	 * @param authentication the HTTP Basic "Authorization" header value
	 * @param identity the identity
	 * @return void (HTTP 204)
	 * @throws ClientErrorException (HTTP 400) if the given HTTP "Authorization" header is malformed
	 * @throws ClientErrorException (HTTP 401) if authentication is lacking or invalid
	 * @throws ClientErrorException (HTTP 403) if authentication is successful, but the requester is
	 *         not an administrator
	 * @throws ClientErrorException (HTTP 404) if the given entity cannot be found
	 * @throws ClientErrorException (HTTP 409) if the given entity cannot be found
	 * @throws PersistenceException (HTTP 500) if there is a problem with the persistence layer
	 */
	@DELETE
	@Path("{identity}")
	public void deleteEntity (@HeaderParam("Authorization") final String authentication, @PathParam("identity") final long identity) {
		final EntityManager brokerManager = LifeCycleProvider.brokerManager();
		final Person requester = LifeCycleProvider.authenticate(authentication);
		if (requester.getGroup() != ADMIN) throw new ClientErrorException(FORBIDDEN);

		brokerManager.getEntityManagerFactory().getCache().evict(BaseEntity.class, identity);
		try {
			final BaseEntity entity = brokerManager.getReference(BaseEntity.class, identity);
			brokerManager.remove(entity);
			try {
				brokerManager.getTransaction().commit();
			} finally {
				brokerManager.getTransaction().begin();
			}
		} catch (final EntityNotFoundException exception) {
			throw new ClientErrorException(NOT_FOUND);
		} catch (final RollbackException exception) {
			throw new ClientErrorException(CONFLICT);
		}
	}
}