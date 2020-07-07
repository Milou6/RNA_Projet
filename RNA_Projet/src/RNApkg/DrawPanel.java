package RNApkg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class DrawPanel extends JPanel{
	//	private int nbrNeurones;
	private String neuronType;

	public DrawPanel(String neuronType) {
		super();
		this.neuronType = neuronType;
	}


	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Dimension size = this.getSize();
		int d =  Math.min(Math.min(size.width, size.height) - 10, 90)  ;
		int x = (size.width - d) / 2;
		int y = (size.height - d) / 2;


		switch(neuronType) {
		case "regular":
			g.setColor(Color.blue);
			g.fillOval(x, y, d, d);
			g.setColor(Color.blue);
			g.drawOval(x, y, d, d);
			break;

		case "bias":
			g.setColor(Color.magenta);
			g.fillOval(x, y, d, d);
			g.setColor(Color.magenta);
			g.drawOval(x, y, d, d);
			break;

		case "true_output":
			g.setColor(Color.orange);
			g.fillOval(x, y, d, d);
			g.setColor(Color.orange);
			g.drawOval(x, y, d, d);

		}

		this.updateUI();   
	}
}
