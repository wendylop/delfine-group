package de.sb.broker.model;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PersonEntityTest extends EntityTest{
	private EntityManager em;
	private Validator validator;
	
	Set<ConstraintViolation<Person>> constrainViolations;
	
	@Test
	public void testConstraints()
	{
		validator = this.getEntityValidatorFactory().getValidator();
		
		//create valid person
		Person person1 = new Person();
		person1.setAlias("Batman");
		person1.getName().setFamily("Bruce");
		person1.getName().setGiven("Wayne");
		person1.getAdress().setStreet("Wayne Street 1");
		person1.getAdress().setCity("Gotham");
		person1.getAdress().setPostCode("30456");
		person1.getContact().setEmail("bruce.wayne@wayne-enterprise.com");
		person1.getContact().setPhone("01506060601");
		
		//create not valid person
		Person person2 = new Person();
		person2.setAlias("Joker");
		person2.getName().setFamily("");
		person2.getName().setGiven("");
		person2.getAdress().setStreet("Wayne Street 1");
		person2.getAdress().setCity("");
		person2.getAdress().setPostCode("0123456789012345678");
		person2.getContact().setEmail("hahahahahahahaha.joker");
		person2.getContact().setPhone("Which Phone!!!");
		
		//test person1
		constrainViolations = validator.validate(person1);
		//check if there are errors - should be 0
		assertEquals(constrainViolations.size(), 0);
		
		//remove old values
		constrainViolations.clear();
		
		//test person2
		constrainViolations = validator.validate(person2);
		//check if ther are errors - should be 5
		assertEquals(constrainViolations.size(), 5);		
	}
	
	@Test
	public void testLifeCycle()
	{	
		em = this.getEntityManagerFactory().createEntityManager();
		
		Person person1 = new Person();
		person1.setAlias("Batman");
		person1.getName().setFamily("Bruce");
		person1.getName().setGiven("Wayne");
		person1.getAdress().setStreet("Wayne Street 1");
		person1.getAdress().setCity("Gotham");
		person1.getAdress().setPostCode("30456");
		person1.getContact().setEmail("bruce.wayne@wayne-enterprise.com");
		person1.getContact().setPhone("01506060601");
		
		
		em = this.getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();
		
		em.persist(person1);
		em.getTransaction().commit();
		this.getWasteBasket().add(person1.getIdentity());
		
		
		//...
		
		//em.persist(entity);
		//em.getTransaction().commit();
		//this.getWasteBasket().add(entity.getIdentity());
		
		//...
		
		em.close();		
	}
	

}
