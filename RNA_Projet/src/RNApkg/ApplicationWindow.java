package RNApkg;
//testy.....

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import net.miginfocom.swing.MigLayout;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Font;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;


public class ApplicationWindow {

	private static final Graphics Graphics = null;
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
	private JLabel lblStartup;
	
	private LIMCouche coucheRNA;
	

	ArrayList<JPanel> layerPanelList;
//	ArrayList<ArrayList<DrawPanel>> neuronPanels;
	ArrayList<ArrayList<Point>> neuronCoords;
	ArrayList<ArrayList<int[]>> neuronPanelDimensions;
	
	/* Printe la valeur dans chaque neurone de l'affichage
	 */
	public void printNeuronValues() {
		
		// Boucle sur les layerPanel
		System.out.println("pnlAffichage.getComponentCount()" + pnlAffichage.getComponentCount());
		for (int i=0; i<layerPanelList.size(); i++) {
			JPanel current_layer_panel = layerPanelList.get(i);
//			Component current_layer_panel = pnlAffichage.getComponent(i);
//			current_layer_panel.
			for (int j=0; j<current_layer_panel.getComponentCount(); j++) {
				System.out.println(myNet.netDataBase.activations.get(i).getEntry(j, 0));
				JPanel current_neuron_panel = (JPanel) current_layer_panel.getComponent(j);
				
				JTextField neuronValue = new JTextField(String.format("%.4f", myNet.netDataBase.activations.get(i).getEntry(j, 0)));
				current_neuron_panel.removeAll();
				current_neuron_panel.add(neuronValue);
				
//				current_neuron_panel.add(new line());
////			current_neuron_panel.repaint();
//				pnlAffichage.updateUI();
			}
		}
	}
	
	

