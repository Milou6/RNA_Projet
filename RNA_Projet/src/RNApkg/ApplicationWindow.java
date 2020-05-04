package RNApkg;
//testy.....
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
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.math3.linear.RealMatrix;
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
	JPanel networkPanel;
	ExtPanel arrowPanel;
	
	private Button btnPredict;				// Bouton permettant de tester le RNA sur une nouvelle s�rie de don�es
	private Button btnPrint;				// Bouton qui ne fait rien pour l'instant
	
	private double [][] x_train;				// permet de stocker les donn�es d'entrainement 
	private double [][] y_train;				// permet de stocker les r�sultats des donn�es d'entrainements
	private double [][] train;
	private static JLayeredPane pnlAffichageDRAW;
	private  JLayeredPane pnlAffichage;
	
	private JTabbedPane tabAffichage;
	private JPanel pnlGraph;
	private JLabel lblTitre;
	
	ArrayList<JPanel> layerPanelList;
	ArrayList<ArrayList<Point>> neuronCoords;
	ArrayList<ArrayList<int[]>> neuronPanelDimensions;

//	int StepTrainEpochs;
	double StepTrainLearningRate;
	int StepTrainEpochsPerStep;
	int StepTrainNumberOfSteps;
	int StepTrainLastStepSize;
	
	private ChartPanel ErrorChartPanel;
	
	
	
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
	 */
	public ApplicationWindow() {
		initialize();
	}

	
	
	/**
	 * Cr�ation et initialisation des diff�rent composants de la fen�tre
	 */
	private void initialize() {	
		
		//===Innitialisation des variables
//		drawPanelList = new ArrayList<DrawPanel>();
		layerPanelList = new ArrayList<JPanel>();
		neuronCoords = new ArrayList<ArrayList<Point>>();
		neuronPanelDimensions = new ArrayList<ArrayList<int[]>>();
		
//		StepTrainEpochs = 0;
		StepTrainLearningRate = 0.0;
		StepTrainEpochsPerStep = 0;
		StepTrainNumberOfSteps = 0;
		StepTrainLastStepSize = 0;
		
		//===fen�tre principal===
		mainFrame = new JFrame();
		mainFrame.setTitle("Projet RNA");
		mainFrame.setBounds(100, 100, 800, 610);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLayout(new BoxLayout(mainFrame, BoxLayout.X_AXIS));
		
		mainFrame.getContentPane().setLayout(new MigLayout("", "[grow][10px:n][fill]", "[50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][10px:n,grow][grow]"));
		
		// Permet de re-dessiner les panels de neurones quand on change la taille de la fenetre
		mainFrame.addComponentListener( new ComponentAdapter() {
			@Override
            public void componentResized(ComponentEvent e) {
                System.out.println("Window Resized: Frame");
//		        mainFrame.removeAll();
//				mainFrame.remove(mainFrame.getComponent(0).get);
//				arrowPanel.removeAll();
//				clearArrows();
                if (myNet.lcCouches.getSize() > 0)
                {
		            drawLayerPanels();
		            drawArrows();
		            pnlAffichageDRAW.repaint();
		            printNeuronValues();
                }
            }
		});
		
		//===Panneau d'affichage===
		pnlAffichage = new JLayeredPane();
		pnlAffichage.setBounds(0, 0, 700, 610);
		pnlAffichage.setLayout(new BoxLayout(pnlAffichage, BoxLayout.X_AXIS));
		mainFrame.getContentPane().add(pnlAffichage, "cell 0 0 1 7,grow");
		pnlAffichage.setLayout(new MigLayout("", "[595px,grow]", "[337px,grow]"));

		// On cr�er des onglets "tab" pour afficher les diff�rente partie de l'affichage sans les perdres
		tabAffichage = new JTabbedPane(JTabbedPane.TOP);
//		tabAffichage.setLayout(new BoxLayout(pnlAffichage, BoxLayout.X_AXIS));
//		pnlAffichage.add(tabAffichage, "cell 0 0,grow");
		
		// Onglet qui acceuille l'affichage du graphe d'erreur
		pnlGraph = new JPanel();
		//tabAffichage.addTab("Graphe Erreur", null, pnlGraph, null);
		pnlGraph.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		// On affiche tout d'abord un titre
		lblTitre = new JLabel(">>-- Projet RNA --<<");
		lblTitre.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitre.setFont(new Font("OCR A Extended", Font.BOLD, 42));
		pnlAffichage.add(lblTitre, "cell 0 0,grow");	

		//on cr�er un pannel pour afficher les fl�ches
		pnlAffichageDRAW = new JLayeredPane();
		pnlAffichageDRAW.setBounds(0, 0, 700, 610);
		pnlAffichageDRAW.setLayout(null);	
		
		//A quoi je sers ?
		networkPanel = new JPanel();
		networkPanel.setLayout(new BoxLayout(networkPanel, BoxLayout.X_AXIS));
		networkPanel.setPreferredSize(new Dimension(pnlAffichageDRAW.getX(), pnlAffichageDRAW.getY()));
		networkPanel.setBackground(Color.PINK);
		Border compound = BorderFactory.createTitledBorder("networkPanel");
		networkPanel.setBorder(compound);
		networkPanel.setVisible(true);
		
		
		
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
				btnPrint.setEnabled(true);	
				
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
					txtConsoleOutput.append("\n Layer : " + lstTypeLayer.getSelectedValue() + " / Activation : " + lstFonctActiv.getSelectedValue() + " / Nombre de neurone : " + txtNbrNeurone.getText() + " / Biais :" +  radNBiaisYes.isSelected() );
					
			///////// Dessine les layerPanel ////////////////
					drawLayerPanels();
					drawArrows();
				} 
				else {
				   txtConsoleOutput.append("\n Action interrompue par l'utilisateur");
				}
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
						txtConsoleOutput.append("\n couche �supprimer : " + lstLayers.getSelectedIndex());
						myNet.lcCouches.removeLayer(lstLayers.getSelectedIndex());
						
						///////// re Dessine les layerPanel ////////////////
						drawLayerPanels();
						drawArrows();
						
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
						
						txtConsoleOutput.append("\n");
						txtConsoleOutput.append("\n Importations efféctuées ( " + file.getName() + " )");
						
						btnTrain.setEnabled(true);
						btnStepTrain.setEnabled(true);
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
					
					// On appelle la methode errorGraph()
					ErrorChartPanel = myNet.errorGraph();
					
					// On rajoute le graphe retourn� par cette m�thode au Panel du graphique dans un onglet
					tabAffichage.addTab("Graph Erreur", null, pnlGraph, null);
					
					pnlGraph.removeAll();
					pnlGraph.add(ErrorChartPanel);
					pnlGraph.validate();
					tabAffichage.setSelectedIndex(1);
					
					btnPredict.setEnabled(true);
					btnPrint.setEnabled(true);
					btnStepTrain.setEnabled(false);
					btnAddLayer.setEnabled(false);
					btnPopLayer.setEnabled(false);
					
				} else {
				   txtConsoleOutput.append("\n Action interrompue par l'utilisateur");
				}
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
//					StepTrainEpochsPerStep = Integer.parseInt(NbeEpochsParStep.getText());
//					StepTrainLearningRate = Double.parseDouble(txtLearningRate.getText());
//					
//					StepTrainNumberOfSteps = Integer.parseInt(txtNbrEpochs.getText()) / Integer.parseInt(NbeEpochsParStep.getText()) - 1;
//					System.out.println(StepTrainNumberOfSteps);
//					StepTrainLastStepSize = Integer.parseInt(txtNbrEpochs.getText()) % Integer.parseInt(NbeEpochsParStep.getText());
//
//				
//				
//					// Le système fait déjà le premier Step d'entrainement
//					myNet.train(x_train, y_train, Integer.parseInt(NbeEpochsParStep.getText()), Double.parseDouble(txtLearningRate.getText()) );
//					
//					// On affiche les valeurs des neurones suite à ce premier Step
//					printNeuronValues();
				
