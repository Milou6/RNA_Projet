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
	boolean[] layerHasBiasNeuron;
	

	public ExtPanel(ArrayList<ArrayList<Point>> neuronCoords, ArrayList<ArrayList<int[]>> neuronPanelDimensions, JLayeredPane pnlAffichage, boolean[] layerHasBiasNeuron) {	
		super();
			this.neuronCoords = neuronCoords;
			this.neuronPanelDimensions = neuronPanelDimensions;
			this.pnlAffichage = pnlAffichage;
			this.layerHasBiasNeuron = layerHasBiasNeuron;
	 }
	 

	 @Override
	  public void paintComponent (Graphics g)
	  {
	    super.paintComponent(g);
	    
	    this.setLayout(new GridLayout(0, 1));
	    this.setPreferredSize(pnlAffichage.getPreferredSize());
	    this.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
	    this.setVisible(true);
	    
	   	this.repaint();
	    
	    
	    // Boucle sur les layers
	    for (int i=0; i<neuronCoords.size()-1; i++) {
	    	
	    	// Boucle sur neurones de cette layer
	    	for (int j=0; j<neuronCoords.get(i).size(); j++) {
	    		
	    		// x0 et y0, les coordonnées du neurone de départ de la ligne
	    		int x0 = neuronCoords.get(i).get(j).x + (neuronPanelDimensions.get(i).get(j) [0] / 2)  + ( Math.min(Math.min(neuronPanelDimensions.get(0).get(0) [0], neuronPanelDimensions.get(0).get(0) [1]) - 10, 90) /2 ) ;
//	    		int x0 = neuronCoords.get(i).get(j).x + (neuronPanelDimensions.get(0).get(0) [0] / 2) ;
	    		int y0 = neuronCoords.get(i).get(j).y + (neuronPanelDimensions.get(i).get(j) [1] / 2);
	    		
	    		
	    		// Si la Layer suivante a un neurone Biais, on ne va pas dessiner de lignes vers ce neurone
	    		int drawLinesStop = 0;
	    		drawLinesStop = neuronCoords.get(i+1).size();
	    		if (layerHasBiasNeuron[i+1] == true) {
	    			drawLinesStop -= 1;
	    		}
	    		
	    		
	    		// Boucle sur tous les neurones de la layer SUIVANTE
	    		for (int k=0; k<drawLinesStop; k++) {
//	    		for (int k=0; k<neuronCoords.get(i+1).size(); k++) {
//	    			System.out.println("Boucle sur tous les neurones de la layer SUIVANTE");
	    			
	    			
	    			// x1 et y1, les coordonnées du neurone d'arrivée de la ligne
	    			int x1 = neuronCoords.get(i+1).get(k).x + (neuronPanelDimensions.get(i+1).get(0) [0] / 2) - ( Math.min(Math.min(neuronPanelDimensions.get(i+1).get(0) [0], neuronPanelDimensions.get(i+1).get(0) [1]) - 10, 90) / 2) ;
//	    			int x1 = neuronCoords.get(i+1).get(k).x + (neuronPanelDimensions.get(i+1).get(0) [0] / 2) ;
	    			int y1 = neuronCoords.get(i+1).get(k).y + (neuronPanelDimensions.get(i+1).get(0) [1] / 2) ;
					
	    			
	    			// On dessine la ligne entre les 2 neurones
					g.drawLine(x0, y0, x1, y1);
					
	    		}
	    	}
	    }
	    // On met à jour le Panel
	    this.updateUI();
	  }
	 
	 
	 
	 
}
