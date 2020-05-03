package RNApkg;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartPanel;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Font;


import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.DefaultListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;


public class ApplicationWindow {

	private static final Graphics Graphics = null;
	private static final MouseListener MouseListener = null;
	private JFrame mainFrame;				// Fenetre principal
	private static TextArea txtConsoleOutput;		// Zone de Texte permettant d'afficher les sorties de la console
	private Net myNet;						// Objet Net représentant le résau de neurone artificiel créer
	private Button btnAddLayer;				// Bouton d'ajouet de couche au résau
	private Button btnPopLayer;				// Bouton pour retirer une couche du resau
	private Button btnImportData;			// Bouton permettant d'importer les données d'entrainement
	private Button btnTrain;				// Bouton permettant de commencer l'entrainment du RNA
	private Button btnStepTrain;
	private Button btnNext;
	
	int step;
	int nber_of_steps;
	int final_step_size;
	JPanel networkPanel;
	ExtPanel arrowPanel;
	
	
	private Button btnPredict;				// Bouton permettant de tester le RNA sur une nouvelle série de donées
	private Button btnPrint;				// Bouton qui ne fait rien pour l'instant
	
	private double [][] x_train;				// permet de stocker les données d'entrainement 
	private double [][] y_train;				// permet de stocker les résultats des données d'entrainements
	private double [][] train;
	private static JLayeredPane pnlAffichage;
	
	private LIMCouche coucheRNA;
	

	ArrayList<JPanel> layerPanelList;
	ArrayList<ArrayList<Point>> neuronCoords;
	ArrayList<ArrayList<int[]>> neuronPanelDimensions;
	
//	int StepTrainEpochs;
	double StepTrainLearningRate;
	int StepTrainEpochsPerStep;
	int StepTrainNumberOfSteps;
	int StepTrainLastStepSize;
	
	
	
