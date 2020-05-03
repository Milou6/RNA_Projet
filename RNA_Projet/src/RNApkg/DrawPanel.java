package RNApkg;
import java.awt.Color;
import java.awt.Dimension;
////
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

public class DrawPanel extends JPanel{
//	private int nbrNeurones;
	private boolean isBiasNeuron;
	
	
	
    public DrawPanel(boolean isBiasNeuron) {
		super();
		this.isBiasNeuron = isBiasNeuron;
	}


	@Override
	public void paintComponent(Graphics g) {
			
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        Dimension size = this.getSize();
//	        int d = 200;
//	        int d = Math.min(size.width, size.height) - 10;
        int d =  Math.min(Math.min(size.width, size.height) - 10, 90)  ;
        int x = (size.width - d) / 2;
        int y = (size.height - d) / 2;
        
        if (isBiasNeuron == false) {
        	g.setColor(Color.blue);
	        g.fillOval(x, y, d, d);
	        g.setColor(Color.blue);
	        g.drawOval(x, y, d, d);
	        
	        
//	        Ellipse2D circle = new Ellipse2D.Double(); 
        }
        
        else {
        	g.setColor(Color.magenta);
	        g.fillOval(x, y, d, d);
	        g.setColor(Color.magenta);
	        g.drawOval(x, y, d, d);
	        
//	        System.out.println("x : " + x +"y : " + y);
        }

	    
	    
	    this.updateUI();
	    
		
    }

}



//int panelHeight = this.getHeight();
//
//for (int i=1; i<nbrNeurones+1; i++) {
////	g.drawOval(50, i*(panelHeight/nbrNeurones)/2, 50, 50); // <-- dessine cercle dans le panel
////	g.drawOval(50, panelHeight/(nbrNeurones+1), 50, 50); // <-- dessine cercle dans le panel
//	
//	g.drawOval(50, 100*i, 50, 50); // <-- dessine cercle dans le panel
//}

//for (int i=1; i<nbrNeurones+1; i++) {
//	JPanel neuronPanel = new JPanel();
//			
//	g.drawOval(50, 50, 50, 50);
//	
//	this.add(neuronPanel);
//	this.updateUI();
//}




//this.setSize(150, 150);
//
//   // Determine the center of the panel
//int cntrX = getWidth()/2;
//int cntrY = getHeight()/2;
//
//// Calculate the radius
//int radius = getWidth()/2;
//
//// Draw the Circle
//g.setColor( Color.BLUE );
//g.fillOval( this.getWidth()/2, this.getHeight()/2, radius, radius );
//g.setColor( Color.GREEN );
//g.drawOval( this.getWidth()/2, this.getHeight()/2, radius, radius );
//
////this.setMinimumSize(new Dimension(150, 150));
//
//
////g.drawOval( 50, 50, 50, 50 );
