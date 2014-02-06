/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO 
 */
package ool.com.ofpm.client;

/**
 * @author 1131080355959
 *
 */
public class Neo4jDBClientImpl implements DBClient {
	
	private static Neo4jDBClientImpl instance = null;
	
	private Neo4jDBClientImpl() {
	}
	
	public static Neo4jDBClientImpl getInstance() {
		if (instance == null) {
			instance = new Neo4jDBClientImpl();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see ool.com.ofpm.client.DBClient#exec()
	 */
	@Override
	public void exec() {
		// TODO Auto-generated method stub	
	}

}