	/* Printe la valeur dans chaque neurone de l'affichage
	 */
	public void printNeuronValues() {
		
		// Ce "if" évite de faire des divisions par zéro....
		if (myNet.netDataBase.activations.size() != 0) {
		
		
			// Boucle sur les layerPanel
			for (int i=0; i<layerPanelList.size(); i++) {
				JPanel current_layer_panel = layerPanelList.get(i);
	//			Component current_layer_panel = pnlAffichage.getComponent(i);
	//			current_layer_panel.
				for (int j=0; j<current_layer_panel.getComponentCount(); j++) {
	//				System.out.println(myNet.netDataBase.activations.get(i).getEntry(j, 0));
					JPanel current_neuron_panel = (JPanel) current_layer_panel.getComponent(j);
					
					JTextField neuronValue = new JTextField(String.format("%.4f", myNet.netDataBase.activations.get(i).getEntry(j, 0)));
					neuronValue.setEditable(false);
					current_neuron_panel.removeAll();
					current_neuron_panel.add(neuronValue);				
	//				current_neuron_panel.add(new line());
	//				current_neuron_panel.repaint();
	//				pnlAffichage.updateUI();
				}
			}
		}
		

	}
	
	
	/* Efface toutes les flèches dessinées auparavant.
	 * Utilisé quand l'application est re-dimensionnée.
	 */
	public void clearArrows() {
		neuronCoords.clear();
		neuronPanelDimensions.clear();
		System.out.println("clearArrows() INVOKED");
		
//		pnlAffichage.remove(arrowPanel);
//		pnlAffichage.remove

//		ArrayList<ArrayList<Point>> emptyList = new ArrayList<ArrayList<Point>>();
//		boolean[] temp_boolean = {true,true,true};
//	
//		// On re-crée un nouveau ExtPanel, en lui donnant des listes vides pour qu'il ne dessine aucune flèche
//	   	ExtPanel returnPanel = new ExtPanel(emptyList, neuronPanelDimensions, pnlAffichage, temp_boolean);
//  	
//  		// On remplace l'ExtPanel du PnlAffichage par ce nouveau ExtPanel vide.
//    	arrowPanel = returnPanel;
//	    arrowPanel.setLayout(new GridLayout(0, 1));
//	    arrowPanel.setPreferredSize(pnlAffichage.getPreferredSize());
//	    arrowPanel.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
//	    arrowPanel.setVisible(true);
//	    pnlAffichage.add(arrowPanel, JLayeredPane.POPUP_LAYER);
	    
	    pnlAffichage.updateUI();
	}
  
    
    /* Dessine les flèches connectant les neurones du réseau.
	 */
    public void drawArrows() {
    	neuronCoords.clear();
		neuronPanelDimensions.clear();
		
		// Boucle sur les layerPanel
		for (int i=0; i<layerPanelList.size(); i++) {
			JPanel current_layer_panel = layerPanelList.get(i);
			ArrayList<Point> layer_points = new ArrayList<Point>();
			ArrayList<int[]> dimensionsList = new ArrayList<int[]>();

			// Boucle sur les panel Neurone de chaque layerPanel
			for (int j=0; j<current_layer_panel.getComponentCount(); j++) {
				JPanel current_neuron_panel = (JPanel) current_layer_panel.getComponent(j);
				
				// On garde les dimensions de chaque Panel
				int[] panelDimensions = new int[2];
				panelDimensions[0] = current_neuron_panel.getWidth();
				panelDimensions[1] = current_neuron_panel.getHeight();
				dimensionsList.add(Arrays.copyOf(panelDimensions, 2));
		

				// On garde aussi les coordonnées de chaque panel.
				// Mais on veut ces coordonnées par rapport au système de coords. de PnlAffichage, 
				// donc on utilise la méthode .convertPoint()
				Point convertedPoint = SwingUtilities.convertPoint(current_neuron_panel, new Point(0,0), pnlAffichage);
				layer_points.add((Point)convertedPoint.clone());

			}
			// On garde le tout dans des listes
			neuronCoords.add(layer_points);
			neuronPanelDimensions.add(dimensionsList);
		}
		// POUR DEBUG
//		for (int i=0; i<neuronCoords.size(); i++) {
//			System.out.println("\n layer " + i);
//			for (int j=0; j<neuronCoords.get(i).size(); j++) {
//				System.out.println(neuronCoords.get(i).get(j));
//			}
//		}
//		
//		for (int i=0; i<neuronPanelDimensions.size(); i++) {
//			System.out.println("\n DIMENSIONS " + i);
//			for (int j=0; j<neuronPanelDimensions.get(i).size(); j++) {
//				System.out.println(Arrays.toString(neuronPanelDimensions.get(i).get(j)));
//			}
//		}
		
		
//		// On doit passer au constructeur de ExtPanel l'info sur les neurones Biais du réseau
		boolean[] layerHasBiasNeuron = new boolean[myNet.layers.size()];
		for (Layer l : myNet.layers) {
			if (l.hasBiasNeuron == true) {
				layerHasBiasNeuron[myNet.layers.indexOf(l)] = true;
			}
			else {layerHasBiasNeuron[myNet.layers.indexOf(l)] = false;}
		}
		System.out.println(Arrays.toString(layerHasBiasNeuron));
		
		try {
			pnlAffichage.remove(arrowPanel);
			System.out.println("ArrowPanel removed");
		}
		catch(Exception e) {
		  System.out.println("no ArrowPanel to remove");
		}
		System.out.println("got after");
		
		// On appelle le constructeur de ExtPanel, en lui donnant les infos sur les coordonnées/dimensions des panels
    	ExtPanel returnPanel = new ExtPanel(neuronCoords, neuronPanelDimensions, pnlAffichage, layerHasBiasNeuron);
   	
    	arrowPanel = returnPanel;
	    arrowPanel.setLayout(new GridLayout(0, 1));
	    arrowPanel.setPreferredSize(pnlAffichage.getPreferredSize());
	    arrowPanel.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
	    arrowPanel.setVisible(true);
	    pnlAffichage.add(arrowPanel, JLayeredPane.POPUP_LAYER);
	    
	    ////////////////////// DEBUG ////////////////////////
//	    System.out.println(pnlAffichage.getComponentCount());
	    
	    
	       // We need to remove old mouselisteners
//		   try {
//		   java.awt.event.MouseMotionListener[] mouseListenersList = pnlAffichage.getMouseMotionListeners();
//			   for (MouseMotionListener ml : mouseListenersList) {
//			   		pnlAffichage.removeMouseMotionListener(ml);	
//			   }
//		   }
//		   catch (Exception e) {
//		   }
//
//		   
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
//				    	g2d.setColor(Color.BLUE);
//				    	g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//				    	
//				    	Double myLine = new Line2D.Double( l.getX1(), l.getY1(), l.getX2(), l.getY2());
////				    	g2d.set
////				    	g2d.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
//				    	g2d.draw(myLine);
////				    	pnlAffichage.updateUI();
//				    	pnlAffichage.repaint();
//			    	}
//			    }
//		    }
////
////			
////	    public void mouseClicked(MouseEvent e) {
////		    	int x = e.getXOnScreen();
////		    	int y = e.getYOnScreen();
////		    	
////			    for (Line2D l : linesList) {
//////			    	if (l.intersects(x-20, y-20, x+20, y+20)) {
////			    	if (l.ptSegDist(e.getX(), e.getY()) <= 10) {
////				    	System.out.println("SECTTTTTTTTTTTTTTT");
//////				    	Point2D start = l.getP1();
//////				    	Point2D end = l.getP2();
//////				    	int x0 = (int) l.getX1();
////				    	g2d.setColor(Color.BLUE);
////				    	g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//////				    	g2d.set
////				    	g2d.drawLine((int) l.getX1(), (int) l.getY1(), (int) l.getX2(), (int) l.getY2());
////				    	pnlAffichage.updateUI();
////			    	}
////			    }
////		    	
////		    	System.out.println("x " + x + " y " + y);  
////			}
////			
////			public void mousePressed(MouseEvent e) {
////				System.out.println("Mouse pressed; # of clicks: "
////			                    + e.getClickCount());
////			    }
////
////			    public void mouseReleased(MouseEvent e) {
////			    	System.out.println("Mouse released; # of clicks: "
////			                    + e.getClickCount());
////			    }
////
////			    public void mouseEntered(MouseEvent e) {
////			    	System.out.println("Mouse entered");
////			    	
////
////				    for (Line2D l : linesList) {
//////					    	createStrokedShape(l);
////				    	
//////					    	if (l.intersects(x-20, y-20, x+20, y+20)) {
////				    	if (l.ptSegDist(e.getX(), e.getY()) <= 10) {
////					    	System.out.println("SECTTTTTTTTTTTTTTT");	
////				    	}
////				    }
////
////
////			    }
////
////			    public void mouseExited(MouseEvent e) {
////			    	System.out.println("Mouse exited");
////			    }
////  
//	    };
//	    
////		this.addMouseListener(ml);
//		pnlAffichage.addMouseMotionListener(ml);
	    
	    
	    
	    
	    pnlAffichage.updateUI();
	    

    }
    

	
	/* Printe les couches du RNA et les neurones dans chaque couche.
	 * Chaque neurone est un sous-panel dans un layerPanel.
	 */
	public void drawLayerPanels() {	
		System.out.println("drawLayerPanels() INVOKED");	
		pnlAffichage.removeAll();
		layerPanelList.clear();
		int index = 0;
		
		if (myNet.layers.size() != 0) {
			// CALCUL DES DIMENSIONS POUR LES PANELS/////////////////
			int panel_width = pnlAffichage.getWidth() / myNet.layers.size();
			int panel_height = 0;
			panel_height = (pnlAffichage.getHeight());
	//		System.out.println("panel_width : " + panel_width);
	//		System.out.println("panel_height : " + panel_height);
			//  /CALCUL DES DIMENSIONS POUR LES PANELS///////////////
	
			// Boucle sur les Layers
			for (Layer l : myNet.layers) {
				ArrayList<int[]> list = new ArrayList<int[]>();
				
				// Implémente les sous-panel qui représentent chaque layer
				JPanel layerPanel = new JPanel();
				layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));
				
				// On rajoute une bordure à ces sous-panels
				Border compound;
				String panelNumber = Integer.toString(pnlAffichage.getComponentCount());
				compound = BorderFactory.createTitledBorder("Layer" + panelNumber);
				layerPanel.setBorder(compound);
				
				// On dessinera les BiasNeuron d'une autre couleur
				int layer_regular_neuron_count = l.layerSize;
				if (l.hasBiasNeuron)  {layer_regular_neuron_count -= 1;}
				DrawPanel neuronPanel = new DrawPanel(false);
				
				// Crée les panels pour chaque RegularNeuron
				for (int i=0; i<layer_regular_neuron_count; i++) {
				// L'attribut "false" fait qu'on dessine un neurone en BLEU
					neuronPanel = new DrawPanel(false);
	
					// On garde les coordonnées du RegularNeuron
					int x = neuronPanel.getX();
					int y = neuronPanel.getY();
					layerPanel.add(neuronPanel);
					
					int[] coords = new int[2];
					coords[0] = x;
					coords[1] = y;
					int[] stored_coords = coords;
					list.add(stored_coords.clone());
				}
					
				// Rajoute les BiasNeuron
				if (l.hasBiasNeuron) {
				// L'attribut "true" fait qu'on dessine un neurone en ROSE
					neuronPanel = new DrawPanel(true);
					layerPanel.add(neuronPanel);
					
					// On garde les coordonnées du BiasNeuron
					int x = neuronPanel.getX();
					int y = neuronPanel.getY();
					layerPanel.add(neuronPanel);
					
					int[] coords = new int[2];
					coords[0] = x;
					coords[1] = y;
					int[] stored_coords = coords;
					list.add(stored_coords.clone());
				}
				layerPanel.setBounds( ( (pnlAffichage.getWidth()/myNet.layers.size()) * index) , 0, panel_width, panel_height/*(100 * l.layerSize)*/ );
				
				pnlAffichage.add(layerPanel, JLayeredPane.DEFAULT_LAYER);
				layerPanelList.add(layerPanel);
				
				// DO WE NEED THIS ????????
//				layerPanel.updateUI();
//				pnlAffichage.updateUI();
				
				index += 1;
			}
		}
	}
	

	
	/*
	 * Lancement de l'application
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApplicationWindow window = new ApplicationWindow();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	

	/*
	 * Permet de créer la fenêtre
	 * 
	 */
	public ApplicationWindow() {
		initialize();
	}



	/**
	 * Création et initialisation des différent composants de la fenêtre
	 * 
	 */
	private void initialize() {	
		
		

		layerPanelList = new ArrayList<JPanel>();
		neuronCoords = new ArrayList<ArrayList<Point>>();
		neuronPanelDimensions = new ArrayList<ArrayList<int[]>>();
		
//		StepTrainEpochs = 0;
		StepTrainLearningRate = 0.0;
		StepTrainEpochsPerStep = 0;
		StepTrainNumberOfSteps = 0;
		StepTrainLastStepSize = 0;
		
		//===fenêtre principale===
		mainFrame = new JFrame();
		mainFrame.setTitle("Projet RNA");
		mainFrame.setBounds(100, 100, 800, 610);
//		mainFrame.setBounds(100, 100, 1200, 900);
//		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BoxLayout(mainFrame, BoxLayout.X_AXIS));
		
		mainFrame.getContentPane().setLayout(new MigLayout("", "[grow][10px:n][fill]", "[50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][10px:n,grow][grow]"));
		
		
		
		
		// Permet de re-dessiner les panels de neurones quand on change la taille de la fenetre
		mainFrame.addComponentListener( new ComponentAdapter() {
			@Override
            public void componentResized(ComponentEvent e) {
                System.out.println("Window Resized: Frame");
//              mainFrame.removeAll();
//				mainFrame.remove(mainFrame.getComponent(0).get);
//				arrowPanel.removeAll();
//				clearArrows();
                drawLayerPanels();
                drawArrows();
                printNeuronValues();
            }
		});
		
		
		
		
		
		//===Liste des couches===
		coucheRNA = new LIMCouche();
		
		//===Panneau d'affichage===
		pnlAffichage = new JLayeredPane();
		pnlAffichage.setBounds(0, 0, 700, 610);
//		pnlAffichage.setLayout(new BoxLayout(pnlAffichage, BoxLayout.X_AXIS));
		 pnlAffichage.setLayout(null);
		// STACKOVERFLOW ALTERNATIVE
//		pnlAffichage.setLayout(new LayeredPaneLayout(pnlAffichage));
		
		
		networkPanel = new JPanel();
		networkPanel.setLayout(new BoxLayout(networkPanel, BoxLayout.X_AXIS));
		networkPanel.setPreferredSize(new Dimension(pnlAffichage.getX(), pnlAffichage.getY()));
		networkPanel.setBackground(Color.PINK);
		Border compound = BorderFactory.createTitledBorder("networkPanel");
		networkPanel.setBorder(compound);
		networkPanel.setVisible(true);
		
		
		mainFrame.getContentPane().add(pnlAffichage, "cell 0 0 1 7,grow");
		
		
		
		
		
//// BOUTON D' AJOUT DE COUCHE /////////////////////////////////////////////////////////////////////////////////
		btnAddLayer = new Button("addLayer");
		btnAddLayer.addActionListener(new ActionListener() {
			/*
			 * Le RNA (Net.java) doit avoir été créer avant d'utiliser le bouton
			 * voir au fond après la création de l'interface graphique
			 */
			public void actionPerformed(ActionEvent e) {
				
				//======Création du pop-up d'ajout de Layer======
				//===Liste type de layer===
				DefaultListModel<String> dlmLayer = new DefaultListModel<String>();
				dlmLayer.addElement("input");
				dlmLayer.addElement("hidden");
				dlmLayer.addElement("output");
				JList<String> lstTypeLayer = new JList<String>(dlmLayer);
				lstTypeLayer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				lstTypeLayer.setSelectedIndex(0);
				lstTypeLayer.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
				
				//===Liste type d'activation===
				DefaultListModel<String> dlmFoncActiv = new DefaultListModel<String>();
				dlmFoncActiv.addElement("sigmoid");
				dlmFoncActiv.addElement("relu");
				dlmFoncActiv.addElement("lrelu");
				JList<String> lstFonctActiv = new JList<String>(dlmFoncActiv);
				lstFonctActiv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				lstFonctActiv.setSelectedIndex(0);
				lstFonctActiv.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
				
				//===Séléction du nombre de neurone de la couche===
				JTextField txtNbrNeurone = new JTextField("1");
				
				//===radio bouton Neurone de biais
				JRadioButton radNBiaisYes = new JRadioButton("Oui");				
				JRadioButton radNBiaisNo = new JRadioButton("Non");
				ButtonGroup grpRadNBiais = new ButtonGroup();
				grpRadNBiais.add(radNBiaisYes);
				grpRadNBiais.add(radNBiaisNo);
				radNBiaisYes.setSelected(true);
				
				//JTextField firstName = new JTextField();
				final JComponent[] inputs = new JComponent[] {
				        new JLabel("Couche"),
				        lstTypeLayer,
				        new JLabel("Foction d'activation"),
				        lstFonctActiv,
				        new JLabel("Nbr de neurone"),
				        txtNbrNeurone,
				        new JLabel("Neurone de biais"),
				        radNBiaisYes,radNBiaisNo
				};
				int result = JOptionPane.showConfirmDialog(null, inputs, "Add layer", JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					int nbrNeurone;
					try {
						nbrNeurone = Integer.parseInt(txtNbrNeurone.getText());
					}
					catch (NumberFormatException e1)
					{
						nbrNeurone = 0;
					}
					
					Layer new_layer = myNet.addLayer(lstTypeLayer.getSelectedValue(), lstFonctActiv.getSelectedValue(), nbrNeurone, radNBiaisYes.isSelected());
					coucheRNA.addLayer(new_layer);
					txtConsoleOutput.append("\n Layer : " + lstTypeLayer.getSelectedValue() + " / Activation : " + lstFonctActiv.getSelectedValue() + " / Nombre de neurone : " + txtNbrNeurone.getText() + " / Biais :" +  radNBiaisYes.isSelected() );
					
					///////// Dessine les layerPanel ////////////////
//					clearArrows();
					drawLayerPanels();
					drawArrows();
					///////// Dessine les layerPanel ////////////////
					
					
					
					btnPopLayer.setEnabled(true);
					btnImportData.setEnabled(true);
					btnPrint.setEnabled(true);
				} 
				else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
			}
		});
		mainFrame.getContentPane().add(btnAddLayer, "cell 2 0,grow");
		
		
		
		
		
//// BOUTON DE SUPPRESSION DE COUCHE /////////////////////////////////////////////////////////////////////////////////
		btnPopLayer = new Button("popLayer");
		btnPopLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//===Liste des Layers===
				JList<Layer> lstLayers = new JList<Layer>(coucheRNA);
				lstLayers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				lstLayers.setSelectedIndex(0);
				lstLayers.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// TODO Auto-generated method stub				
					}
				});
				
				//JTextField firstName = new JTextField();
				final JComponent[] inputs = new JComponent[] {
				        new JLabel("Couche"),
				        lstLayers
				};
				int result = JOptionPane.showConfirmDialog(null, inputs, "Pop Layer", JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					System.out.println(result);
					System.out.println("couche à supprimer : " + lstLayers.getSelectedValue().toString());
				} else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
			}
		});
		mainFrame.getContentPane().add(btnPopLayer, "cell 2 1,grow");
		btnPopLayer.setEnabled(false);
		
		
		
		
		