	public void clearArrows() {
		
//		pnlAffichage.remove(arrowPanel);



		neuronCoords.clear();
		neuronPanelDimensions.clear();
//		arrowPanel = drawArrows();

		ArrayList<ArrayList<Point>> emptyList = new ArrayList<ArrayList<Point>>();
//		
	   	ExtPanel returnPanel = new ExtPanel(emptyList, neuronPanelDimensions, pnlAffichage);
//    	
    	arrowPanel = returnPanel;
	    arrowPanel.setLayout(new GridLayout(0, 1));
	    arrowPanel.setPreferredSize(pnlAffichage.getPreferredSize());
	    arrowPanel.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
//	    testPanel.setBackground(Color.PINK);
	    arrowPanel.setVisible(true);
	    pnlAffichage.add(arrowPanel, JLayeredPane.POPUP_LAYER);
	    
//	    testPanel.setMinimumSize(new Dimension(300, 300));
	    
	    pnlAffichage.updateUI();
	}
  
    
    public ExtPanel drawArrows() {
    	

//    	
//    	System.out.println("neuronCOORDS :");
//    	System.out.println(Arrays.toString(neuronCoords.get(0).get(2)));
    	
		// Boucle sur les layerPanel
		for (int i=0; i<layerPanelList.size(); i++) {
			JPanel current_layer_panel = layerPanelList.get(i);
			ArrayList<Point> layer_points = new ArrayList<Point>();
			ArrayList<int[]> dimensionsList = new ArrayList<int[]>();
//			Component current_layer_panel = pnlAffichage.getComponent(i);
//			current_layer_panel.


			for (int j=0; j<current_layer_panel.getComponentCount(); j++) {
				JPanel current_neuron_panel = (JPanel) current_layer_panel.getComponent(j);
				
//				int panelWidth = new int;
//				int panelHeight = new int;
//				int panelWidth = current_neuron_panel.getWidth();
//				int panelHeight = current_neuron_panel.getHeight();
				
				
//				int[] clonedList = new int[2];
				int[] panelDimensions = new int[2];
				panelDimensions[0] = current_neuron_panel.getWidth();
				panelDimensions[1] = current_neuron_panel.getHeight();
				
//				String one = Integer.toString(panelWidth);
//				String two = Integer.toString(panelHeight);
//				int[] test = new int[2];
//				test[0] = Integer.parseInt(one);
//				test[1] = Integer.parseInt(two);
				
				
//				clonedList = panelDimensions.clone();
//				dimensionsList.add(panelDimensions.clone());
				dimensionsList.add(Arrays.copyOf(panelDimensions, 2));
		

				Point convertedPoint = SwingUtilities.convertPoint(current_neuron_panel, new Point(0,0), pnlAffichage);
//				System.out.println(convertedPoint);
				layer_points.add((Point)convertedPoint.clone());
				
//				current_neuron_panel.add(new line());
////			current_neuron_panel.repaint();
//				pnlAffichage.updateUI();
			}
			neuronCoords.add(layer_points);
			neuronPanelDimensions.add(dimensionsList);
		}
		
		for (int i=0; i<neuronCoords.size(); i++) {
			System.out.println("\n layer " + i);
			for (int j=0; j<neuronCoords.get(i).size(); j++) {
				System.out.println(neuronCoords.get(i).get(j));
			}
		}
		
		for (int i=0; i<neuronPanelDimensions.size(); i++) {
			System.out.println("\n DIMENSIONS " + i);
			for (int j=0; j<neuronPanelDimensions.get(i).size(); j++) {
				System.out.println(Arrays.toString(neuronPanelDimensions.get(i).get(j)));
			}
		}
		
		
    	ExtPanel returnPanel = new ExtPanel(neuronCoords, neuronPanelDimensions, pnlAffichage);
    	
    	arrowPanel = returnPanel;
	    arrowPanel.setLayout(new GridLayout(0, 1));
	    arrowPanel.setPreferredSize(pnlAffichage.getPreferredSize());
	    arrowPanel.setBounds(0, 0, pnlAffichage.getWidth(), pnlAffichage.getHeight());
//	    testPanel.setBackground(Color.PINK);
	    arrowPanel.setVisible(true);
	    pnlAffichage.add(arrowPanel, JLayeredPane.POPUP_LAYER);
//	    testPanel.setMinimumSize(new Dimension(300, 300));
	    
	    pnlAffichage.updateUI();
		return returnPanel;
    	
    }
    

	
	/* Printe les couches du RNA et les neurones dans chaque couche.
	 * Chaque neurone est un sous-panel dans un layerPanel.
	 */
	public void drawLayerPanels() {
		
		pnlAffichage.removeAll();
		layerPanelList.clear();
		
		// CALCUL DES DIMENSIONS POUR LES PANELS/////////////////
		int panel_width = pnlAffichage.getWidth() / myNet.layers.size();
		int panel_height = 0;
//		for (Layer l : myNet.layers) {
//			panel_height = Math.max(panel_height, l.layerSize);
//		}
//		panel_height *= 100;
		panel_height = (pnlAffichage.getHeight());
		System.out.println("panel_width : " + panel_width);
		System.out.println("panel_height : " + panel_height);
		//  /CALCUL DES DIMENSIONS POUR LES PANELS///////////////

		int index = 0;
		for (Layer l : myNet.layers) {
			
			ArrayList<int[]> list = new ArrayList<int[]>();
			
			System.out.println("here");
			// Implémente les sous-panel qui représentent chaque layer
			JPanel layerPanel = new JPanel();
			layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));
			
			
			Border compound;
			String panelNumber = Integer.toString(pnlAffichage.getComponentCount());
			// Chaque sous-paneau a un titre du style "LayerX"
			compound = BorderFactory.createTitledBorder("Layer" + panelNumber);
			layerPanel.setBorder(compound);
			
			int layer_regular_neuron_count = l.layerSize;
			if (l.hasBiasNeuron)  {layer_regular_neuron_count -= 1;}
			
			DrawPanel neuronPanel = new DrawPanel(false);
			
			// crée les panels pour chaque neurone
			for (int i=0; i<layer_regular_neuron_count; i++) {
				System.out.println("reg_neuron");
//				System.out.println("yes");
				neuronPanel = new DrawPanel(false);
//				neuronPanel.setBackground(new Color(50, 210, 250, 200));

				
				// GETTING THE PANEL COORDS
				int x = neuronPanel.getX();
				int y = neuronPanel.getY();
				
				layerPanel.add(neuronPanel);
				
				int[] coords = new int[2];
				coords[0] = x;
				coords[1] = y;
				int[] stored_coords = coords;
				list.add(stored_coords.clone());
								

			}
			
