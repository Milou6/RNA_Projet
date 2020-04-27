package RNApkg;
//testy.....

import java.awt.EventQueue;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Font;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.DefaultListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;


public class ApplicationWindow {

	private JFrame mainFrame;				// Fenetre principal
	private static TextArea txtConsoleOutput;		// Zone de Texte permettant d'afficher les sorties de la console
	private Net myNet;						// Objet Net représentant le résau de neurone artificiel créer
	private Button btnAddLayer;				// Bouton d'ajouet de couche au résau
	private Button btnPopLayer;				// Bouton pour retirer une couche du resau
	private Button btnImportData;			// Bouton permettant d'importer les données d'entrainement
	private Button btnTrain;				// Bouton permettant de commencer l'entrainment du RNA
	private Button btnPredict;				// Bouton permettant de tester le RNA sur une nouvelle série de donées
	private Button btnPrint;				// Bouton qui ne fait rien pour l'instant
	
	private double [][] x_test;				// permet de stocker les données d'entrainement 
	private double [][] y_test;				// permet de stocker les résultats des données d'entrainements
	private double [][] test;
	private JPanel pnlAffichage;
	private JLabel lblStartup;
	
	private LIMCouche coucheRNA;
	

	ArrayList<DrawPanel> drawPanelList;
	
	
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
		
		drawPanelList = new ArrayList<DrawPanel>();
		
		//===fenêtre principal===
		mainFrame = new JFrame();
		mainFrame.setTitle("Projet RNA");
		mainFrame.setBounds(100, 100, 800, 610);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new MigLayout("", "[grow][10px:n][fill]", "[50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][50px:50px][10px:n,grow][grow]"));
		
		//===Liste des couches===
		coucheRNA = new LIMCouche();
		
		//===Panneau d'affichage===
		pnlAffichage = new JPanel();
		pnlAffichage.setLayout(new BoxLayout(pnlAffichage, BoxLayout.X_AXIS));

		
		// Permet de re-sizer les LayerPanel quand on change la taille de la fenetre //
		pnlAffichage.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentResized(ComponentEvent e) {
	          	System.out.println("Resized to " + e.getComponent().getSize());
	        	if (drawPanelList.size() != 0) {
		          	for (DrawPanel panel : drawPanelList) {
		        		panel.repaint();
		        	}
	        	}
	        }
		});
		
		
		
/*		{
			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				Shape line = new Line2D.Double(3, 3, 303, 303);
				
				int rectWidth = pnlAffichage.getWidth();
				Shape rect = new Rectangle(3, 3, rectWidth-10, 303);
				
				Shape circle = new Ellipse2D.Double(100, 100, 100, 100);
				Shape roundRect = new RoundRectangle2D.Double(20, 20, 250, 250, 5, 25);
				g2.draw(line);
				g2.draw(rect);
				g2.draw(circle);
				g2.draw(roundRect);
			}
		}; */
		
		
		mainFrame.getContentPane().add(pnlAffichage, "cell 0 0 1 7,grow");
//		mainFrame.getContentPane().add(pnlAffichage);
		
//		pnlAffichage.setLayout(new MigLayout("", "[grow,center]", "[grow,center]"));
//		
//		
//		lblStartup = new JLabel("Projet RNA");
//		lblStartup.setFocusCycleRoot(true);
//		lblStartup.setFont(new Font("OCR A Extended", Font.BOLD, 54));
		
///	
		
//		pnlAffichage.add(lblStartup, "cell 0 0");
		
		
		
		
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
					


					
					// Implémente les sous-panel qui représentent chaque layer
					DrawPanel layerPanel = new DrawPanel(nbrNeurone);
					
					JLabel layerPanelText = new JLabel("layerPane");
					layerPanel.add(layerPanelText);
					
					Border compound;
					String panelNumber = Integer.toString(pnlAffichage.getComponentCount());
					// Chaque sous-paneau a un titre du style "LayerX"
					compound = BorderFactory.createTitledBorder("Layer" + panelNumber);
					layerPanel.setBorder(compound);
					
//					layerPanel.setBounds(0, 0, pnlAffichage.getWidth() , pnlAffichage.getHeight());
					layerPanel.setMaximumSize(new Dimension(150, pnlAffichage.getHeight()));
					
					pnlAffichage.add(layerPanel);
					pnlAffichage.updateUI();
//					System.out.println(Arrays.deepToString(pnlAffichage.getComponents()));
				
				} else {
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
						x_test = donneesInput.get(0);
						y_test = donneesInput.get(1);
						
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
		
		
		
		
//// BOUTON D'ENTRAINEMENT DU RESEAU /////////////////////////////////////////////////////////////////////////////////
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
					myNet.train(x_test, y_test, Integer.parseInt(txtNbrEpochs.getText()), Double.parseDouble(txtLearningRate.getText()));
					
					// On appelle la methode errorGraph()
					ChartPanel ErrorChartPanel = myNet.errorGraph();
						
					// On rajoute le graphe retourné par cette méthode au Panel d'affichage
					pnlAffichage.removeAll();
					pnlAffichage.add(ErrorChartPanel);
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
		
		
		
		
		
//// BOUTON DE PREDICTION SUR DE NOUVELLES DONNEES /////////////////////////////////////////////////////////////////////////////////
	// Il prend un fichier .csv , comme le bouton "ImporterDonnes" 
		btnPredict = new Button("predict");
		btnPredict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				//// ON REUTILISE ICI LE CODE POUR LA DIALOG BOX DE "IMPORTERDONNEES" ////////////
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
		mainFrame.getContentPane().add(btnPredict, "cell 2 4,grow");
		
		
		
		
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
		mainFrame.getContentPane().add(btnPrint, "cell 2 5,grow");
		
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