//// BOUTON D' IMPORTATION DE DONNEES /////////////////////////////////////////////////////////////////////////////////
		btnImportData = new Button("Importer donn\u00E9es d'input");
		btnImportData.setEnabled(false);
		btnImportData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {				
				final JFileChooser fc = new JFileChooser();
				int donneesImportees = fc.showOpenDialog(mainFrame);
				
				 if (donneesImportees == JFileChooser.APPROVE_OPTION) {
					 File file = fc.getSelectedFile();
					 String filePath = file.getAbsolutePath();					  
						
					// Implémentation du Dialog pour l'importation de données ///////////////////////////////////////////
					
					//===radio bouton Neurone de biais===
					JRadioButton radLigne1Oui = new JRadioButton("Oui");				
					JRadioButton radLigne1Non = new JRadioButton("Non");
					ButtonGroup groupeRad = new ButtonGroup();
					groupeRad.add(radLigne1Oui);
					groupeRad.add(radLigne1Non);
					radLigne1Oui.setSelected(true);
					
					//===Liste du nombre d'outputs pour chaque ligne de données===
					DefaultListModel<Integer> dlmOutputs = new DefaultListModel<Integer>();
					dlmOutputs.addElement(1);
					dlmOutputs.addElement(2);
					dlmOutputs.addElement(3);
					dlmOutputs.addElement(4);
					dlmOutputs.addElement(5);
					JList<Integer> listeOutputs = new JList<Integer>(dlmOutputs);
					listeOutputs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					listeOutputs.setSelectedIndex(0);
					
					// Rassemble les choix du Dialog
					final JComponent[] ImportationDonnees = new JComponent[] {
					        new JLabel("Ignorer la 1ere ligne du fichier?"),
					        radLigne1Oui, radLigne1Non,
					        new JLabel("Nbe d'outputs / exemple d'entrainement"),
					        listeOutputs
					};
					int result = JOptionPane.showConfirmDialog(null, ImportationDonnees, "Importation : " + file.getName(), JOptionPane.PLAIN_MESSAGE);
					
					// /Implémentation du Dialog pour l'importation de données ///////////////////////////////////////////
				 
					 // On appelle la méthode importCSV() avec les paramètres choisis par l'utilisateur dans le 
					 // Dialog ci-dessus.
					if (result == JOptionPane.OK_OPTION) {
						ArrayList<double[][]> donneesInput = myNet.importCSV(filePath, radLigne1Oui.isSelected(), listeOutputs.getSelectedValue());
						x_train = donneesInput.get(0);
						y_train = donneesInput.get(1);
						
						txtConsoleOutput.append("\n");
						txtConsoleOutput.append("\n Importations efféctuées ( " + file.getName() + " )");
						
						
						btnTrain.setEnabled(true);
						btnStepTrain.setEnabled(true);
					}
					else {
					    System.out.println("User canceled / closed the dialog, result = " + result);
					}
				 }
			}
		});
		mainFrame.getContentPane().add(btnImportData, "cell 2 2,grow");
		
		
		
		
