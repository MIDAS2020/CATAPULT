package andyfyp;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

public class ThumbnailTable extends JTable implements DragSourceListener, DragGestureListener {

	// JTable data
	private Object[][] rows;
	private Object[] columns;

	private DragSource dragSource; // source processing <- (source component, drag listener)
	private MyTransferHandler transferHandler; // data carrier <- (export, import)

	public ThumbnailTable(Object[] columnNames, Object[][] data, int rowHeight) {
		super(data, columnNames);

		rows = data;
		columns = columnNames;
		// columns = new Object[3];

		setRowHeight(rowHeight);

		// Table Render Model;
		TableCellRenderer render = getDefaultRenderer(ImageIcon.class);
		setDefaultRenderer(ImageIcon.class, new ButtonRenderer(render));

		// Table Data Model;
		TableModel newModel = new ImageModel();
		setModel(newModel);

		// Drag
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(this, // JComponent
				DnDConstants.ACTION_MOVE, // Action
				this); // DragGestureListener

		// Transfer
		transferHandler = new MyTransferHandler();
		this.setTransferHandler(transferHandler);
	}

	/*-------------------------TransferHandler--------------------------------*/
	class MyTransferHandler extends TransferHandler {

		// Export
		@Override
		public Transferable createTransferable(JComponent tableComponent) {
			ThumbnailTable table = (ThumbnailTable) tableComponent;
			int row = table.getSelectedRow();
			int col = table.getSelectedColumn();

			if (table.getValueAt(row, col) instanceof DnDImage) {
				DnDImage tmp = (DnDImage) table.getValueAt(row, col);
				StringSelection strSelection = new StringSelection("Pattern" + tmp.patternId);
				return strSelection;
			}
			return null;
		}
	}

	/*-------------------------DragGestureListener----------------------------*/
	public void dragGestureRecognized(DragGestureEvent dge) {
		// Null data
		if (this.getValueAt(getSelectedRow(), getSelectedColumn()) == null) {
			return;
		}
		// Not desirable data type
		else if (!(getValueAt(getSelectedRow(), getSelectedColumn()) instanceof DnDImage)) {
			return;
		}

		JComponent tableComp = (JComponent) this;
		Transferable transferable = transferHandler.createTransferable(tableComp);

		dragSource.startDrag(dge, // event
				DragSource.DefaultMoveDrop, // cursor
				transferable, this); // drag source listener
	}

	/*-------------------------DragSourceListener-----------------------------*/
	public void dragEnter(DragSourceDragEvent dsde) {
	}

	public void dragOver(DragSourceDragEvent dsde) {
	}

	public void dragExit(DragSourceEvent dse) {
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	public void dropActionChanged(DragSourceDragEvent dsde) {
	}

	/*-----------------------DataModel & DataRender----------------------------*/
	class ButtonRenderer implements TableCellRenderer {

		// redering when data is not of the expected type
		private TableCellRenderer defaultRenderer;

		public ButtonRenderer(TableCellRenderer defaultRenderer) {
			this.defaultRenderer = defaultRenderer;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// Render expected type;
			if (value instanceof Component) {
				return (Component) value;
			}
			// Render unexpected type;
			return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}// end ButtonRenderer

	class ImageModel extends AbstractTableModel {

		public int getRowCount() {
			return rows.length;
		}

		public int getColumnCount() {
			return columns.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			System.out.println("rowIndex:" + rowIndex + " columnIndex:" + columnIndex + " rows:" + rows.length
					+ " columns:" + columns.length);
			return rows[rowIndex][columnIndex];
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Class getColumnClass(int column) {
			return DnDImage.class;
		}

		@Override
		public String getColumnName(int column) {
			return (String) columns[column];
		}
	}// end ButtonModel class
}
