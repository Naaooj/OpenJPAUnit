package fr.naoj.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.openjpa.persistence.EntityManagerImpl;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.naoj.entity.Person;

/**
 * <p>Main test class, responsible of creating in memory database,
 * entity manager and launching test</p>
 *
 * @author Johann Bernez
 */
public class PersonDAOTest {

	private static final String DATASET_NAME = "PersonDataSet.xml";
	
	private static EntityManager manager;
	
	@BeforeClass public static void createContext() {
		try {
			// Creating the database in memory
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        	DriverManager.getConnection("jdbc:derby:memory:testDB;create=true").close();
        	
			Map<String, Object> properties = new HashMap<String, Object>();
			// 1.3 Runtime forward mapping 
			// Without it, entities are not recognized in the context and
			// can't be enhanced automatically by openjpa
			properties.put("openjpa.jdbc.SynchronizeMappings", "add");
			// 5.57 openjpa.RuntimeUnenhancedClasses
			properties.put("openjpa.RuntimeUnenhancedClasses", "supported");
			
			// No need to define de provider in the persistence.xml file, the test
			// class is responsible of it's instantiation just like an JEE container's
			// would have done
			Class<?> c = Class.forName("org.apache.openjpa.persistence.PersistenceProviderImpl", true, Thread.currentThread().getContextClassLoader());
			PersistenceProviderImpl provider = (PersistenceProviderImpl) c.newInstance();
			EntityManagerFactory factory = provider.createEntityManagerFactory("testPU", "test.persistence.xml", properties);
			
			// Now creating the entity manager for real, cross fingers :-)
			manager = factory.createEntityManager();

        	EntityManagerImpl em = (EntityManagerImpl) manager.getDelegate();
        	Connection connection = (Connection) em.getConnection();
        	
        	// If the container do not create the database (depending on the configuration
        	// of persistence.xml), it's possible to use an sql script instead
        	//ij.runScript(connection, PersonDAOTest.class.getClassLoader().getResourceAsStream("Person.sql"), "UTF-8", System.out, "UTF-8");
        	
        	// To see how are loaded the classes to enhance (may be helpful sometimes) :
        	//MetaDataRepository repo = em.getConfiguration().getMetaDataRepositoryInstance();
        	//repo.loadPersistentTypes(false, LoggingDAOTest.class.getClassLoader());
        	
        	// Fill the database with datas from dataset
        	IDataSet dateSet = new FlatXmlDataSetBuilder().build(PersonDAOTest.class.getClassLoader().getResourceAsStream(DATASET_NAME));
        	IDatabaseConnection dbConnection = new DatabaseConnection(connection, "app");
        	dbConnection.getConfig().setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
        	DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dateSet);
        	
        	// Each class enhanced by OpenJPA must implement PersistenceCapable.class
			// To check if an entity is in the jpa context, use : ImplHelper.isManagedType(em.getConfiguration(), YourEntity.class));
        	// To check if an entity is enhanced, use : PCRegistry.isRegistered(YourEntity.class));
		} catch (Exception e) {
			Logger.getLogger(PersonDAOTest.class.getName()).log(Level.SEVERE, "Unable to initialize in memory database", e);
			fail("Unable to initialize test case, see the logs for more details");
		}
	}
	
	@Test public void testDao() {
		PersonDAO dao = new PersonDAO(manager);
		List<Person> persons = dao.findAll();
		assertNotNull(persons);
		assertEquals(1, persons.size());
		
		Person p = persons.get(0);
		assertNotNull(p);
		assertEquals("Doe", p.getLastname());
		assertEquals("John", p.getFirstname());
	}
}
