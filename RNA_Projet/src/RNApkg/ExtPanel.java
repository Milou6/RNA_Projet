package RNApkg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.sound.sampled.Line;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ExtPanel extends JComponent{
	ArrayList<ArrayList<Point>> neuronCoords;
	ArrayList<ArrayList<int[]>> neuronPanelDimensions;
	JLayeredPane pnlAffichage;
	boolean[] layerHasBiasNeuron;
//	ArrayList<Line2D> linesList;
//	ArrayList<Line2D> linesList;
	ArrayList<ArrayList<Line2D>> linesList;
	boolean highlight;
	int HLindex;

	public ExtPanel(ArrayList<ArrayList<Point>> neuronCoords, ArrayList<ArrayList<int[]>> neuronPanelDimensions, JLayeredPane pnlAffichage, boolean[] layerHasBiasNeuron) {	
		super();
			this.neuronCoords = neuronCoords;
			this.neuronPanelDimensions = neuronPanelDimensions;
			this.pnlAffichage = pnlAffichage;
			this.layerHasBiasNeuron = layerHasBiasNeuron;
//			this.linesList = new ArrayList<Line2D>();
			this.linesList = new ArrayList<ArrayList<Line2D>>();
			this.highlight = false;
			this.HLindex = -1;
			
			
			addMouseMotionListener(new MouseAdapter() {
				public void mouseMoved(MouseEvent e) {
//			    	int x = e.getXOnScreen();
//					int y = e.getYOnScreen();
					int x = e.getX();
					int y = e.getY();
					
					highlight = false;
					HLindex = -1;
			    	
//				    for (Line2D l : linesList) {
					for (ArrayList<Line2D> l : linesList) {
//				    	if (l.intersects(x-20, y-20, x+20, y+20)) {
						for (Line2D line : l) {
							if (line.ptSegDist(e.getX(), e.getY()) <= 5) {
								highlight = true;
								HLindex = linesList.indexOf(l);
							}
						}
//				    	if (l.ptSegDist(e.getX(), e.getY()) <= 10) {
//				    		highlight = true;
////				    		Point2D hlStart = l.getP1();
////				    		hlEnd = l.getP2();
//				    		HLindex = linesList.indexOf(l);
//				    		
//// DELETED BLOCK HERE
//				    	}
//				    	else {
//				    		highlight = false;
//				    	}
				    }
				    repaint();
			    }
			});
}
	 

	 @Override
	  public void paintComponent (Graphics g)
	  {
	    super.paintComponent(g);
//		super.paint(g);
	    Graphics2D g2d = (Graphics2D) g;
	    
	    linesList.clear();
	    ArrayList<Line2D> currentList = new ArrayList<Line2D>();
	    
	    this.setLayout(new GridLayout(0, 1));
	    this.setPreferredSize(pnlAffichage.getPreferredSize());
	    this.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
	    this.setVisible(true);
	    
//	   	this.repaint();
	    
	    int highlightIndex = 0;
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
//	    		System.out.println(neuronCoords.size());
	    		
	    		try {
		    		if (layerHasBiasNeuron[i+1] == true) {
		    			drawLinesStop -= 1;
		    		}
	    		}
	    		catch(Exception e) {
	    			System.out.println("ExtPanel exception caught");
	    		}

	    		
	    		
	    		// Boucle sur tous les neurones de la layer SUIVANTE
	    		for (int k=0; k<drawLinesStop; k++) {
//	    		for (int k=0; k<neuronCoords.get(i+1).size(); k++) {
//	    			System.out.println("Boucle sur tous les neurones de la layer SUIVANTE");
	    			
	    			
	    			// x1 et y1, les coordonnées du neurone d'arrivée de la ligne
	    			int x1 = neuronCoords.get(i+1).get(k).x + (neuronPanelDimensions.get(i+1).get(0) [0] / 2) - ( Math.min(Math.min(neuronPanelDimensions.get(i+1).get(0) [0], neuronPanelDimensions.get(i+1).get(0) [1]) - 10, 90) / 2) ;
//	    			int x1 = neuronCoords.get(i+1).get(k).x + (neuronPanelDimensions.get(i+1).get(0) [0] / 2) ;
	    			int y1 = neuronCoords.get(i+1).get(k).y + (neuronPanelDimensions.get(i+1).get(0) [1] / 2) ;
					
	    			
	    			Double myLine = new Line2D.Double(x0, y0, x1, y1);
//	    			g2d.setColor(Color.GREEN);
//	    			g2d.setStroke(new BasicStroke(4f));
	    			
//	    			Shape myShape = createStrokedShape(myLine);
	    			
	    			// On dessine la ligne entre les 2 neurones
//					g.drawLine(x0, y0, x1, y1);$
	    			
	    			g2d.setColor(Color.BLACK);
	    			g2d.setStroke(new BasicStroke(1));
//	    			if (highlight == true) {
	    			
	    			if (highlightIndex == HLindex) {
				    	g2d.setColor(Color.PINK);
				    	g2d.setStroke(new BasicStroke(3));
//				    	highlight = false;
	    			}

	    			g2d.draw(myLine);
	    			
//	    			linesList.add(myLine);
	    			currentList.add(myLine);
	    		}
	    	
	    	linesList.add(currentList);
	    	}
	    	highlightIndex += 1;
//	    	g2d.dispose();
	    }
	    
	    // On met à jour le Panel
