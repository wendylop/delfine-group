package de.sb.broker.model;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
//import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


public class PersonEntityTest extends EntityTest{
	private Validator validator;	
	Set<ConstraintViolation<Person>> constrainViolations;	
	private final EntityManager em = this.getEntityManagerFactory().createEntityManager();
	
	@Test
	public void testConstraints()
	{
		validator = this.getEntityValidatorFactory().getValidator();
		
		//create valid person
		Person person1 = new Person();
		person1.setAlias("Batman");
		person1.getName().setFamily("Wayne");
		person1.getName().setGiven("Bruce");
		person1.getAddress().setStreet("Wayne Street 1");
		person1.getAddress().setCity("Gotham");
		person1.getAddress().setPostCode("30456");
		person1.getContact().setEmail("bruce.wayne@wayne-enterprise.com");
		person1.getContact().setPhone("01506060601");
		
		//create not valid person
		Person person2 = new Person();
		person2.setAlias("Joker");
		person2.getName().setFamily("");
		person2.getName().setGiven("");
		person2.getAddress().setStreet("Wayne Street 1");
		person2.getAddress().setCity("");
		person2.getAddress().setPostCode("0123456789012345678");
		person2.getContact().setEmail("hahahahahahahaha.joker");
		person2.getContact().setPhone("Which Phone!!!");
		
		//test person1
		constrainViolations = validator.validate(person1);
		//check if there are errors - should be 0
		Assert.assertEquals(constrainViolations.size(), 0);
		
		//remove old values
		constrainViolations.clear();
		
		//test person2
		constrainViolations = validator.validate(person2);
		//check if there are errors - should be 5
		Assert.assertEquals(constrainViolations.size(), 5);		
	}
	
	@Test
	public void testLifeCycle()
	{	
				//persist person entity
		Person person = new Person();
		person.setAlias("Batman");
		person.getName().setFamily("Wayne");
		person.getName().setGiven("Bruce");
		person.getAddress().setStreet("Wayne Street 1");
		person.getAddress().setCity("Gotham");
		person.getAddress().setPostCode("30456");
		person.getContact().setEmail("bruce.wayne@wayne-enterprise.com");
		person.getContact().setPhone("01506060601");
		
		em.getTransaction().begin();
		
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
		//test if entity exists in database
		em.getTransaction().begin();
		person = em.find(Person.class, person.getIdentity());
		Assert.assertEquals(person.getName().getFamily(), "Wayne");
		
		//remove person from database and check if it has been deleted properly
		//em.getTransaction().begin();
		em.remove(person);
		em.getTransaction().commit();
		
		//em.getTransaction().begin();
		person = em.find(Person.class, person.getIdentity());
		Assert.assertNull(person);
		em.clear();
		em.close();		
	}
	
	@After
	public void finializeTests(){
		if (em.isOpen()){
			em.clear();
			em.close();
		} 
	}

}