//// BOUTON D'ENTRAINEMENT DU RESEAU /////////////////////////////////////////////////////////////////////////////////////////
		btnTrain = new Button("train");
		btnTrain.setEnabled(false);
		btnTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JTextField txtNbrEpochs = new JTextField("5000");
				JTextField txtLearningRate = new JTextField("0.5");
				
				// Rassemble les choix du Dialog
				final JComponent[] Entrainement = new JComponent[] {
				        new JLabel("Nombre d'epochs d'entraînement : "),
				        txtNbrEpochs,
				        new JLabel("learning_rate : "),
				        txtLearningRate
				};
				int result = JOptionPane.showConfirmDialog(null, Entrainement, "Train network", JOptionPane.PLAIN_MESSAGE);
				
				if (result == JOptionPane.OK_OPTION) {
					myNet.train(x_train, y_train, Integer.parseInt(txtNbrEpochs.getText()), Double.parseDouble(txtLearningRate.getText()));
					
					// On appelle la methode errorGraph()
					ChartPanel ErrorChartPanel = myNet.errorGraph();
					
					// On rajoute le graphe dans un JDialog séparé de la fenetre principale
					JPanel chartPanel = new JPanel();
					chartPanel.add(ErrorChartPanel);
					
					JDialog chartDialog = new JDialog();
					chartDialog.setLayout(new FlowLayout(BoxLayout.X_AXIS));
	
					chartDialog.add(chartPanel);
					chartDialog.setSize(chartDialog.getPreferredSize());
					chartDialog.setVisible(true);
					
					
					// On rajoute le graphe retourné par cette méthode au Panel d'affichage
//					pnlAffichage.removeAll();
//					pnlAffichage.add(ErrorChartPanel);
					
					
					pnlAffichage.validate();
					
					btnPredict.setEnabled(true);
					btnPrint.setEnabled(true);
					btnStepTrain.setEnabled(false);
					btnAddLayer.setEnabled(false);
					btnPopLayer.setEnabled(false);
					
				} else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
				//txtConsoleOutput.append(txtConsoleOutput.toString());
				//txtConsoleOutput.append("\n\n" + System.in.toString());
				
				//txtConsoleOutput.append("\n\n" + myNet.networkError.toString());
			}
		});
		mainFrame.getContentPane().add(btnTrain, "cell 2 3,grow");
		
		
		
