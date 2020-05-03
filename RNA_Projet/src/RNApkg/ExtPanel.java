package RNApkg;

import java.awt.Graphics;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class ExtPanel extends JComponent{
	 public ExtPanel() {
		 super(); 
	 }
	 
	 
	  // Override paintComponent(): 

	  public void paintComponent (Graphics g)
	  {
	    // Always call super.paintComponent (g): 
	    super.paintComponent(g);

	    // drawString() is a Graphics method. 
	    // Draw the string "Hello World" at location 100,100 
//	    g.drawString ("Hello World!", 100, 100);
	    g.drawLine(0, 0, 250, 250);

	    // Let's find out when paintComponent() is called. 
//	    System.out.println ("Inside paintComponent");
	  }
	 
	 
	 
	 
}
