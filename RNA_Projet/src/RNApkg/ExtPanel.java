package RNApkg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import org.apache.commons.math3.linear.RealMatrix;


@SuppressWarnings("serial")
public class ExtPanel extends JComponent{
	ArrayList<ArrayList<Point>> neuronCoords;
	ArrayList<ArrayList<int[]>> neuronPanelDimensions;
	JLayeredPane pnlAffichage;
	ArrayList<RealMatrix> NeuronWeights;
	boolean[] layerHasBiasNeuron;
	ArrayList<ArrayList<Line2D>> linesList;
	int HLindex;
	JLabel weightsLabel;
	int labelX;
	int labelY;

	public ExtPanel(ArrayList<ArrayList<Point>> neuronCoords, ArrayList<ArrayList<int[]>> neuronPanelDimensions, JLayeredPane pnlAffichage, ArrayList<RealMatrix> NeuronWeights, boolean[] layerHasBiasNeuron) {	
		super();
		this.neuronCoords = neuronCoords;
		this.neuronPanelDimensions = neuronPanelDimensions;
		this.pnlAffichage = pnlAffichage;
		this.NeuronWeights = NeuronWeights;
		this.layerHasBiasNeuron = layerHasBiasNeuron;
		this.linesList = new ArrayList<ArrayList<Line2D>>();
		this.HLindex = -1;
		this.weightsLabel = new JLabel("");
		this.labelX = 0;
		this.labelY = 0;


		// Ce MouseMotionListener permet de HighLighter les lignes d'un neurone quand le mouse passe par-dessus
		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				labelX = e.getX();
				labelY = e.getY();
				weightsLabel.setText("");
				HLindex = -1;

				for (ArrayList<Line2D> l : linesList) {
					for (Line2D line : l) {
						if (line.ptSegDist(e.getX(), e.getY()) <= 8) {
							HLindex = linesList.indexOf(l);
						}
					}
				}
				repaint();
			}
		});
	}


	@Override
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		linesList.clear();
		ArrayList<Line2D> currentList = new ArrayList<Line2D>();
		int highlightIndex = 0;

		this.setLayout(new GridLayout(0, 1));
		this.setPreferredSize(pnlAffichage.getPreferredSize());
		this.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
		this.setVisible(true);

		int[] coordsOfWeightsToFetch = new int[2];
		boolean drawLabel = false;


		// Boucle sur les layers
		for (int i=0; i<neuronCoords.size()-1; i++) {

			// Boucle sur neurones de cette layer
			for (int j=0; j<neuronCoords.get(i).size(); j++) {

				currentList.clear();

				// x0 et y0, les coordonnées du neurone de départ de la ligne
				int x0 = neuronCoords.get(i).get(j).x + (neuronPanelDimensions.get(i).get(j) [0] / 2)  + ( Math.min(Math.min(neuronPanelDimensions.get(0).get(0) [0], neuronPanelDimensions.get(0).get(0) [1]) - 10, 90) /2 ) ;
				int y0 = neuronCoords.get(i).get(j).y + (neuronPanelDimensions.get(i).get(j) [1] / 2);

				// Si la Layer suivante a un neurone Biais, on ne va pas dessiner de lignes vers ce neurone
				int drawLinesStop = 0;
				drawLinesStop = neuronCoords.get(i+1).size();

				// Si la Layer suivante a un neurone Biais, on ne va pas dessiner de lignes vers ce neurone
				try {
					if (layerHasBiasNeuron[i+1] == true) { drawLinesStop -= 1; }
				}
				catch(Exception e) { System.out.println("ExtPanel exception caught"); }


				ArrayList<Line2D> listCopy = new ArrayList<Line2D>();

				// Boucle sur tous les neurones de la layer SUIVANTE
				for (int k=0; k<drawLinesStop; k++) {	    			

					// x1 et y1, les coordonnées du neurone d'arrivée de la ligne
					int x1 = neuronCoords.get(i+1).get(k).x + (neuronPanelDimensions.get(i+1).get(0) [0] / 2) - ( Math.min(Math.min(neuronPanelDimensions.get(i+1).get(0) [0], neuronPanelDimensions.get(i+1).get(0) [1]) - 10, 90) / 2) ;
					int y1 = neuronCoords.get(i+1).get(k).y + (neuronPanelDimensions.get(i+1).get(0) [1] / 2) ;

					// On dessine la ligne entre le neurone actuel est l'un des neurones de la Layer suivante auquel il est connecté
					Double myLine = new Line2D.Double(x0, y0, x1, y1);

					g2d.setColor(Color.BLACK);
					g2d.setStroke(new BasicStroke(1));

					// Si le mouse est sur une des lignes d'un neurone, on HighLighte toutes les lignes de ce neurone en ORANGE
					if (highlightIndex == HLindex) {
						//						
						// Ces coordonnées nous servent à afficher les bons poids dans le Label
						coordsOfWeightsToFetch[0] = i;
						coordsOfWeightsToFetch[1] = j;
						drawLabel = true;

						g2d.setColor(Color.ORANGE);
						g2d.setStroke(new BasicStroke(3));
					}

					g2d.draw(myLine);
					currentList.add(myLine);	   						

					// On copie les lignes du neurone actuel dans une liste (utilisé pour le MotionListener = HighLight)
					for (int x=0; x<currentList.size(); x++) {
						listCopy.add(currentList.get(x));
					}
				}
				highlightIndex += 1;
				linesList.add(listCopy);
			}

			//////// AFFICHAGE DU LABEL AVEC LES POIDS ///////////////////////			
			if (drawLabel == true) {
				g2d.setColor(Color.ORANGE);
				g2d.setStroke(new BasicStroke(3));

				// On affiche un JLabel avec les poids des lignes HighLightees
				try {this.remove(weightsLabel);}
				catch (Exception e) {}

				String weightsLabelString = "<html>";
				try {
					double[][] weightsData = NeuronWeights.get(coordsOfWeightsToFetch[0]).transpose().getData();
					double[] currentNeuronData = weightsData[coordsOfWeightsToFetch[1]];

					for (double weight : currentNeuronData) {
						weightsLabelString += (String.valueOf(weight) + "<br/>" );	
					}
					weightsLabelString += "</html>";
				}
				catch (Exception e) {}

				weightsLabel.setText(weightsLabelString);
				weightsLabel.setForeground(new Color(230, 115, 0));
				weightsLabel.setFont(new Font("OCR A Extended", Font.BOLD, 15));
				weightsLabel.setVerticalAlignment(JLabel.TOP);
				weightsLabel.setHorizontalAlignment(JLabel.LEFT);
				weightsLabel.setBounds(labelX + 30, labelY, 180, 300);

				Rectangle myRect = new Rectangle();
				myRect.setBounds(labelX + 30, labelY, 180, 300);

				g2d.draw(myRect);
				g2d.setColor(Color.WHITE);
				g2d.fillRect(labelX + 30, labelY, 180, 300);
				g2d.setColor(Color.ORANGE);

				weightsLabel.setVisible(true);
				this.add(weightsLabel);
			}
		}
	}
}