/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This interface provides the abstraction for any database system. It provides
 * service to the database returned by getDatabase().
 * 
 * @author zhouyong
 */
public interface DatabaseEngine {

	/**
	 * Returns the context dababase which the database engine is responsible for
	 * This relationship must be true: getDatabase().getEngine() == this
	 * 
	 * @return the context database
	 */
	public DatabaseInfo getDatabase();

	/**
	 * Add the database to the backend DBMS
	 * 
	 * @return true if operation successful
	 * @throws java.lang.Exception
	 */
	public boolean commitAdd() throws Exception;

	/**
	 * Returns the JDBC connection if connection is successful, otherwise returns
	 * null This method will use the login parameters stored in the context database
	 * 
	 * @return JDBC connection
	 * @throws java.lang.Exception
	 * @see getDatabase()
	 */
	public Connection connect() throws Exception;

	/**
	 * Returns the set of data sources
	 * 
	 * @return the data source set
	 */
	public Set<String> getDataSourceSet();

	/**
	 * Delete the context database, optional operation
	 * 
	 * @return true if operation successful
	 * @throws java.lang.Exception
	 */
	public boolean delete() throws Exception;

	/**
	 * Add a new data source
	 * 
	 * @param dataSourceName
	 * @return true if the operation is successful
	 * @throws java.lang.Exception
	 */
	public boolean addDataSource(String dataSourceName) throws Exception;

	/**
	 * Delete a data source and all documents associated with this data source
	 * 
	 * @param dataSourceName
	 * @return true if the operation is successful
	 * @throws java.lang.Exception
	 */
	public boolean deleteDataSource(String dataSourceName) throws Exception;

	/**
	 * Returns all the unique root to leaf paths of a data source
	 * 
	 * @param data
	 *            source name
	 * @return a list of path expressions
	 * @throws java.lang.Exception
	 */
	// public List<PathExpression2> getPaths(String dataSource);

	/**
	 * Load a document to a data source
	 * 
	 * @param url
	 * @param dataSource
	 * @return document ID
	 * @throws java.lang.Exception
	 */
	public int loadDocument(String url, String dataSource) throws Exception;

	/**
	 * Delete a document with docId from a data source This operation is optional
	 * for now
	 * 
	 * @param document
	 *            id
	 * @param data
	 *            source name
	 * @return true if operation successful
	 * @throws java.lang.Exception
	 */
	public boolean deleteDocument(int docId, String dataSource) throws Exception;

	/**
	 * Returns the query engine
	 * 
	 * @return a query engine
	 */
	// public QueryEngine getQueryEngine();

	/**
	 * Execute a query This method should be moved to the QueryEngine in future
	 * implementation
	 * 
	 * @param query
	 * @throws java.lang.Exception
	 */
	public void execute(String query) throws Exception;

	/**
	 * Returns the result tree returned by the last query submitted to
	 * execute(String) This method should be moved to the QueryEngine in future
	 * implementation To provide more flexible API, this method should return a DOM
	 * document or document fragment, the DefaultMutableTreeNode version should be
	 * implmented in the GUI.
	 * 
	 * @param dataSource
	 * @return Tree Root
	 * @throws java.lang.Exception
	 */
	public DefaultMutableTreeNode getTree(String dataSource) throws Exception;
}
