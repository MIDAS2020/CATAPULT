/*
 * DatabaseInfo.java
 *
 * Created on September 5, 2007, 9:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package db;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This is a container for the database information. A dababase engine is also
 * provided. Any database ralated task should be performed through the database
 * engine API.
 * 
 * @author Colin
 */
public class DatabaseInfo implements Serializable {

	private static final long serialVersionUID = 10000L;

	public static final String SCHEMA_TREE_ROOT = "<root>";

	public static int HOST_NAME = 0;
	public static int PORT_NUMBER = 1;
	public static int DB_NAME = 2;
	public static int USER_NAME = 3;
	public static int PASSWORD = 4;

	// database parameters
	// unmodifiable, user must provide all these parameters in the constructor
	// currently hostname and port are not used
	private String hostName = "localhost";
	private String port = "1433";
	private String dbName;
	private String userName;
	private String password;

	public static String[] dblabels = { "C", "O", "Cu", "N", "S", "P", "Cl", "Zn", "B", "Br", "Co", "Mn", "As", "Al",
			"Ni", "Se", "Si", "V", "Sn", "I", "F", "Li", "Sb", "Fe", "Pd", "Hg", "Bi", "Na", "Ca", "Ti", "Ho", "Ge",
			"Pt", "Ru", "Rh", "Cr", "Ga", "K", "Ag", "Au", "Tb", "Ir", "Te", "Mg", "Pb", "W", "Cs", "Mo", "Re", "Cd",
			"Os", "Pr", "Nd", "Sm", "Gd", "Yb", "Er", "U", "Tl", "Ac" };

	/*
	 * public static String[] dblabels_eMolecul =
	 * {"H","He","Li","Be","B","C","N","O","F","Ne","Na","Mg","Al","Si","P","S",
	 * "Cl",
	 * "Ar","K","Ca","Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn","Ga","Ge","As"
	 * ,"Se","Br","Kr","Rb","Sr","Y","Zr",
	 * "Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn","Sb","Te","I","Xe","Cs",
	 * "Ba","La","Ce","Pr","Nd","Pm","Sm","Eu",
	 * "Gd","Tb","Dy","Ho","Er","Tm","Yb","Lu","Hf","Ta","W","Re","Os","Ir","Pt",
	 * "Au","Hg","Tl","Pb","Bi","Po","At","Rn",
	 * "Fr","Ra","Ac","Th","Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm","Md",
	 * "No","Lr","Rf","Db","Sg","Bh","Hs","Mt",
	 * "Ds","Rg","Cn","Uut","Fl","Uup","Lv","Uus","Uuo"};
	 */
	// emolecul dataset
	public static String[] dbLabels_emolecul ={
			"Cs", "Cu", "Yb", "Cl", "Pt", "Pr", "Co", "Cr", "Li", "Cd", "Ce", "Hg", "Hf", "La", "Lu",
			"Pd", "Tl", "Tm", "Ho", "Pb", "*", "Ti", "Te", "Dy", "Ta", "Os", "Mg", "Tb", "Au", "Se",
			"F", "Sc", "Fe", "In", "Si", "B", "C", "As", "Sn", "N", "Ba", "O", "Eu", "H", "Sr", "I", "Mo",
			"Mn", "K", "Ir", "Er", "Ru", "Ag", "W", "V", "Ni", "P", "S", "Nb",
			"Y", "Na", "Sb", "Al", "Ge", "Rb", "Re", "Gd", "Ga", "Br", "Rh", "Ca", "Bi", "Zn", "Zr",
			"R#", "R","X","R1","A","U",	"Ar",	"Kr",	"Xe",	"e", ".",	"Tc",	 "Mu", "Mu-", "He",	"Ps",	"At",
			"Po",	"Be",	"Ne","Rn",	"Fr",	"Ra",	"Ac",	"Rf",	"Db", "Sg","Bh",	"Hs",		"Mt",
			"Ds",	"Rg",	"Nd","Pm",		"Sm",	"Th",	"Pa",	"Np","Pu",	"Am",	"Cm",	"Bk",
			"Cf","Es",	"Fm",	"Md", "No",	"Lr","0",	"Uub", "R2",	"R3",	"R4",	"D", "R5",	"ACP"
			};
		//{ "Cs", "Cu", "Yb", "Cl", "Pt", "Pr", "Co", "Cr", "Li", "Cd", "Ce", "Hg",
		//	"Hf", "La", "Lu", "Pd", "Tl", "Tm", "Ho", "Pb", "*", "Ti", "Te", "Dy", "Ta", "Os", "Mg", "Tb", "Au", "Se",
		//	"F", "Sc", "Fe", "In", "Si", "B", "C", "As", "Sn", "N", "Ba", "O", "Eu", "H", "Sr", "I", "Mo", "Mn", "K",
		//	"Ir", "Er", "Ru", "Ag", "W", "V", "Ni", "P", "S", "Nb", "Y", "Na", "Sb", "Al", "Ge", "Rb", "Re", "Gd", "Ga",
		//	"Br", "Rh", "Ca", "Bi", "Zn", "Zr" };
	// static vector<string> vertexLabel_emolecul(vertexLabel_emolecul,
	// vertexLabel_emolecul + 74);