//					// POUR DEBUGGER PLUS RAPIDEMENT ///////////////////////////////////
//					// Changer le "false" dans la ligne sous ce bloc -->  btnStepTrain.setEnabled(false);

					btnNext.setEnabled(true);
					btnPrint.setEnabled(true);
					myNet.addLayer("input", "sigmoid", 2, true);
					myNet.addLayer("hidden", "sigmoid", 4, true);
					myNet.addLayer("output", "sigmoid", 1, false);
					
					StepTrainLearningRate = 0.5;
					StepTrainEpochsPerStep = 900;
					StepTrainNumberOfSteps = 5;
					StepTrainLastStepSize = 500;
					
					ArrayList<double[][]> donneesInput = myNet.importCSV("../RNA_Projet/src/donneeEntrainement/XOR_data.csv", true, 1);
					x_train = donneesInput.get(0);
					y_train = donneesInput.get(1);
					
					// Premier step automatique
					myNet.train(x_train, y_train, StepTrainEpochsPerStep, StepTrainLearningRate);
					
					clearArrows();
					drawLayerPanels();
					drawArrows();
					
					// On appelle la methode errorGraph()
					ErrorChartPanel = myNet.errorGraph();
					
					// On rajoute le graphe retourn� par cette m�thode au Panel du graphique dans un onglet
					tabAffichage.addTab("Graph Erreur", null, pnlGraph, null);
					
					pnlGraph.removeAll();
					pnlGraph.add(ErrorChartPanel);
					pnlGraph.validate();
					tabAffichage.setSelectedIndex(1);
					
					
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
		btnStepTrain.setEnabled(true);
		
		
		
