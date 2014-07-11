/**
 * @author OOL 1131080355959
 * @date 2014/02/20
 * @TODO 
 */
package ool.com.orientdb.client;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.sql.SQLException;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * @author 1131080355959
 *
 */
public class DaoImplTest {
	
	@Mocked("init")
	DaoImpl daoMock;

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#DaoImpl(ool.com.orientdb.client.ConnectionUtils)}.
	 */
	@Test
	public void testDaoImpl() {
		new Expectations() {
			ConnectionUtilsImpl mockUtils;
			{
				new ConnectionUtilsImpl();
				invoke(daoMock,"init");
			}
		};
		try {
			Dao dao = new DaoImpl(new ConnectionUtilsImpl());
			assertNotNull(dao);
		} catch (SQLException e) {
			fail("");
		}
		
		new Expectations() {
			ConnectionUtilsImpl mockUtils;
			{
				new ConnectionUtilsImpl();
				invoke(daoMock,"init"); result = new SQLException("expected");
			}
		};
		try {
			Dao dao = new DaoImpl(new ConnectionUtilsImpl());
			fail();
		} catch (SQLException e) {
			assertEquals("expected", e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#close()}.
	 */
	@Test
	public void testClose() {
		/*
		try {
			new Expectations() {
				ConnectionUtilsImpl mockUtils;
				ODatabaseDocumentTx mockDoc;
				{
					new ConnectionUtilsImpl(); result=mockUtils;
					invoke(daoMock,"init");
					mockUtils.getDatabase(); result = mockDoc;
				}
			};
			try {
				Dao dao = new DaoImpl(new ConnectionUtilsImpl());
			} catch (SQLException e) {
				fail("");
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
		*/
	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#getDeviceRid(java.lang.String)}.
	 */
	@Test
	public void testGetDeviceRid() {
	
	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#getDeviceInfo(java.lang.String)}.
	 */
	@Test
	public void testGetDeviceInfo() {
	
	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#getConnectedDevice(java.lang.String)}.
	 */
	@Test
	public void testGetConnectedDevice() {
	
	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#getPatchPortRidList(java.lang.String)}.
	 */
	@Test
	public void testGetPatchPortRidList() {
	
	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#isContainsPatchWiring(java.util.List)}.
	 */
	@Test
	public void testIsContainsPatchWiring() {
	
	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#getShortestPath(java.util.List)}.
	 */
	@Test
	public void testGetShortestPath() {
	
	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#insertPatchWiring(java.util.List, java.lang.String, java.util.List)}.
	 */
	@Test
	public void testInsertPatchWiring() {

	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#getPortRidPatchWiring(java.util.List)}.
	 */
	@Test
	public void testGetPortRidPatchWiring() {

	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#deleteRecordPatchWiring(java.util.List)}.
	 */
	@Test
	public void testDeleteRecordPatchWiring() {

	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#getPortInfo(java.lang.String)}.
	 */
	@Test
	public void testGetPortInfo() {

	}

	/**
	 * Test method for {@link ool.com.orientdb.client.DaoImpl#updateLinkWeight(int, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUpdateLinkWeight() {

	}

}
