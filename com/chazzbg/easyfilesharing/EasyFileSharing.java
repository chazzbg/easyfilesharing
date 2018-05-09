/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chazzbg.easyfilesharing;

import java.io.IOException;
import javax.swing.UIManager;

/**
 *
 * @author chazz
 */
public class EasyFileSharing {

	/**
	 * @param args the command line arguments
	 */
	
	public static void main(String[] args) throws IOException {
	
	
		
		try {
		
				
					javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
				
			
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>
		/*
		 * Create and display the form
		 */
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new MainView().setVisible(true);
			}
		});
	}
}
