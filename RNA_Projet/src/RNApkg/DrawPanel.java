package RNApkg;
//
import java.awt.Graphics;

import javax.swing.JPanel;

public class DrawPanel extends JPanel{
	private int nbrNeurones;
	
    public DrawPanel(int nbrNeurones) {
		super();
		this.nbrNeurones = nbrNeurones;
	}


	@Override public void paintComponent(Graphics g) {
		
		for (int i=1; i<nbrNeurones+1; i++) {
			g.drawOval(50, 100*i, 50, 50); // <-- draws an oval on the panel
		}
    }

}
