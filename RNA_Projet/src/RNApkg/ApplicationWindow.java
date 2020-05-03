package RNApkg;
//testy.....

import java.awt.EventQueue;
import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Font;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.DefaultListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;


public class ApplicationWindow {

//	private static final Graphics Graphics = null;
	private JFrame mainFrame;				// Fenetre principal
	private static TextArea txtConsoleOutput;		// Zone de Texte permettant d'afficher les sorties de la console
	private Net myNet;						// Objet Net repr�sentant le r�sau de neurone artificiel cr�er
	private Button btnAddLayer;				// Bouton d'ajouet de couche au r�sau
	private Button btnPopLayer;				// Bouton pour retirer une couche du resau
	private Button btnImportData;			// Bouton permettant d'importer les donn�es d'entrainement
	private Button btnTrain;				// Bouton permettant de commencer l'entrainment du RNA
	private Button btnStepTrain;
	private Button btnNext;
	
	int step;
	int nber_of_steps;
	int final_step_size;
	
	private Button btnPredict;				// Bouton permettant de tester le RNA sur une nouvelle s�rie de don�es
	private Button btnPrint;				// Bouton qui ne fait rien pour l'instant
	
	private double [][] x_train;				// permet de stocker les donn�es d'entrainement 
	private double [][] y_train;				// permet de stocker les r�sultats des donn�es d'entrainements
//	private double [][] train;
//	private static JPanel pnlAffichage;
	private  JPanel pnlAffichage;
	
	private JTabbedPane tabAffichage;
	private JPanel pnlRNA;
	private JPanel pnlGraph;
	private JLabel lblTitre;
	//private LIMCouche coucheRNA;
	private ChartPanel ErrorChartPanel;

	ArrayList<DrawPanel> drawPanelList;
	ArrayList<JPanel> layerPanelList;
	
	
    @SuppressWarnings("serial")
    public class line extends JComponent {
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);

            Shape s = new Line2D.Float(0, 0, 150, 150);
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(20));
            g2.draw(s);
//           txtConsoleOutput.append("\n doing something...");
        }
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
	 * Permet de cr�er la fen�tre
	 * 
	 */
	public ApplicationWindow() {
		initialize();
	}

	/**
	 * Cr�ation et initialisation des diff�rent composants de la fen�tre
	 * 
	 */
	private void initialize() {	
		
		drawPanelList = new ArrayList<DrawPanel>();
		layerPanelList = new ArrayList<JPanel>();
		
		//===fen�tre principal===
		mainFrame = new JFrame();
		mainFrame.setTitle("Projet RNA");
		mainFrame.setBounds(100, 100, 800, 610);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BoxLayout(mainFrame, BoxLayout.X_AXIS));
		
		mainFrame.getContentPane().setLayout(new MigLayout("", "[grow][10px:n][fill]", "[50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][10px:n,grow][grow]"));
		
		//===Liste des couches===
		//coucheRNA = new LIMCouche();
		
		//===Panneau d'affichage===
		pnlAffichage = new JPanel();
		pnlAffichage.setBounds(0, 0, 700, 610);
		pnlAffichage.setLayout(new BoxLayout(pnlAffichage, BoxLayout.X_AXIS));
		mainFrame.getContentPane().add(pnlAffichage, "cell 0 0 1 7,grow");
		
//		// Permet de re-sizer les LayerPanel quand on change la taille de la fenetre //
//		pnlAffichage.addComponentListener(new ComponentAdapter() {
//	        @Override
//	        public void componentResized(ComponentEvent e) {
//	          	txtConsoleOutput.append("\n Resized to " + e.getComponent().getSize());
//	        	if (layerPanelList.size() != 0) {
//		          	for (JPanel panel : layerPanelList) {
//		        		panel.repaint();
//		        	}
//	        	}
//	        }
//		});
		
		//ErrorChartPanel = myNet.errorGraph();
				
		
		
	
////////////////////// TEST DES GLASS-PANE ............. ///////////////////////////////////////////////////////////////////////7
		