			// rajoute neurone biais si necessaire
			if (l.hasBiasNeuron) {
				System.out.println("bias_neuron");
				neuronPanel = new DrawPanel(true);
//				neuronPanel.setBackground(new Color(50, 210, 250, 200));
				
				layerPanel.add(neuronPanel);
//				neuronPanels.get(index).add(neuronPanel);
				
				// GETTING THE PANEL COORDS
				int x = neuronPanel.getX();
				int y = neuronPanel.getY();
				
				layerPanel.add(neuronPanel);
				
				int[] coords = new int[2];
				coords[0] = x;
				coords[1] = y;
				int[] stored_coords = coords;
				list.add(stored_coords.clone());
				
//				Point convertedPoint = SwingUtilities.convertPoint(pnlAffichage, 0, 0, neuronPanel);
//				System.out.println(convertedPoint);
			}
			
			// On garde les coordonnées des neurones pour peindre les flèches
//			neuronCoords.add(list);

			layerPanel.setBounds( ( (pnlAffichage.getWidth()/myNet.layers.size()) * index) , 0, panel_width, panel_height/*(100 * l.layerSize)*/ );
//			layerPanel.setBounds(0, 0, 200, 200);
//			layerPanel.setBackground(new Color(50, 210, 250, 200));
			
//			pnlAffichage.add(layerPanel);
			pnlAffichage.add(layerPanel, JLayeredPane.DEFAULT_LAYER);
//			pnlAffichage.add(layerPanel, new Integer(JLayeredPane.DEFAULT_LAYER));
			
//			networkPanel.add(layerPanel);
//			networkPanel.updateUI();
//			pnlAffichage.setLayer(networkPanel, 10);
//			pnlAffichage.add(networkPanel, new Integer(10));
//			pnlAffichage.repaint();
			layerPanelList.add(layerPanel);
			layerPanel.updateUI();
			pnlAffichage.updateUI();
			
			
//			Point convertedPoint = SwingUtilities.convertPoint(neuronPanel, new Point(0,0), pnlAffichage);
//			System.out.println(convertedPoint);
			
			index += 1;
		}

	    
////	    pnlAffichage.setLayer(testPanel, 1);
//	    pnlAffichage.add(testPanel, new Integer(JLayeredPane.DEFAULT_LAYER + 10));
		
	}
	
	
	
	
	
	
	
	
	/**
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

	/**
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
		
		//===fenêtre principal===
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
				clearArrows();
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

		
//		// Permet de re-sizer les LayerPanel quand on change la taille de la fenetre //
//		pnlAffichage.addComponentListener(new ComponentAdapter() {
//	        @Override
//	        public void componentResized(ComponentEvent e) {
//	          	System.out.println("Resized to " + e.getComponent().getSize());
//	        	if (layerPanelList.size() != 0) {
//		          	for (JPanel panel : layerPanelList) {
//		        		panel.repaint();
//		        	}
//	        	}
//	        }
//		});
		
				
		
		mainFrame.getContentPane().add(pnlAffichage, "cell 0 0 1 7,grow");
		
		
////////////////////// TEST DES GLASS-PANE ............. ///////////////////////////////////////////////////////////////////////7
		
//		JPanel glassPane = (JPanel) mainFrame.getGlassPane();
//		glassPane.setPreferredSize(new Dimension(300, 500));
////		glassPane.size
//		System.out.println(glassPane.getSize());
//		
//
//		glassPane.setVisible(true);
//		glassPane.setLayout(new GridLayout(0, 1));
////	    JButton glassButton = new JButton("Hide");
////	    glassPane.add(glassButton);
	    
//	    ExtPanel testPanel = new ExtPanel();
//	    testPanel.setLayout(new GridLayout(0, 1));
//	    testPanel.setPreferredSize(new Dimension(300,300));
//	    testPanel.setBackground(Color.PINK);
////	    testPanel.setMinimumSize(new Dimension(300, 300));
//	    
//	    testPanel.setVisible(true);
//
////	    glassPane.add(testPanel);
//	    mainFrame.setGlassPane(testPanel);
//	    mainFrame.repaint();
////	    mainFrame.pack();
////	    mainFrame.glass
	    
//	    GridBagConstraints c = new GridBagConstraints();
//	    c.fill = GridBagConstraints.HORIZONTAL;
////	    c.gridx = 1;
////	    c.gridy = 1;
//	    c.gridwidth = 1;
//	    c.gridheight = 1;
//	    c.fill = GridBagConstraints.BOTH;

		


		
		
//		mainFrame.getContentPane().add(pnlAffichage);
		
//		pnlAffichage.setLayout(new MigLayout("", "[grow,center]", "[grow,center]"));
		
		
		
		
//// BOUTON D' AJOUT DE COUCHE /////////////////////////////////////////////////////////////////////////////////
		btnAddLayer = new Button("addLayer");
		btnAddLayer.addActionListener(new ActionListener() {
			/*
			 * Le RNA (Net.java) doit avoir été créer avant d'utiliser le bouton
			 * voir au fond après la création de l'interface graphique
			 */
			public void actionPerformed(ActionEvent e) {

				btnPopLayer.setEnabled(true);
				btnImportData.setEnabled(true);
				//btnPrint.setEnabled(true);

				
				
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
					
					// Dessine les layerPanel
					drawLayerPanels();
					clearArrows();
					drawArrows();	
				} 
				else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
											
				btnPopLayer.setEnabled(true);
				btnImportData.setEnabled(true);
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
		btnPopLayer.setEnabled(false);
		mainFrame.getContentPane().add(btnPopLayer, "cell 2 1,grow");
		
		
		
		
		
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
						
						txtConsoleOutput.append("\n Importations efféctuées");
						btnTrain.setEnabled(true);
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
					//
					
					// On rajoute le graphe retourné par cette méthode au Panel d'affichage
