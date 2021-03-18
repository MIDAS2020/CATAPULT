package db;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cjjin
 */

import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeTransferHandler extends TransferHandler {

	/**
	 * Bundle up the data for export.
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {

		JTree jt = (JTree) c;
		TreePath[] paths = jt.getSelectionPaths();
		if (paths != null) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
			String nodelabel = node.toString();

			System.out.println("nodelabel:" + nodelabel);
			return new StringSelection(nodelabel);
		}
		return null;

	}

	/**
	 * The list handles both copy and move actions.
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	/**
	 * When the export is complete, remove the old list entry if the action was a
	 * move.
	 */
	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
		if (action != MOVE) {
			return;
		}
	}

	/**
	 * We only support importing strings.
	 */
	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {

		// disable import
		return false;
	}
}
