package fr.naoj.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fr.naoj.entity.Person;

/**
 * <p>Simple dao calling one native query.</p>
 *
 * @author Johann Bernez
 */
public class PersonDAO {

	private EntityManager manager;
	
	public PersonDAO(EntityManager manager) {
		this.manager = manager;
	}
	
	public List<Person> findAll() {
		Query query = manager.createNamedQuery("findAll");
		@SuppressWarnings("unchecked")
		List<Person> persons = query.getResultList();
		return persons;
	}
}
