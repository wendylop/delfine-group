package de.sb.broker.model;

import static java.util.logging.Level.WARNING;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import de.sb.java.TypeMetadata;


/**
 * JUnit base class for broker entity tests. It realizes once-per-all-tests entity manager factory
 * and validator factory creation, and once-per-any-test entity cleanup.
 */
@TypeMetadata(copyright = "2015-2015 Sascha Baumeister, all rights reserved", version = "1.0.0", authors = "Sascha Baumeister")
public abstract class EntityTest {
	static private final String PERSISTENCE_UNIT_NAME = "broker";
	static private EntityManagerFactory ENTITY_MANAGER_FACTORY;
	static private ValidatorFactory ENTITY_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

	private final Set<Long> wasteBasket = new HashSet<>();


	/**
	 * Returns the entity manager factory.
	 * @return the entity manager factory
	 */
	public EntityManagerFactory getEntityManagerFactory () {
		return ENTITY_MANAGER_FACTORY;
	}


	/**
	 * Returns the entity validator factory.
	 * @return the entity validator factory
	 */
	public ValidatorFactory getEntityValidatorFactory () {
		return ENTITY_VALIDATOR_FACTORY;
	}


	/**
	 * Returns the waste basket.
	 * @return the identities of entities to be deleted after each test
	 */
	public Set<Long> getWasteBasket() {
		return this.wasteBasket;
	}


	/**
	 * Creates a new entity manager factory.
	 */
	@BeforeClass
	static public void createClassResources () {
		ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}


	/**
	 * Closes the entity manager factory.
	 */
	@AfterClass
	static public void closeClassResources () {
		ENTITY_MANAGER_FACTORY.close();
	}


	/**
	 * Removes any entity with an identity contained within the waste basket,
	 * and clears the latter.
	 */
	@After
	public void emptyWasteBasket () {
		final EntityManager entityManager = ENTITY_MANAGER_FACTORY.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			for (final Long identity : this.wasteBasket) {
				try {
					final Object entity = entityManager.find(BaseEntity.class, identity);
					if (entity != null) entityManager.remove(entity);
				} catch (final Exception exception) {
					Logger.getGlobal().log(WARNING, exception.getMessage(), exception);
				}
			}
			entityManager.getTransaction().commit();
			this.wasteBasket.clear();
		} finally {
			entityManager.close();
		}
	}
}