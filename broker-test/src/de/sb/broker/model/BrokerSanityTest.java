package de.sb.broker.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class BrokerSanityTest {

	public static void main(String[] args) {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("broker");
		final EntityManager em = emf.createEntityManager();
		em.close();
		emf.close();
	}

}
