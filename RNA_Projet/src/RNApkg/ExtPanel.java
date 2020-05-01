package RNApkg;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class ExtPanel extends JComponent{
	ArrayList<ArrayList<Point>> neuronCoords;
	ArrayList<ArrayList<int[]>> neuronPanelDimensions;
	JLayeredPane pnlAffichage;
	
	 public ExtPanel(ArrayList<ArrayList<Point>> neuronCoords, ArrayList<ArrayList<int[]>> neuronPanelDimensions, JLayeredPane pnlAffichage) {
		 super();
		 this.neuronCoords = neuronCoords;
		 this.neuronPanelDimensions = neuronPanelDimensions;
		 this.pnlAffichage = pnlAffichage;
	 }
	 
	 
	  // Override paintComponent(): 

	  public void paintComponent (Graphics g)
	  {
	    // Always call super.paintComponent (g): 
	    super.paintComponent(g);
	    
	    this.setLayout(new GridLayout(0, 1));
	    this.setPreferredSize(pnlAffichage.getPreferredSize());
	    this.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
	    this.setVisible(true);
	    
	   	this.repaint();
	    

	    // drawString() is a Graphics method. 
	    // Draw the string "Hello World" at location 100,100 
//	    g.drawString ("Hello World!", 100, 100);
//	    g.drawLine(66, 5, 300, 300);
	    
	    // Boucle sur les layers
	    for (int i=0; i<neuronCoords.size()-1; i++) {
	    	
	    	// Boucle sur neurones de cette layer
	    	for (int j=0; j<neuronCoords.get(i).size(); j++) {
	    		
	    		int x0 = neuronCoords.get(i).get(j).x + (neuronPanelDimensions.get(0).get(0) [0] / 2)  + ( Math.min(Math.min(neuronPanelDimensions.get(0).get(0) [0], neuronPanelDimensions.get(0).get(0) [1]) - 10, 90) /2 ) ;
//	    		int x0 = neuronCoords.get(i).get(j).x + (neuronPanelDimensions.get(0).get(0) [0] / 2) ;
	    		int y0 = neuronCoords.get(i).get(j).y + (neuronPanelDimensions.get(0).get(0) [1] / 2);
	    		
	    		// Boucle sur tous les neurones de la layer SUIVANTE
	    		for (int k=0; k<neuronCoords.get(i+1).size(); k++) {
	    			
	    			int x1 = neuronCoords.get(i+1).get(k).x + (neuronPanelDimensions.get(i+1).get(0) [0] / 2) - ( Math.min(Math.min(neuronPanelDimensions.get(i+1).get(0) [0], neuronPanelDimensions.get(i+1).get(0) [1]) - 10, 90) / 2) ;
//	    			int x1 = neuronCoords.get(i+1).get(k).x + (neuronPanelDimensions.get(i+1).get(0) [0] / 2) ;
	    			int y1 = neuronCoords.get(i+1).get(k).y + (neuronPanelDimensions.get(i+1).get(0) [1] / 2) ;
					
					g.drawLine(x0, y0, x1, y1);
					
	    		}
	    		
	    	}
	    }
	    
	    
	    this.updateUI();
	    // Let's find out when paintComponent() is called. 
//	    System.out.println ("Inside paintComponent");
	  }
	 
	 
	 
	 
}