//		JPanel glassPane = (JPanel) mainFrame.getGlassPane();
//		glassPane.setPreferredSize(new Dimension(700, 500));
////		glassPane.size
//		System.out.println(glassPane.getSize());
//		
//
//		glassPane.setVisible(true);
//		glassPane.setLayout(new FlowLayout());
//	    JButton glassButton = new JButton("Hide");
////	    glassPane.add(glassButton);
//	    
//	    ExtPanel testPanel = new ExtPanel();
//	    testPanel.setPreferredSize(new Dimension(480,290));
////	    testPanel.setBackground(Color.BLUE);
//	    
////	    testPanel.setVisible(true);
//	    glassPane.add(testPanel);
//	    
////	    GridBagConstraints c = new GridBagConstraints();
////	    c.fill = GridBagConstraints.HORIZONTAL;
//////	    c.gridx = 1;
//////	    c.gridy = 1;
////	    c.gridwidth = 1;
////	    c.gridheight = 1;
////	    c.fill = GridBagConstraints.BOTH;

		


		
		
//		mainFrame.getContentPane().add(pnlAffichage);
		
//		pnlAffichage.setLayout(new MigLayout("", "[grow,center]", "[grow,center]"));
		
		pnlAffichage.setLayout(new MigLayout("", "[595px,grow]", "[337px,grow]"));

		// On cr�er des onglets "tab" pour afficher les diff�rente partie de l'affichage sans les perdres
		tabAffichage = new JTabbedPane(JTabbedPane.TOP);
		//pnlAffichage.add(tabAffichage, "cell 0 0,grow");
		
		// Onglet qui acceuille l'affichage du RNA
		pnlRNA = new JPanel();
		//tabAffichage.addTab("R�seaux de neurone", null, pnlRNA, null);
		pnlRNA.setLayout(new MigLayout("", "[97px]", "[25px,grow]"));
		
		// Onglet qui acceuille l'affichage du graphe d'erreur
		pnlGraph = new JPanel();
		//tabAffichage.addTab("Graphe Erreur", null, pnlGraph, null);
		pnlGraph.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		// On affiche tout d'abord un titre
		lblTitre = new JLabel(">>-- Projet RNA --<<");
		lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitre.setFont(new Font("OCR A Extended", Font.BOLD, 42));
		pnlAffichage.add(lblTitre, "cell 0 0,grow");	

		