//// BOUTON Poursuivre entrainement "NEXT" /////////////////////////////////////////////////////////////////////////////////////////////////////////			
		btnNext = new Button("NEXT");
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
										
				ErrorChartPanel = myNet.errorGraph();
				
				pnlGraph.removeAll();
				pnlGraph.add(ErrorChartPanel);
				pnlGraph.validate();
			}
		});		
		mainFrame.getContentPane().add(btnNext, "cell 2 5,grow");
		btnNext.setEnabled(false);
		
		
		
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
		mainFrame.getContentPane().add(btnPredict, "cell 2 6,grow");
		
		
		
//// AFFICHAGE /////////////////////////////////////////////////////////////////////////////////
		btnPrint = new Button("print");
		btnPrint.setEnabled(false);
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txtConsoleOutput.append("\n");
				txtConsoleOutput.append(myNet.print());
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
		
		
		
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		//===cr�ation du r�saux de neurone===
		myNet = new Net();
//		txtConsoleOutput.append("\n R�saux de neurones cr��");
	}
	
	
	
	/**
	 * Pour Afficher du texte dans la console du programme depuis ailleurs
	 */
	public static void ConsoleOutputAppend (String newText) {
		txtConsoleOutput.append("\n " + newText);
	}
	
	
	
	/**
	 * Printe les couches du RNA et les neurones dans chaque couche. Chaque neurone est un sous-panel dans un layerPanel.
	 */
	public void drawLayerPanels() {
		System.out.println("drawLayerPanels() INVOKED");	
		
		// on retire le titre avant d'ajouter le premi�re onglet
		try {
			pnlAffichage.remove(lblTitre);
//			pnlAffichage.add(tabAffichage, "cell 0 0,grow");
			
//			pnlAffichage.add(tabAffichage);
			//tabAffichage.addTab("R�seaux de neurone", null, pnlRNA, null);
//			tabAffichage.addTab("R�seaux de neurone", null, pnlAffichageDRAW, null);
		} catch (Exception e) {
			System.out.println(e);
		}	
				
//		pnlRNA.removeAll();
		pnlAffichageDRAW.removeAll();
		layerPanelList.clear();
		int index = 0;
		
		if (myNet.lcCouches.getSize() != 0) {
			// CALCUL DES DIMENSIONS POUR LES PANELS/////////////////
			int panel_width = pnlAffichageDRAW.getWidth() / myNet.lcCouches.getSize();
			int panel_height = 0;
			panel_height = (pnlAffichageDRAW.getHeight());
	//		System.out.println("panel_width : " + panel_width);
	//		System.out.println("panel_height : " + panel_height);
			//  /CALCUL DES DIMENSIONS POUR LES PANELS///////////////
	
			// Boucle sur les Layers
			for (int i = 0; i < myNet.lcCouches.getSize();i++) {
				ArrayList<int[]> list = new ArrayList<int[]>();
				
				// Implémente les sous-panel qui représentent chaque layer
				JPanel layerPanel = new JPanel();
				layerPanel.setLayout(new BoxLayout(layerPanel, BoxLayout.Y_AXIS));
				
				// On rajoute une bordure à ces sous-panels
				Border compound;
				String panelNumber = Integer.toString(pnlAffichageDRAW.getComponentCount());
				compound = BorderFactory.createTitledBorder("Layer" + panelNumber);
				layerPanel.setBorder(compound);
				
				// On dessinera les BiasNeuron d'une autre couleur
				int layer_regular_neuron_count = myNet.lcCouches.getElementAt(i).layerSize;
				if (myNet.lcCouches.getElementAt(i).hasBiasNeuron)  {layer_regular_neuron_count -= 1;}
				DrawPanel neuronPanel = new DrawPanel(false);
				
				// Crée les panels pour chaque RegularNeuron
				for (int j=0; j<layer_regular_neuron_count; j++) {
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
				if (myNet.lcCouches.getElementAt(i).hasBiasNeuron) {
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
				layerPanel.setBounds( ( (pnlAffichageDRAW.getWidth()/myNet.lcCouches.getSize()) * index) , 0, panel_width, panel_height/*(100 * l.layerSize)*/ );
				
				pnlAffichageDRAW.add(layerPanel, JLayeredPane.DEFAULT_LAYER);
//				pnlAffichageDRAW.updateUI();
//				pnlAffichageDRAW.repaint();
//				tabAffichage.updateUI();
//				tabAffichage.repaint();
				pnlAffichage.add(tabAffichage, "cell 0 0,grow", JLayeredPane.DEFAULT_LAYER);
//				pnlAffichage.add
				tabAffichage.addTab("R�seaux de neurone", null, pnlAffichageDRAW, null);
				layerPanelList.add(layerPanel);
				
				index += 1;
				
//				pnlRNA.add(layerPanel,"grow");
//				pnlRNA.updateUI();
			}
		}
	}
	
	
	
	/**
	 *  Print la valeur dans chaque neurone de l'affichage
	 */
	public void printNeuronValues() {
	
		// Ce "if" évite de faire des divisions par zéro....
		if (myNet.netDataBase.activations.size() != 0) {
		
			for (int i=0; i<layerPanelList.size(); i++) {
				JPanel current_layer_panel = layerPanelList.get(i);
	//			Component current_layer_panel = pnlAffichageDRAW.getComponent(i);
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
	//				pnlAffichageDRAW.updateUI();
				}
			}
/*			
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
*/
		}
	}



	/* Efface toutes les flèches dessinées auparavant.
	 * Utilisé quand l'application est re-dimensionnée.
	 */
	public void clearArrows() {
		neuronCoords.clear();
		neuronPanelDimensions.clear();
		System.out.println("clearArrows() INVOKED");
		
//		pnlAffichageDRAW.remove(arrowPanel);
//		pnlAffichageDRAW.remove

//		ArrayList<ArrayList<Point>> emptyList = new ArrayList<ArrayList<Point>>();
//		boolean[] temp_boolean = {true,true,true};
//	
//		// On re-crée un nouveau ExtPanel, en lui donnant des listes vides pour qu'il ne dessine aucune flèche
//	   	ExtPanel returnPanel = new ExtPanel(emptyList, neuronPanelDimensions, pnlAffichageDRAW, temp_boolean);
//  	
//  		// On remplace l'ExtPanel du pnlAffichageDRAW par ce nouveau ExtPanel vide.
//    	arrowPanel = returnPanel;
//	    arrowPanel.setLayout(new GridLayout(0, 1));
//	    arrowPanel.setPreferredSize(pnlAffichageDRAW.getPreferredSize());
//	    arrowPanel.setBounds(0, 0, pnlAffichageDRAW.getWidth(), pnlAffichageDRAW.getHeight());
//	    arrowPanel.setVisible(true);
//	    pnlAffichageDRAW.add(arrowPanel, JLayeredPane.POPUP_LAYER);
	    
	    pnlAffichageDRAW.updateUI();
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
				// Mais on veut ces coordonnées par rapport au système de coords. de pnlAffichageDRAW, 
				// donc on utilise la méthode .convertPoint()
				Point convertedPoint = SwingUtilities.convertPoint(current_neuron_panel, new Point(0,0), pnlAffichageDRAW);
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
		boolean[] layerHasBiasNeuron = new boolean[myNet.lcCouches.getSize()];
		for (int i = 0; i < myNet.lcCouches.getSize();i++) {
			if (myNet.lcCouches.getElementAt(i).hasBiasNeuron == true) {
				layerHasBiasNeuron[myNet.lcCouches.indexOf(myNet.lcCouches.getElementAt(i))] = true;
			}
			else {layerHasBiasNeuron[myNet.lcCouches.indexOf(myNet.lcCouches.getElementAt(i))] = false;}
		}
		System.out.println(Arrays.toString(layerHasBiasNeuron));
		
		try {
			pnlAffichageDRAW.remove(arrowPanel);
			System.out.println("ArrowPanel removed");
		}
		catch(Exception e) {
		  System.out.println("no ArrowPanel to remove");
		}
		System.out.println("got after");
		
		ArrayList<RealMatrix> NeuronWeights = myNet.netDataBase.weights;
		
		// On appelle le constructeur de ExtPanel, en lui donnant les infos sur les coordonnées/dimensions des panels
    	ExtPanel returnPanel = new ExtPanel(neuronCoords, neuronPanelDimensions, pnlAffichageDRAW, NeuronWeights, layerHasBiasNeuron);
		
   	
    	arrowPanel = returnPanel;
	    arrowPanel.setLayout(new GridLayout(0, 1));
	    arrowPanel.setPreferredSize(pnlAffichageDRAW.getPreferredSize());
	    arrowPanel.setBounds(0, 0, pnlAffichageDRAW.getWidth(), pnlAffichageDRAW.getHeight());
	    arrowPanel.setVisible(true);
	    pnlAffichageDRAW.add(arrowPanel, JLayeredPane.POPUP_LAYER);
	    
	    
	    
	    ////////////////////// DEBUG ////////////////////////
//	    System.out.println(pnlAffichageDRAW.getComponentCount());
	    
	    
	       // We need to remove old mouselisteners
//		   try {
//		   java.awt.event.MouseMotionListener[] mouseListenersList = pnlAffichageDRAW.getMouseMotionListeners();
//			   for (MouseMotionListener ml : mouseListenersList) {
//			   		pnlAffichageDRAW.removeMouseMotionListener(ml);	
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
////				    	pnlAffichageDRAW.updateUI();
//				    	pnlAffichageDRAW.repaint();
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
////				    	pnlAffichageDRAW.updateUI();
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
//		pnlAffichageDRAW.addMouseMotionListener(ml);
	    
	    
	    
	    
	    pnlAffichageDRAW.updateUI();
	    

    }
    
}