//					pnlAffichage.removeAll();
//					pnlAffichage.add(ErrorChartPanel);
					
					
					pnlAffichage.validate();
					
					btnPredict.setEnabled(true);
					btnPrint.setEnabled(true);
					
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
		btnNext = new Button("NEXT");
//		btnStepTrain.setEnabled(false);
		
		btnStepTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				btnNext.setEnabled(true);
				myNet.addLayer("input", "sigmoid", 2, true);
				myNet.addLayer("hidden", "sigmoid", 4, true);
				myNet.addLayer("output", "sigmoid", 1, false);
				
				drawLayerPanels();
				drawArrows();
				
				ArrayList<double[][]> donneesInput = myNet.importCSV("C:\\Users\\haas_\\Downloads\\P.O.O\\XOR_data.csv", true, 1);
				x_train = donneesInput.get(0);
				y_train = donneesInput.get(1);
				
//				myNet.train(x_train, y_train, 5000, 0.5);
				
				//TEMP
				step = 1000;
				nber_of_steps = 5000 / step;
				final_step_size =   5000%step;
				
				// Premier step automatique
				myNet.train(x_train, y_train, step, 0.5);
	
			}
		});
		
		mainFrame.getContentPane().add(btnStepTrain, "cell 2 4,grow");
		mainFrame.getContentPane().add(btnNext, "cell 2 5,grow");
		
		
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (nber_of_steps>0) {
					myNet.stepTrain(x_train, y_train, step, 0.5);
					nber_of_steps -= 1;
										
					printNeuronValues();
					
//					pnlAffichage.getComponent(0).add(new line());
					
					
					
				}

	
			}
		});
		
		
		
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
				txtConsoleOutput.append(myNet.print());
				//myNet.print();
				//myNet.errorGraph();
			}
		});
		mainFrame.getContentPane().add(btnPrint, "cell 2 6,grow");
		
		txtConsoleOutput = new TextArea();
		txtConsoleOutput.setFont(new Font("OCR A Extended", Font.BOLD, 12));
		txtConsoleOutput.setEditable(false);
		txtConsoleOutput.setForeground(Color.GREEN);
		txtConsoleOutput.setBackground(Color.BLACK);
		txtConsoleOutput.setText("Sortie Console :");
		mainFrame.getContentPane().add(txtConsoleOutput, "cell 0 7 3 1,grow");
		
		
		
		
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//===création du résaux de neurone===
		myNet = new Net();
		txtConsoleOutput.append("\n Résaux de neurones créé");
		
	}
	
	public static void ConsoleOutputAppend (String newText) {
		txtConsoleOutput.append(newText);
	}
	
	
	

	
	
	
	
	
	
}