//// BOUTON DE STEP-TRAIN /////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
		btnStepTrain = new Button("Step-train");		
		btnStepTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				

				
				
				JTextField txtNbrEpochs = new JTextField("5000");
				JTextField txtLearningRate = new JTextField("0.5");
				JTextField NbeEpochsParStep = new JTextField("1000");
				
				// Rassemble les choix du Dialog
				final JComponent[] Entrainement = new JComponent[] {
				        new JLabel("Nombre d'epochs d'entraînement : "),
				        txtNbrEpochs,
				        new JLabel("learning_rate : "),
				        txtLearningRate,
				        new JLabel("Epochs / Step : "),
				        NbeEpochsParStep
				};
				int result = JOptionPane.showConfirmDialog(null, Entrainement, "Step-Train", JOptionPane.PLAIN_MESSAGE);
				
				if (result == JOptionPane.OK_OPTION) {
					StepTrainEpochsPerStep = Integer.parseInt(NbeEpochsParStep.getText());
					StepTrainLearningRate = Double.parseDouble(txtLearningRate.getText());
					
					StepTrainNumberOfSteps = Integer.parseInt(txtNbrEpochs.getText()) / Integer.parseInt(NbeEpochsParStep.getText()) - 1;
					System.out.println(StepTrainNumberOfSteps);
					StepTrainLastStepSize = Integer.parseInt(txtNbrEpochs.getText()) % Integer.parseInt(NbeEpochsParStep.getText());

				
				
					// Le système fait déjà le premier Step d'entrainement
					myNet.train(x_train, y_train, Integer.parseInt(NbeEpochsParStep.getText()), Double.parseDouble(txtLearningRate.getText()) );
					
					// On affiche les valeurs des neurones suite à ce premier Step
					printNeuronValues();
					
					
//				// POUR DEBUGGER PLUS RAPIDEMENT ///////////////////////////////////
//				// Changer le "false" dans la ligne sous ce bloc -->  btnStepTrain.setEnabled(false);
//
//				btnNext.setEnabled(true);
//				myNet.addLayer("input", "sigmoid", 2, true);
//				myNet.addLayer("hidden", "sigmoid", 4, true);
//				myNet.addLayer("output", "sigmoid", 1, false);
//				
//				StepTrainLearningRate = 0.5;
//				StepTrainEpochsPerStep = 900;
//				StepTrainNumberOfSteps = 5;
//				StepTrainLastStepSize = 500;
//				
//				ArrayList<double[][]> donneesInput = myNet.importCSV("C:\\Users\\haas_\\Downloads\\P.O.O\\XOR_data.csv", true, 1);
//				x_train = donneesInput.get(0);
//				y_train = donneesInput.get(1);
//				
//				// Premier step automatique
//				myNet.train(x_train, y_train, StepTrainEpochsPerStep, StepTrainLearningRate);
//				
//				clearArrows();
//				drawLayerPanels();
//				drawArrows();
				// POUR DEBUGGER PLUS RAPIDEMENT ///////////////////////////////////
					
					
					
					
					// On désactive les boutons suivants
					btnStepTrain.setEnabled(false);
					btnTrain.setEnabled(false);
					btnAddLayer.setEnabled(false);
					btnPopLayer.setEnabled(false);
					
					btnNext.setEnabled(true);
				}
				else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
				
		

	
			}
		});		
		mainFrame.getContentPane().add(btnStepTrain, "cell 2 4,grow");
		btnStepTrain.setEnabled(false);
		