//// BOUTON D' AJOUT DE COUCHE /////////////////////////////////////////////////////////////////////////////////
		btnAddLayer = new Button("addLayer");
		btnAddLayer.addActionListener(new ActionListener() {
			/*
			 * Le RNA (Net.java) doit avoir �t� cr�er avant d'utiliser le bouton
			 * voir au fond apr�s la cr�ation de l'interface graphique
			 */
			public void actionPerformed(ActionEvent e) {

				btnPopLayer.setEnabled(true);
				btnImportData.setEnabled(true);
				//btnPrint.setEnabled(true);				
				
				//======Cr�ation du pop-up d'ajout de Layer======
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
				
				//===S�l�ction du nombre de neurone de la couche===
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
				        new JLabel("Fonction d'activation"),
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
					//myNet.lcCouches.addLayer(new_layer);
					txtConsoleOutput.append("\n Layer : " + lstTypeLayer.getSelectedValue() + " / Activation : " + lstFonctActiv.getSelectedValue() + " / Nombre de neurone : " + txtNbrNeurone.getText() + " / Biais :" +  radNBiaisYes.isSelected() );
					
					// Dessine les layerPanel
					drawLayerPanels();	
				} 
				else {

					
					// Impl�mente les sous-panel qui repr�sentent chaque layer
//					DrawPanel layerPanel = new DrawPanel(nbrNeurone);
					
//					JLabel layerPanelText = new JLabel("layerPane");
//					layerPanel.add(layerPanelText);
					
//					Border compound;
//					String panelNumber = Integer.toString(pnlRNA.getComponentCount());
					// Chaque sous-paneau a un titre du style "LayerX"
//					compound = BorderFactory.createTitledBorder("Layer" + panelNumber);
//					layerPanel.setBorder(compound);
					
//					layerPanel.setBounds(0, 0, pnlAffichage.getWidth() , pnlAffichage.getHeight());
//					layerPanel.setMaximumSize(new Dimension(150, pnlRNA.getHeight()));
					
//					pnlRNA.add(layerPanel);
//					pnlRNA.updateUI();
//					System.out.println(Arrays.deepToString(pnlAffichage.getComponents()));
				
//				} else {
				   txtConsoleOutput.append("\n Action interrompue par l'utilisateur");
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
				if(myNet.lcCouches.getSize() > 0)
				{
					//===Liste des Layers===
					JList<Layer> lstLayers = new JList<Layer>(myNet.lcCouches);
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
						//System.out.println(result);
						txtConsoleOutput.append("\n couche �supprimer : " + lstLayers.getSelectedValue().toString());
						myNet.lcCouches.removeLayer(lstLayers.getSelectedIndex());
						drawLayerPanels();
					} else {
					   txtConsoleOutput.append("\n Action interrompue par l'utilisateur");
					}
				}
				else
				{
					txtConsoleOutput.append("\n Aucune couche � supprim� !");
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
						
					// Impl�mentation du Dialog pour l'importation de donn�es ///////////////////////////////////////////
					
					//===radio bouton Neurone de biais===
					JRadioButton radLigne1Oui = new JRadioButton("Oui");				
					JRadioButton radLigne1Non = new JRadioButton("Non");
					ButtonGroup groupeRad = new ButtonGroup();
					groupeRad.add(radLigne1Oui);
					groupeRad.add(radLigne1Non);
					radLigne1Oui.setSelected(true);
					
					//===Liste du nombre d'outputs pour chaque ligne de donn�es===
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
					
					// /Impl�mentation du Dialog pour l'importation de donn�es ///////////////////////////////////////////
				 
					 // On appelle la m�thode importCSV() avec les param�tres choisis par l'utilisateur dans le 
					 // Dialog ci-dessus.
					if (result == JOptionPane.OK_OPTION) {
						ArrayList<double[][]> donneesInput = myNet.importCSV(filePath, radLigne1Oui.isSelected(), listeOutputs.getSelectedValue());
						x_train = donneesInput.get(0);
						y_train = donneesInput.get(1);
						
						txtConsoleOutput.append("\n Importations eff�ctu�es");
						btnTrain.setEnabled(true);
					}
					else {
					   txtConsoleOutput.append("\n Action interrompue par l'utilisateur");
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
				        new JLabel("Nombre d'epochs d'entraÃ®nement : "),
				        txtNbrEpochs,
				        new JLabel("learning_rate : "),
				        txtLearningRate
				};
				int result = JOptionPane.showConfirmDialog(null, Entrainement, "Train network", JOptionPane.PLAIN_MESSAGE);
				
				if (result == JOptionPane.OK_OPTION) {
					myNet.train(x_train, y_train, Integer.parseInt(txtNbrEpochs.getText()), Double.parseDouble(txtLearningRate.getText()));
					
					
/*					// On rajoute le graphe dans un JDialog s�par� de la fenetre principale
					JPanel chartPanel = new JPanel();
					chartPanel.add(ErrorChartPanel);
					
					JDialog chartDialog = new JDialog();
					chartDialog.setLayout(new FlowLayout(BoxLayout.X_AXIS));
	
					chartDialog.add(chartPanel);
					chartDialog.setSize(chartDialog.getPreferredSize());
					chartDialog.setVisible(true);
*/					//
					
					// On rajoute le graphe retourn� par cette m�thode au Panel d'affichage
//					pnlAffichage.removeAll();
//					pnlAffichage.add(ErrorChartPanel);
					
					
//					pnlAffichage.validate();
					
					
					// On appelle la methode errorGraph()
					//ChartPanel ErrorChartPanel = myNet.errorGraph();
					ErrorChartPanel = myNet.errorGraph();
					
					// On rajoute le graphe retourn� par cette m�thode au Panel du graphique dans un onglet
					tabAffichage.addTab("Graph Erreur", null, pnlGraph, null);
					
					pnlGraph.removeAll();
					pnlGraph.add(ErrorChartPanel);
					pnlGraph.validate();
					tabAffichage.setSelectedIndex(1);
					
					btnPredict.setEnabled(true);
					btnPrint.setEnabled(true);
					
				} else {
				   txtConsoleOutput.append("\n Action interrompue par l'utilisateur");
				}
				//txtConsoleOutput.append(txtConsoleOutput.toString());
				//txtConsoleOutput.append("\n\n" + System.in.toString());
				
				//txtConsoleOutput.append("\n\n" + myNet.networkError.toString());
			}
		});
		mainFrame.getContentPane().add(btnTrain, "cell 2 3,grow");
		
		
		
