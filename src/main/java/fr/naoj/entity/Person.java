package fr.naoj.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * <p>Entity representing basic informations about a person.</p>
 *
 * @author Johann Bernez
 */
@Entity
@Table(name="PERSON", schema="APP")
@NamedQueries({
	@NamedQuery(name="findAll", query="SELECT p FROM Person p")
})
public class Person implements Serializable {

	private static final long serialVersionUID = -8265699928619561960L;

	@Id
	@Column(name="pk_person")
	private int pkPerson;
	
	@Column
	private String firstname;
	
	@Column
	private String lastname;
	
	public int getPkPerson() {
		return pkPerson;
	}
	
	public void setPkPerson(int pkPerson) {
		this.pkPerson = pkPerson;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
}