//	    this.updateUI();
	    
//	    for (Line2D l : linesList) {
//	    	System.out.println(l);
//	    }
//	    System.out.println(linesList.size());
	    
	    
	    
	    

//	    pnlAffichage.remove
		   // We need to remove old mouselisteners
//		   try {
//		   java.awt.event.MouseMotionListener[] mouseListenersList = this.getMouseMotionListeners();
//			   for (MouseMotionListener ml : mouseListenersList) {
//			   		this.removeMouseMotionListener(ml);	
//			   }
//		   }
//		   catch (Exception e) {
//		   }

		   
//		MouseMotionListener ml = new MouseAdapter() {
//			public void mouseMoved(MouseEvent e) {
////		    	int x = e.getXOnScreen();
////				int y = e.getYOnScreen();
//				int x = e.getX();
//				int y = e.getY();
//		    	
//			    for (Line2D l : linesList) {
////			    	if (l.intersects(x-20, y-20, x+20, y+20)) {
//			    	if (l.ptSegDist(e.getX(), e.getY()) <= 10) {
//				    	System.out.println("SECTTTTTTTTTTTTTTT");
////				    	Point2D start = l.getP1();
////				    	Point2D end = l.getP2();
////				    	int x0 = (int) l.getX1();
//				    	g2d.setColor(Color.PINK);
//				    	g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//				    	
//				    	Line2D myLine = new Line2D.Double( l.getX1(), l.getY1(), l.getX2(), l.getY2());
////				    	g2d.set
//				    	g2d.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
////				    	g2d.
////				    	g2d.draw(myLine);
////				    	pnlAffichage.updateUI();
////				    	pnlAffichage.repaint();
//				    	Component panel = pnlAffichage.getComponent(pnlAffichage.getComponentCount()-1);
//				    	panel.repaint();
//			    	}
//			    }
//		    }
//
//			
//	    public void mouseClicked(MouseEvent e) {
//		    	int x = e.getXOnScreen();
//		    	int y = e.getYOnScreen();
//		    	
//			    for (Line2D l : linesList) {
////			    	if (l.intersects(x-20, y-20, x+20, y+20)) {
//			    	if (l.ptSegDist(e.getX(), e.getY()) <= 10) {
//				    	System.out.println("SECTTTTTTTTTTTTTTT");
////				    	Point2D start = l.getP1();
////				    	Point2D end = l.getP2();
////				    	int x0 = (int) l.getX1();
//				    	g2d.setColor(Color.BLUE);
//				    	g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
////				    	g2d.set
//				    	g2d.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
//				    	pnlAffichage.updateUI();
//			    	}
//			    }
//		    	
//		    	System.out.println("x " + x + " y " + y);  
//			}
//			
//			public void mousePressed(MouseEvent e) {
//				System.out.println("Mouse pressed; # of clicks: "
//			                    + e.getClickCount());
//			    }
//
//			    public void mouseReleased(MouseEvent e) {
//			    	System.out.println("Mouse released; # of clicks: "
//			                    + e.getClickCount());
//			    }
//
//			    public void mouseEntered(MouseEvent e) {
//			    	System.out.println("Mouse entered");
//			    	
//
//				    for (Line2D l : linesList) {
////					    	createStrokedShape(l);
//				    	
////					    	if (l.intersects(x-20, y-20, x+20, y+20)) {
//				    	if (l.ptSegDist(e.getX(), e.getY()) <= 10) {
//					    	System.out.println("SECTTTTTTTTTTTTTTT");	
//				    	}
//				    }
//
//
//			    }
//
//			    public void mouseExited(MouseEvent e) {
//			    	System.out.println("Mouse exited");
//			    }
//
//	    };
//	    
//		this.addMouseListener(ml);
//		this.addMouseMotionListener(ml);
	 
	    
	    
	  }
 
}
	 
	 