//// BOUTON DE STEP-TRAIN /////////////////////////////////////////////////////////////////////////////////////////////////////////	
		
		btnStepTrain = new Button("Step-train");
		mainFrame.getContentPane().add(btnStepTrain, "cell 2 4,grow");
//		btnStepTrain.setEnabled(false);
		
		btnStepTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				btnNext.setEnabled(true);
				myNet.addLayer("input", "sigmoid", 2, true);
				myNet.addLayer("hidden", "sigmoid", 4, true);
				myNet.addLayer("output", "sigmoid", 1, false);
				
				drawLayerPanels();
				ArrayList<double[][]> donneesInput = myNet.importCSV("../RNA_Projet/src/donneeEntrainement/XOR_data.csv", true, 1);
				x_train = donneesInput.get(0);
				y_train = donneesInput.get(1);
				
//				myNet.train(x_train, y_train, 5000, 0.5);
				
				//TEMP
				step = 1000;
				nber_of_steps = 5000 / step;
				final_step_size =   5000%step;
				
				// Premier step automatique
				myNet.train(x_train, y_train, step, 0.5);
				
				
				printNeuronValues();
				
				
				// On appelle la methode errorGraph()
				//ChartPanel ErrorChartPanel = myNet.errorGraph();
				ErrorChartPanel = myNet.errorGraph();
				
				// On rajoute le graphe retourn� par cette m�thode au Panel du graphique dans un onglet
				tabAffichage.addTab("Graph Erreur", null, pnlGraph, null);
				
				pnlGraph.removeAll();
				pnlGraph.add(ErrorChartPanel);
				pnlGraph.validate();
				tabAffichage.setSelectedIndex(1);
			}
		});
		
//// BOUTON Poursuivre entrainement "NEXT" /////////////////////////////////////////////////////////////////////////////////////////////////////////	
				
		btnNext = new Button("NEXT");
		mainFrame.getContentPane().add(btnNext, "cell 2 5,grow");
		
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (nber_of_steps>0) {
					myNet.stepTrain(x_train, y_train, step, 0.5);
					nber_of_steps -= 1;
										
					printNeuronValues();
					ErrorChartPanel = myNet.errorGraph();
					
					pnlGraph.removeAll();
					pnlGraph.add(ErrorChartPanel);
					pnlGraph.validate();
//					pnlAffichage.getComponent(0).add(new line());	
				}
			}
		});
		
		
		
