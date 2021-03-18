/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
//import org.jdesktop.application.ResourceMap;
//import davinci.GBlendApp;
//import davinci.GBlendView;

/**
 *
 * @author zhouyong
 */
class DbComboBoxRenderer extends JLabel implements ListCellRenderer {

	public DbComboBoxRenderer() {
		setOpaque(true);
		setIconTextGap(10);
		setHorizontalAlignment(LEADING);
		setVerticalAlignment(CENTER);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value != null) {
			setText(value.toString().trim());
			setFont(new java.awt.Font("Tahoma", 1, 12));
		}

		if (value instanceof DatabaseInfo) {
			setText(value.toString().trim());
		}
		/*
		 * ResourceMap resourceMap =
		 * GBlendApp.getApplication().getContext().getResourceMap(GBlendView.class);
		 * 
		 * if (index == 0) { setIcon(resourceMap.getIcon("Database.database_connect"));
		 * } else { setIcon(resourceMap.getIcon("Database.database_gear")); }
		 */
		return this;
	}
}
