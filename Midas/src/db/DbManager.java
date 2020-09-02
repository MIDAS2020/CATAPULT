/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author cjjin
 */
public class DbManager {

	// private DbPanel view;
	private DefaultComboBoxModel databaseListComboBoxModel;
	private static DbManager instance = null;

	// bound properties
	private DatabaseInfo currentDb;
	private DatabaseList databaseList;
	public static final String NO_DATABASE_INFO = "No Database Selected";
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public static DbManager getInstance() {
		if (instance == null) {
			instance = new DbManager();
		}

		return instance;
	}

	protected DbManager() {

		// local parameters
		// this.view = view;
		this.databaseList = new DatabaseList();

		databaseListComboBoxModel = new DefaultComboBoxModel(new String[] { NO_DATABASE_INFO });
		// view.getDatabaseListComboBox().setModel(databaseListComboBoxModel);

		// bind the currentDb property with the databaselist combobox
		// the event is fired by selecting a different item in the combobox
		// view.getDatabaseListComboBox().addItemListener(
		// new DatabaseListComboBoxItemListener());

		// bind databaseList with the combobox
		databaseList.addListDataListener(new DatabaseListDataListener());

		// bind the currentDb property with the combobox
		// the event is fired from other class
		this.addPropertyChangeListener(new CurrentDbPropertyChangeListener());

		// set database combobox view and action
		// view.getDatabaseListComboBox().setRenderer(new DbComboBoxRenderer());

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	private class DatabaseListComboBoxItemListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			Object item = e.getItem();
			if (item instanceof DatabaseInfo) {
				setCurrentDb((DatabaseInfo) item);
			} else {
				setCurrentDb(null);
			}
		}

	}

	/*
	 * public DbPanel getView() { return view; }
	 */
	// database list data lister
	// this listener is invoked whenever a new database is added
	// or an old database is removed
	private class DatabaseListDataListener implements ListDataListener {

		public void intervalAdded(ListDataEvent e) {
			int index0 = e.getIndex0();
			int index1 = e.getIndex1();

			for (int i = index0; i <= index1; i++) {
				// DatabaseInfo db = (DatabaseInfo) databaseList.get(i);
				// addDefaultSchemaTree(db);

				databaseListComboBoxModel.addElement(databaseList.get(i));
			}
		}

		public void intervalRemoved(ListDataEvent e) {
			Object[] removedObjects = databaseList.getRemovedObjects();

			if (removedObjects.length == 0) {
				return;
			}
			setCurrentDb(null);

			for (Object obj : removedObjects) {
				databaseListComboBoxModel.removeElement(obj);
			}
		}

		public void contentsChanged(ListDataEvent e) {

		}
	}

	private class CurrentDbPropertyChangeListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if ("currentDb".equals(propertyName)) {
				DatabaseInfo db = (DatabaseInfo) evt.getNewValue();

				if (db == null) {
					// DefaultMutableTreeNode root = new DefaultMutableTreeNode(SCHEMA_TREE_ROOT);
					// root.add(new DefaultMutableTreeNode(NO_DATABASE_INFO));
					// DefaultTreeModel treeModel = new DefaultTreeModel(root);
					// getView().getSchemaTree().setModel(treeModel);
					// getView().getDatabaseListComboBox().setSelectedIndex(0);
				} else {
					// databaseListComboBoxModel.setSelectedItem(db);
					// getView().getDatabaseListComboBox().setSelectedItem(db);
					/*
					 * if (!db.isSchemaAvailable() && !db.getDataSourceList().isEmpty()) { // set up
					 * a temp tree model DefaultMutableTreeNode root = new
					 * DefaultMutableTreeNode(SCHEMA_TREE_ROOT); root.add(new
					 * DefaultMutableTreeNode("Refreshing Schema..."));
					 * getView().getSchemaTree().setModel(new DefaultTreeModel(root));
					 * 
					 * // load the schema in background task Task loadSchemaTask = new
					 * LoadSchemaTask2(db, db.getDataSourceList()); loadSchemaTask.execute();
					 */
				}

				// getView().getSchemaTree().setModel(schemaTreeMapper.get(db));
				// db.setSchemaAvailable(true);
			}
		}
	}

	public void addDatabase(DatabaseInfo db) throws Exception {

		databaseList.addElement(db);
		this.setCurrentDb(db);
	}

	public void setCurrentDb(DatabaseInfo currentDb) {
		DatabaseInfo oldDb = this.currentDb;
		this.currentDb = currentDb;
		changeSupport.firePropertyChange("currentDb", oldDb, currentDb);
	}
}