//// BOUTON DE PREDICTION SUR DE NOUVELLES DONNEES /////////////////////////////////////////////////////////////////////////////////
	// Il prend un fichier .csv , comme le bouton "ImporterDonnes" 
		btnPredict = new Button("predict");
		mainFrame.getContentPane().add(btnPredict, "cell 2 6,grow");
		btnPredict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				//// ON REUTILISE ICI LE CODE POUR LA DIALOG BOX DE "IMPORTER DONNEES" ////////////
				final JFileChooser fc = new JFileChooser();
				int donneesImportees = fc.showOpenDialog(mainFrame);
				
				 if (donneesImportees == JFileChooser.APPROVE_OPTION) {
					 File file = fc.getSelectedFile();
					 String filePath = file.getAbsolutePath();
				
						// Impl�mentation du Dialog pour lA PREDICTION de donn�es ///////////////////////////////////////////
						
						//===radio bouton Neurone de biais===
						JRadioButton radLigne1Oui = new JRadioButton("Oui");				
						JRadioButton radLigne1Non = new JRadioButton("Non");
						ButtonGroup groupeRad = new ButtonGroup();
						groupeRad.add(radLigne1Oui);
						groupeRad.add(radLigne1Non);
						radLigne1Oui.setSelected(true);
						
						//===Liste du nombre d'outputs pour chaque ligne de donn�es===
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
				
					 // On appelle la m�thode importCSV() avec les param�tres choisis par l'utilisateur dans le 
					 // Dialog ci-dessus.
					if (result == JOptionPane.OK_OPTION) {
						double[][] prediction = myNet.testNetwork(filePath, radLigne1Oui.isSelected(), listeOutputs.getSelectedValue());
						
						txtConsoleOutput.append("\n PREDICTIONS : \n");
						txtConsoleOutput.append("\n" + Arrays.deepToString(prediction) + "\n");
						btnTrain.setEnabled(true);
					}
					else {
					   txtConsoleOutput.append("\n Action interrompue par l'utilisateur");
					}
				 }
			}
		});
		btnPredict.setEnabled(false);
		
		
		
		
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
		mainFrame.getContentPane().add(txtConsoleOutput, "flowy,cell 0 7 3 1,grow");
		
		//===cr�ation du r�saux de neurone===
		myNet = new Net();
		txtConsoleOutput.append("\n R�saux de neurones cr��");
		
	}
	
	public static void ConsoleOutputAppend (String newText) {
		txtConsoleOutput.append(newText);
	}
	
	/* Printe les couches du RNA et les neurones dans chaque couche.
	 * Chaque neurone est un sous-panel dans un layerPanel.
	 */
	public void drawLayerPanels() {
		
		// on retire le titre avant d'ajouter le premi�re onglet
		pnlAffichage.remove(lblTitre);
		
		pnlAffichage.add(tabAffichage, "cell 0 0,grow");
		tabAffichage.addTab("R�seaux de neurone", null, pnlRNA, null);
		
		pnlRNA.removeAll();
		layerPanelList.clear();
		
		for (int i = 0; i < myNet.lcCouches.getSize();i++) {
			// Impl�mente les sous-panel qui repr�sentent chaque layer
			JPanel layerPanel = new JPanel();
			layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));
			
			
			Border compound;
			String panelNumber = Integer.toString(pnlRNA.getComponentCount());
			// Chaque sous-paneau a un titre du style "LayerX"
			compound = BorderFactory.createTitledBorder("Layer" + panelNumber);
			layerPanel.setBorder(compound);
			
			int layer_regular_neuron_count = myNet.lcCouches.getElementAt(i).layerSize;
			if (myNet.lcCouches.getElementAt(i).hasBiasNeuron)  {layer_regular_neuron_count -= 1;}
			
			// cr�e les panels pour chaque neurone
			for (int j=0; j<layer_regular_neuron_count; j++) {
//				txtConsoleOutput.append("\n yes");
				DrawPanel neuronPanel = new DrawPanel(false);
				
				layerPanel.add(neuronPanel);
				layerPanel.updateUI();
			}
			
			// rajoute neurone biais si necessaire
			if (myNet.lcCouches.getElementAt(i).hasBiasNeuron) {
				DrawPanel neuronPanel = new DrawPanel(true);
				
				layerPanel.add(neuronPanel);
				layerPanel.updateUI();
			}
			layerPanelList.add(layerPanel);
			pnlRNA.add(layerPanel,"grow");
			pnlRNA.updateUI();
		}
	}
	
	/* Print la valeur dans chaque neurone de l'affichage
	 */
	public void printNeuronValues() {
		
		// Boucle sur les layerPanel
		for (int i=0; i<pnlRNA.getComponentCount(); i++) {
			JPanel current_layer_panel = (JPanel) pnlRNA.getComponent(i);
			
			for (int j=0; j<current_layer_panel.getComponentCount(); j++) {
				System.out.println(myNet.netDataBase.activations.get(i).getEntry(j, 0));
				txtConsoleOutput.append("\n test");
				JPanel current_neuron_panel = (JPanel) current_layer_panel.getComponent(j);
				
				JTextField neuronValue = new JTextField(String.format("%.4f", myNet.netDataBase.activations.get(i).getEntry(j, 0)));
				current_neuron_panel.removeAll();
				current_neuron_panel.add(neuronValue);
				
//				current_neuron_panel.add(new line());
////			current_neuron_panel.repaint();
				pnlAffichage.updateUI();
			}
		}
	}
}