//// /BOUTON DE STEP-TRAIN /////////////////////////////////////////////////////////////////////////////////////////////////////////		
		
		
		
//// /BOUTON NEXT /////////////////////////////////////////////////////////////////////////////////////////////////////////	
		btnNext = new Button("Next_Step");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// Si l'on est pas au dernier Step, entrainer sur un Step complet
				if (StepTrainNumberOfSteps > 0) {
					myNet.stepTrain(x_train, y_train, StepTrainEpochsPerStep, StepTrainLearningRate);
					printNeuronValues();
					StepTrainNumberOfSteps -= 1;
				}
	
	
				// Si l'on est au dernier step, on re-entraine avec le nombre d'epochs restantes
				if (StepTrainNumberOfSteps == 0) {
					if (StepTrainLastStepSize > 0) {
						myNet.stepTrain(x_train, y_train, StepTrainLastStepSize, StepTrainLearningRate);
						printNeuronValues();
					}
					btnNext.setEnabled(false);
				}
				
			
					// On appelle la methode errorGraph()
					ChartPanel ErrorChartPanel = myNet.errorGraph();
					
					// On rajoute le graphe dans un JDialog séparé de la fenetre principale
					JPanel chartPanel = new JPanel();
					chartPanel.add(ErrorChartPanel);
					
					JDialog chartDialog = new JDialog();
					chartDialog.setLayout(new FlowLayout(BoxLayout.X_AXIS));
					chartDialog.add(chartPanel);
					chartDialog.setSize(chartDialog.getPreferredSize());
					chartDialog.setVisible(true);	
			}
		});		
		mainFrame.getContentPane().add(btnNext, "cell 2 5,grow");
		btnNext.setEnabled(false);
		
