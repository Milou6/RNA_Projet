package RNApkg;

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


import java.awt.Button;
import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.DefaultListModel;
import javax.swing.ButtonGroup;


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
	
//	private XYSeriesCollection dataset;
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
		mainFrame.getContentPane().add(pnlAffichage, "cell 0 0 1 6,grow");
		pnlAffichage.setLayout(new MigLayout("", "[grow,center]", "[grow,center]"));
		
		
		lblStartup = new JLabel("Projet RNA");
		lblStartup.setFocusCycleRoot(true);
		lblStartup.setFont(new Font("OCR A Extended", Font.BOLD, 54));
		pnlAffichage.add(lblStartup, "cell 0 0");
		
		
		//===Bouton de d'ajout de couche===
		btnAddLayer = new Button("addLayer");
		btnAddLayer.addActionListener(new ActionListener() {
			/*
			 * Le RNA (Net.java) doit avoir été créer avant d'utiliser le bouton
			 * voir au fond après la création de l'interface graphique
			 */
			public void actionPerformed(ActionEvent e) {
/*				
				//On rajoute 3 layers à l'objet
				myNet.addLayer("input", "sigmoid", 2, true);
				myNet.addLayer("hidden", "sigmoid", 4, true);
//				myNet.addLayer("hidden", "sigmoid", 4, true);
//				myNet.addLayer("hidden", "sigmoid", 4, true);
				myNet.addLayer("output", "sigmoid", 1, false);
				txtConsoleOutput.append("\n Création des couches efféctuées");
*/
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
				} else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
											
				btnPopLayer.setEnabled(true);
				btnImportData.setEnabled(true);
			}
		});
		mainFrame.getContentPane().add(btnAddLayer, "cell 2 0,grow");
		
		//===Bouton de supression de couche===
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
					System.out.println("couche à supprimé : " + lstLayers.getSelectedValue().toString());
				} else {
				    System.out.println("User canceled / closed the dialog, result = " + result);
				}
			}
		});
		btnPopLayer.setEnabled(false);
		mainFrame.getContentPane().add(btnPopLayer, "cell 2 1,grow");
		
		
		
		//===Bouton d'importation des données===
		btnImportData = new Button("Importer donn\u00E9es d'input");
		btnImportData.setEnabled(false);
		btnImportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				// Tableau XOR: nos données d'input pour l'entrainement du Réseau
				final double [][] x_import = {{0,0}, {0,1}, {1,0}, {1,1}};
				final double [][] y_import = {{0}, {1}, {1}, {0}};
				
//				final double [][] y_import =  { {0},{0.198669331},{0.389418342},{0.564642473},{0.717356091},{0.841470985},{0.932039086},{0.98544973},{0.999573603},{0.973847631},{0.909297427},{0.808496404},{0.675463181},{0.515501372},{0.33498815},{0.141120008},{-0.058374143},{-0.255541102},{-0.442520443},{-0.611857891},{-0.756802495},{-0.871575772},{-0.951602074},{-0.993691004},{-0.996164609},{-0.958924275},{-0.883454656},{-0.772764488},{-0.631266638},{-0.464602179},{-0.279415498},{-0.083089403},{0.116549205},{0.311541364},{0.494113351},{0.656986599},{0.793667864},{0.898708096},{0.967919672},{0.998543345},{0.989358247},{0.940730557} };		
//				final double [][] x_import = { {0},{0.2},{0.4},{0.6},{0.8},{1},{1.2},{1.4},{1.6},{1.8},{2},{2.2},{2.4},{2.6},{2.8},{3},{3.2},{3.4},{3.6},{3.8},{4},{4.2},{4.4},{4.6},{4.8},{5},{5.2},{5.4},{5.6},{5.8},{6},{6.2},{6.4},{6.6},{6.8},{7},{7.2},{7.4},{7.6},{7.8},{8},{8.2} };
				

//				final double [][] x_import = {{1,3}, {2,4}, {3,1}, {4,2}};
//				final double [][] y_import = {{0,0}, {1,1}, {0,0}, {1,1}};
				
//				final double [][] x_import = {{1,1}, {1,2}, {2,1}, {2,2}, {3,3}, {3,4}, {4,3}, {4,4},
//											{1,3}, {1,4}, {2,3}, {2,4}, {3,1}, {3,2}, {4,1}, {4,4}};
//				final double [][] y_import = {{0}, {0},{0}, {0},{0}, {0},{0}, {0},  
//											{1}, {1},{1}, {1},{1}, {1},{1}, {1}};
				
				// logic gate AND
//				final double [][] x_import = {{0,0}, {0,1}, {1,0}, {1,1}};
//				final double [][] y_import = {{0}, {0}, {0}, {1}};
				
				// x au carré
//				final int [][] x_import = {{1}, {3}, {12}, {11}, {2}, {4}};
//				final int [][] y_import = {{1}, {9}, {144}, {121}, {4}, {16}};
				
				// nombre pair?
//				final int [][] x_import = {{1}, {3}, {12}, {11}, {2}, {4}, {8}, {10}, {32}, {5}, {9}};
//				final int [][] y_import = {{0}, {0}, {1}, {0}, {1}, {1}, {1}, {1}, {1}, {0}, {0}};
				
				
				//import des valeurs de tests
				x_test = x_import;
				y_test = y_import;
				
				txtConsoleOutput.append("\n Importations efféctuées");
				btnTrain.setEnabled(true);
			}
		});
		mainFrame.getContentPane().add(btnImportData, "cell 2 2,grow");
		
		
		
		
		//entrainement 
		btnTrain = new Button("train");
		btnTrain.setEnabled(false);
		btnTrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myNet.train(x_test, y_test,  10000, 0.5);
				//txtConsoleOutput.append(txtConsoleOutput.toString());
				//txtConsoleOutput.append("\n\n" + System.in.toString());
				
				//txtConsoleOutput.append("\n\n" + myNet.networkError.toString());
				
				XYSeriesCollection dataset = new XYSeriesCollection();
				XYSeries series1 = new XYSeries("Object 1", false, true);
				
				for (int i=0; i<myNet.networkError.size()-1; i++) {
					series1.add(i, myNet.networkError.get(i));
				}
				dataset.addSeries(series1);
				
				JFreeChart chart = ChartFactory.createXYLineChart("Network_Error", "Epoch", "Error", dataset, PlotOrientation.VERTICAL, true, true, false);
				//JFreeChart chart = ChartFactory.createXYLineChart("Network_Error", "error", "Epoch", dataset);
				chart.createBufferedImage(500, 500);
				
				ChartPanel CP = new ChartPanel(chart);
				pnlAffichage.removeAll();
				pnlAffichage.add(CP);
				pnlAffichage.validate();
				
				btnPredict.setEnabled(true);
				btnPrint.setEnabled(true);
			}
		});
		mainFrame.getContentPane().add(btnTrain, "cell 2 3,grow");
		
		
		
		//===prédiction sur les nouvelles donées===
		btnPredict = new Button("predict");
		btnPredict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final double [][] testDonne = {{0,1}};
				test = testDonne;			
				txtConsoleOutput.append(myNet.predict(test));
			}
		});
		btnPredict.setEnabled(false);
		mainFrame.getContentPane().add(btnPredict, "cell 2 4,grow");
		
		
		
		
		//===Affichage===
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
		txtConsoleOutput.append("\n Résaux de neurone Créer");
		
	}
	
	public static void ConsoleOutputAppend (String newText) {
		txtConsoleOutput.append(newText);
	}
}



