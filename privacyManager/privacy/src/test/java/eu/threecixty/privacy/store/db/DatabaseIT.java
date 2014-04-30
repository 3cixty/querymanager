package eu.threecixty.privacy.store.db;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

/** Database integration testing unit */
public class DatabaseIT {

	private static Logger log = Logger.getLogger(DatabaseIT.class);
	private EntityManager em;

	@Before
	public void setUp() throws Exception {
		try {
            EntityManagerFactory emf
                    = Persistence.createEntityManagerFactory("PrivacyPU");
            em = emf.createEntityManager();
        } catch (Exception e) {
            log.fatal("Cannot create EntityManager", e);
            throw e;
        }
	}

	@Test
	public void test() {
		Long someUser = new Long(1);
        
        UserTable bean = em.find(UserTable.class, someUser);
        assertNotNull(
              String.format("Not found! someVarchar=\"%s\"",someUser)
            , bean
        );
        
        assertEquals(Long.valueOf(1L), bean.getUserId());
        assertEquals("admin", bean.getName());
        assertEquals("admin".getBytes(), bean.getAuthenticator());
	}

}