//// /BOUTON NEXT /////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
		
		
		
		
		
//// BOUTON DE PREDICTION SUR DE NOUVELLES DONNEES /////////////////////////////////////////////////////////////////////////////////
	// Il prend un fichier .csv , comme le bouton "ImporterDonnes" 
		btnPredict = new Button("predict");
		btnPredict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				//// ON REUTILISE ICI LE CODE POUR LA DIALOG BOX DE "IMPORTER DONNEES" ////////////
				final JFileChooser fc = new JFileChooser();
				int donneesImportees = fc.showOpenDialog(mainFrame);
				
				 if (donneesImportees == JFileChooser.APPROVE_OPTION) {
					 File file = fc.getSelectedFile();
					 String filePath = file.getAbsolutePath();
				
						// Implémentation du Dialog pour lA PREDICTION de données ///////////////////////////////////////////
						
						//===radio bouton Neurone de biais===
						JRadioButton radLigne1Oui = new JRadioButton("Oui");				
						JRadioButton radLigne1Non = new JRadioButton("Non");
						ButtonGroup groupeRad = new ButtonGroup();
						groupeRad.add(radLigne1Oui);
						groupeRad.add(radLigne1Non);
						radLigne1Oui.setSelected(true);
						
						//===Liste du nombre d'outputs pour chaque ligne de données===
						DefaultListModel<Integer> dlmOutputs = new DefaultListModel<Integer>();
						dlmOutputs.addElement(1);
						dlmOutputs.addElement(2);
						dlmOutputs.addElement(3);
						dlmOutputs.addElement(4);
						dlmOutputs.addElement(5);
						JList<Integer> listeOutputs = new JList<Integer>(dlmOutputs);
						listeOutputs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						listeOutputs.setSelectedIndex(0);
				
						// Rassemble les choix du Dialog
						final JComponent[] ImportationDonnees = new JComponent[] {
						        new JLabel("Ignorer la 1ere ligne du fichier?"),
						        radLigne1Oui, radLigne1Non,
						        new JLabel("Nbe d'outputs / exemple d'entrainement"),
						        listeOutputs
						};
						
					 int result = JOptionPane.showConfirmDialog(null, ImportationDonnees, "Importation : " + file.getName(), JOptionPane.PLAIN_MESSAGE);
				
					 // On appelle la méthode importCSV() avec les paramètres choisis par l'utilisateur dans le 
					 // Dialog ci-dessus.
					if (result == JOptionPane.OK_OPTION) {
						double[][] prediction = myNet.testNetwork(filePath, radLigne1Oui.isSelected(), listeOutputs.getSelectedValue());
						
						txtConsoleOutput.append("\n PREDICTIONS : \n");
						txtConsoleOutput.append("\n" + Arrays.deepToString(prediction) + "\n");
						btnTrain.setEnabled(true);
					}
					else {
					    System.out.println("User canceled / closed the dialog, result = " + result);
					}
				 }
			}
		});
		btnPredict.setEnabled(false);
		mainFrame.getContentPane().add(btnPredict, "cell 2 6,grow");
		
		
		
		
//// AFFICHAGE /////////////////////////////////////////////////////////////////////////////////
		btnPrint = new Button("print");
		btnPrint.setEnabled(false);
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtConsoleOutput.append("\n");
				txtConsoleOutput.append(myNet.print());
				//myNet.print();
				//myNet.errorGraph();
			}
		});
		mainFrame.getContentPane().add(btnPrint, "cell 2 6,grow");
		
		txtConsoleOutput = new TextArea();
		txtConsoleOutput.setFont(new Font("OCR A Extended", Font.BOLD, 16));
		txtConsoleOutput.setEditable(false);
		txtConsoleOutput.setForeground(Color.GREEN);
		txtConsoleOutput.setBackground(Color.BLACK);
		txtConsoleOutput.setText("Sortie Console :");
		mainFrame.getContentPane().add(txtConsoleOutput, "cell 0 7 3 1,grow");
		
		
		
		
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//===création du résaux de neurone===
		myNet = new Net();
//		txtConsoleOutput.append("\n Résaux de neurones créé");
		
	}
	
	public static void ConsoleOutputAppend (String newText) {
		txtConsoleOutput.append(newText);
	}
	
	
	

	
	
	
	
	
	
}