	// pubchem dataset
	public static String[] dbLabels_pubchem = { "H", "C", "O", "N", "Cl", "S", "F", "P", "Br", "I", "Na", "Si", "As",
			"Hg", "Ca", "K", "B", "Sn", "Se", "Al", "Fe", "Mg", "Zn", "Pb", "Co", "Cu", "Cr", "Mn", "Sb", "Cd", "Ni",
			"Be", "Ag", "Li", "Tl", "Sr", "Bi", "Ce", "Ba", "U", "Ge", "Pt", "Te", "V", "Zr", "Cs", "Au", "Mo", "W",
			"La", "Ti", "Rh", "Lu", "Pd", "In", "Eu", "Ga", "Pr", "Ho", "Th", "Ta", "Tc", "Tb", "Ir", "Nd", "Nb", "Rb",
			"Kr", "Yb", "Cm", "Pu", "Cf", "Hf", "He", "Pa", "Tm", "Pm", "Po", "Xe", "Dy", "Os", "Md", "Sc", "Ar", "At",
			"Sm", "Er", "Ru", "Es", "Ac", "Am", "Ne", "Y", "Re", "Gd", "No", "Rn", "Np", "Fm", "Bk", "Lr" };
	// static Vector<String> vertexLabel_pubchem(vertexLabel_pubchem,
	// vertexLabel_pubchem + 100);

	// https://en.wikipedia.org/wiki/Valence_(chemistry)
	public static int[] dbElement_maxValence = { 4, 2, 4, 5, 6, 5, 7, 2, 3, 7, 5, 7, 5, 3, 4, 6, 4, 5, 4, 7, 1, 1, 5, 7,
			6, 2, 5, 1, 2, 4, 3, 4, 6, 8, 6, 6, 3, 1, 4, 5, 4, 9, 6, 2, 4, 6, 1, 6, 7, 2, 8, 5, 4, 3, 3, 3, 3, 6, 3,
			3 };

	// database engine that provides service to this database
	private transient DatabaseEngine engine;

	// the database connection
	private transient Connection dbConnection;

	// connection status
	private transient boolean connected = false;

	// status code if schema is available
	private transient boolean schemaAvailable = false;

	public DatabaseInfo() {
	}

	public DatabaseInfo(String dbName, String userName, String password) {
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
	}

	public DatabaseInfo(String[] metaInfo, DatabaseEngine engine) {
		hostName = "localhost";
		port = "1433";
		dbName = metaInfo[DatabaseInfo.DB_NAME];
		userName = metaInfo[DatabaseInfo.USER_NAME];
		password = metaInfo[DatabaseInfo.PASSWORD];
		this.engine = engine;
	}

	public void connect() throws Exception {
		dbConnection = getEngine().connect();
		this.setConnected(true);
	}

	public String getDbName() {
		return dbName;
	}

	@Override
	public String toString() {
		return dbName + "@" + getHostName();
	}

	public String getHostName() {
		return hostName;
	}

	public String getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public DatabaseEngine getEngine() {
		return engine;
	}

	public String[] getLabels() {
		System.out.println("DBName=" + dbName);
		if (dbName.compareTo("emolecul") == 0)
			return dbLabels_emolecul;
		else if (dbName.compareTo("pubchem") == 0)
			return dbLabels_pubchem;
		else
			return dblabels;
	}

	public int[] getMaxValency() {
		return dbElement_maxValence;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public List<String> getDataSourceList() {
		List<String> list = new LinkedList<String>(getEngine().getDataSourceSet());
		return Collections.unmodifiableList(list);
	}

	public Connection getDbConnection() {
		return dbConnection;
	}

	public void setEngine(DatabaseEngine engine) {
		if (getEngine() == null)
			this.engine = engine;
	}

	public boolean isSchemaAvailable() {
		return schemaAvailable;
	}

	public void setSchemaAvailable(boolean schemaAvailable) {
		this.schemaAvailable = schemaAvailable;
	}
}
