package RNApkg;

import java.awt.BorderLayout;
//Pour la méthode importCSV()
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/* Classe de base du package.
 * 
 * À l'initialisation, un objet Net est connecté à un objet de la classe DataBase.
 * 
 * L'objet Net peut instantier plusieurs objets Layer, qui forment les "couches" du RNA.
 * Chaque objet Layer contient des objets Neuron.
 * ____________________________________________________________________________________
 * Attributs:
 * 
 * ArrayList<Layer> layers : ArrayList qui store des objets Layer
 * 
 * DataBase netDataBase : Référence à l'objet DataBase connecté
 * 
 * ArrayList<ArrayList<RealMatrix>> batchActivations : garde l'activation de tous les neurones pour une epoch.
 * 													   Utilisé pour mettre à jour les poids.
 * 
 * ArrayList<ArrayList<RealMatrix>> batchErrors : garde l'erreur moyenne de tous les exemples d'entrainement pour une epoch.
 * 
 * ArrayList<Double> networkError : garde l'erreur moyenne de TOUTES LES EPOCHS.
 * 									Utilisé pour dessiner le graphe d'erreur à la fin de l'entrainement.
 */
public class Net {
	LIMCouche lcCouches;
	DataBase netDataBase;
	ArrayList<ArrayList<RealMatrix>> batchActivations;
	ArrayList<ArrayList<RealMatrix>> batchErrors;
	ArrayList<Double> networkError;

	//Constructeur
	public Net() {
		this.lcCouches = new LIMCouche();
		this.netDataBase = new DataBase(this);
		this.batchActivations = new ArrayList<ArrayList<RealMatrix>>();
		this.batchErrors = new ArrayList<ArrayList<RealMatrix>>();
		this.networkError = new ArrayList<Double>();
	}

	public String print() {
		String impression;

		impression = "\n NETWORK PRINT ( " + lcCouches.getSize() + " layers )";

		for(int i = 0; i < lcCouches.getSize();i++)
		{
			impression += "\n Layer " + lcCouches.indexOf(lcCouches.getElementAt(i)) + " :";
			impression += "Taille = "+lcCouches.getElementAt(i).getlayerSize()+" / ";
			impression += "Type d'activation = "+lcCouches.getElementAt(i).getActivation()+" / ";
			if(lcCouches.getElementAt(i).getHasBiasNeuron())
			{
				impression += "Neurone de Biais = Oui";
			}
			else
			{
				impression += "Neurone de Biais = Non";
			}
		}

		return impression;
	}


	/* Crée un JFrame représentant la moyenne de l'erreur du RNA pendant l'entraînement.
	 */
	public ChartPanel errorGraph() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("RNA_error", false, true);

		for (int i=0; i<networkError.size()-1; i++) {
			series1.add(i, networkError.get(i));
		}
		dataset.addSeries(series1);


		JFreeChart chart = ChartFactory.createXYLineChart("Network_Error", "Epoch", "Error", dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.createBufferedImage(600, 600);

		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new java.awt.BorderLayout());
		ChartPanel CP = new ChartPanel(chart);

		return CP;
	}

	/* Pareil à errorGraph(), mais pour utiliser en console dans Net pour débug
	 */
	@SuppressWarnings("deprecation")
	public void debugErrorGraph() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("RNA_error", false, true);

		for (int i=0; i<networkError.size()-1; i++) {
			series1.add(i, networkError.get(i));
		}
		dataset.addSeries(series1);


		JFreeChart chart = ChartFactory.createXYLineChart("Network_Error", "Epoch", "Error", dataset, PlotOrientation.VERTICAL, true, true, false);		
		chart.createBufferedImage(600, 600);

		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new java.awt.BorderLayout());
		ChartPanel CP = new ChartPanel(chart);

		jPanel1.add(CP,BorderLayout.CENTER);
		jPanel1.validate();

		JFrame frame = new JFrame();
		frame.add(jPanel1); 
		frame.setSize(300, 300); 
		frame.show(); 
	}



	/* Retourne le Hadamard product de 2 matrices.
	 * (multiplication élément-par-élément)
	 */
	public static RealMatrix hadamardProduct(RealMatrix m1, RealMatrix m2) {
		RealMatrix result = new Array2DRowRealMatrix(new double[m1.getRowDimension()][m1.getColumnDimension()]);		
		for (int i=0; i<m1.getRowDimension(); i++) {
			for (int j=0; j<m1.getColumnDimension(); j++) {
				result.setEntry(i, j, (m1.getEntry(i, j) * m2.getEntry(i, j)) );
			}
		}
		return result;
	}


	/* Applique la dérivée de la fonction sigmoid.
	 */
	public  RealMatrix sigmoidPrime(RealMatrix m1) {

		int m1_index = netDataBase.weightedInputs.indexOf(m1);
		RealMatrix corresponding_activation_matrix = netDataBase.activations.get(m1_index);
		//Si la layer en question a un BiasNeuron, on ne veut pas son activation pour le calcul
		if (lcCouches.getElementAt(m1_index).hasBiasNeuron == true) {
			//Ligne très longue, mais qui simplement supprime la dernière ligne de la matrice
			corresponding_activation_matrix = corresponding_activation_matrix.getSubMatrix(0, corresponding_activation_matrix.getRowDimension()-2, 0, corresponding_activation_matrix.getColumnDimension()-1);
		}
		RealMatrix result = corresponding_activation_matrix.copy();

		result = result.scalarMultiply(-1);
		result = result.scalarAdd(1);
		result = hadamardProduct(result, corresponding_activation_matrix);

		return result;
	}

	/* Applique la dérivée de la fonction ReLU.
	 */
	public  RealMatrix reluPrime(RealMatrix m1) {

		int m1_index = netDataBase.weightedInputs.indexOf(m1);

		RealMatrix corresponding_activation_matrix = netDataBase.activations.get(m1_index);
		//Si la layer en question a un BiasNeuron, on ne veut pas son activation pour le calcul
		if (lcCouches.getElementAt(m1_index).hasBiasNeuron == true) {
			//Ligne très longue, mais qui simplement supprime la dernière ligne de la matrice
			corresponding_activation_matrix = corresponding_activation_matrix.getSubMatrix(0, corresponding_activation_matrix.getRowDimension()-2, 0, corresponding_activation_matrix.getColumnDimension()-1);
		}
		RealMatrix result = corresponding_activation_matrix.copy();

		for (int row=0; row<m1.getRowDimension(); row++) {
			if (m1.getEntry(row, 0) >= 0) {
				m1.setEntry( row, 0, 1);
			}
			else {
				m1.setEntry( row, 0, 0);
			}	
		}
		return m1;
	}

	/* Applique la dérivée de la fonction Leaky-ReLU.
	 */
	public  RealMatrix lreluPrime(RealMatrix m1) {

		int m1_index = netDataBase.weightedInputs.indexOf(m1);

		RealMatrix corresponding_activation_matrix = netDataBase.activations.get(m1_index);
		//Si la layer en question a un BiasNeuron, on ne veut pas son activation pour le calcul
		if (lcCouches.getElementAt(m1_index).hasBiasNeuron == true) {
			//Ligne très longue, mais qui simplement supprime la dernière ligne de la matrice
			corresponding_activation_matrix = corresponding_activation_matrix.getSubMatrix(0, corresponding_activation_matrix.getRowDimension()-2, 0, corresponding_activation_matrix.getColumnDimension()-1);
		}
		RealMatrix result = corresponding_activation_matrix.copy();

		for (int row=0; row<m1.getRowDimension(); row++) {
			if (m1.getEntry(row, 0) >= 0) {
				m1.setEntry( row, 0, 1);
			}
			else {
				m1.setEntry( row, 0, 0.01);
			}	
		}
		return m1;
	}



	/* Rajoute un objet Layer à ArrayList<Layer> layers
	 * ___________________________________________________________________________________________
	 * Paramètres :
	 * 
	 * String layer_type : "input", "hidden" ou "output"
	 * 
	 * String layer_activation_function : détermine le type de fonction d'actiovation des neurones de la Layer ("sigmoid")
	 * 
	 * int layer_size : le nombre de RegularNeuron à ajouter à l'objet Layer (sans compter le BiasNeuron)
	 * 
	 * boolean add_bias_neuron : Si true, rajoute un BiasNeuron comme dernier élément de la Layer
	 */
	public Layer addLayer(String layer_type, String layer_activation_function, int layer_size, boolean add_bias_neuron) {
		Layer new_layer = new Layer(this, layer_activation_function);
		//new_layer.parent = this;
		//Switch instancie différents types de layer selon le paramètre
		switch(layer_type) {
		case "input":
			new_layer = new InputLayer(this, layer_activation_function);
			break;
		case "hidden":
			new_layer = new HiddenLayer(this, layer_activation_function);
			break;
		case "output":
			new_layer = new OutputLayer(this, layer_activation_function);
			break;
		}
		//On rajoute la layer vide à Net.lcCouches
		lcCouches.addLayer(new_layer);

		//On rajoute les neurones à la layer
		for (int i=0; i<layer_size; i++) {
			new_layer.addNeuron("regular");
		}
		//Rajout du neurone biais
		if (add_bias_neuron == true) {
			new_layer.addNeuron("bias");
			new_layer.hasBiasNeuron = true;
		}
		return new_layer;
	}

	/* Voir : importCSV()
	 * Prends un fichier .csv de données de test, et passe ces données par le réseau.
	 * Retourne un double[][] avec les prédictions du réseau.
	 */
	public double[][] testNetwork(String file_path, boolean skip_first_row, int outputs_per_row) {
		ArrayList<double[][]> imported = this.importCSV(file_path, skip_first_row, outputs_per_row);
		double[][] x_test = imported.get(0);
		double[][] y_test = imported.get(1);
		double[][] predictions = new double[x_test.length][];

		// Boucle sur chaque ligne du .csv importé
		for (int i=0; i<x_test.length; i++) {
			predictions[i] = new double[y_test[0].length];

			//Pour chaque RegularNeuron de la Layer0, on initialise son activation (valeurs des données d'Input)
			for (int j=0; j<lcCouches.getElementAt(0).layerSize; j++) {
				Neuron current_neuron = lcCouches.getElementAt(0).neurons.get(j);
				if (current_neuron instanceof RegularNeuron) {
					current_neuron.activation = x_test[i][j];
				}
			}

			for (int k = 0; k < lcCouches.getSize();k++) {
				lcCouches.getElementAt(k).forwardPropagate(x_test);
			}
			for (int l=0; l<lcCouches.getElementAt(lcCouches.getSize()-1).neurons.size(); l++) {
				predictions[i][l] = lcCouches.getElementAt(lcCouches.getSize()-1).neurons.get(l).activation;
			}
		}

		return predictions;
	}


	/* Méthode qui entraîne le RNA, en appliquant la forward- et back-propagation
	 * successivement pendant X epochs.
	 * __________________________________________________________________________
	 * Paramètres :
	 * 
	 * int[][] x_test : tableau de données d'Input
	 * 
	 * int[][] y_test : labels (=résultat attendu) des données d'Input
	 * 
	 * int epochs : nombre d'itérations sur la totalité des données d'entraînement
	 * 
	 * double learning_rate : taux d'apprentissage
	 */
	public void train(double[][] x_test, double[][] y_test, int epochs, double learning_rate) {

		// Test: vérifie que x_test et y_test ont la même taille
		if (x_test.length != y_test.length) {
			throw new ArrayIndexOutOfBoundsException("Input data and Label lengths do not match.");
		}
		// Test: vérifie que l'InputLayer et x_test sont compatibles
		int number_of_input_neurons = lcCouches.getElementAt(0).layerSize;
		if (lcCouches.getElementAt(0).hasBiasNeuron == true) {number_of_input_neurons -= 1;}
		if ( number_of_input_neurons != x_test[0].length) {
			throw new ArrayIndexOutOfBoundsException(String.format("Input entries are of length %d and InputLayer has %d input neuron(s)", x_test[0].length, number_of_input_neurons));
		}
		// Test: vérifie que l'OututLayer et y_test sont compatibles
		if (y_test[0].length != lcCouches.getElementAt(lcCouches.getSize()-1).layerSize) {
			throw new ArrayIndexOutOfBoundsException(String.format("Output data is of length %d and OutputLayer has %d neuron(s)", y_test[0].length, lcCouches.getElementAt(lcCouches.getSize()-1).layerSize));
		}


		//On initialise l'objet DataBase, ainsi que les matrices de poids et activations de l'objet
		DataBase dataBase = this.netDataBase;
		initializeWeights();
		initializeActivations();
		initializeWeightedInputs();
		initializeLayerError();

		//// RESET MORE DOWN when errorcompute is fixed
		ArrayList<RealMatrix> temp_batch_errors = new ArrayList<>();
		ArrayList<RealMatrix> temp_batch_activations = new ArrayList<RealMatrix>();
		double batch_error = 0.0;


		//Boucle des Epochs
		for (int e=0; e<epochs; e++) {
			System.out.println(" \n EPOCH " + e);
			this.batchErrors.clear();
			this.batchActivations.clear();


			// Boucle des Batch (chaque Batch correspond à une propagation en avant pour chaque entrée des données d'Input) 
			for (int f=0; f<x_test.length; f++) {

				//Pour chaque RegularNeuron de la Layer0, on initialise son activation (valeurs des données d'Input)
				for (int n=0; n<lcCouches.getElementAt(0).layerSize; n++) {
					Neuron current_neuron = lcCouches.getElementAt(0).neurons.get(n);
					if (current_neuron instanceof RegularNeuron) {
						current_neuron.activation = x_test[f][n];
					}
				}
				//FORWARD-PROP ICI
				for (int i = 0; i < lcCouches.getSize();i++) {
					lcCouches.getElementAt(i).forwardPropagate(x_test);
				}

				computeOutputError(y_test[f]);
				backPropagateError();


				// On garde les erreurs et les activations du RNA pour chaque x du Batch.
				// On crée des variables "temp" pour éviter de faire des shallow_copy
				temp_batch_errors = new ArrayList<>(netDataBase.layerError);
				temp_batch_activations = new ArrayList<RealMatrix>();
				for (int i=0; i<netDataBase.activations.size(); i++) {
					temp_batch_activations.add(i, netDataBase.activations.get(i).copy());
				}


				this.batchErrors.add(temp_batch_errors);
				this.batchActivations.add(temp_batch_activations);
			}

			// networkError utilisé pour faire le graph de l'erreur d'entraînement
			batch_error = 0.0;
			for (int i=0; i<this.batchErrors.size(); i++) {
				batch_error = batch_error + Math.abs(batchErrors.get(i).get(lcCouches.getSize()-1).getEntry(0, 0));
			}
			batch_error = batch_error / batchErrors.size();
			this.networkError.add(batch_error);
			
			updateNetworkWeights(batchErrors, batchActivations, learning_rate);

			// SUPER LENT => VOIR POUR AMELIORER
			//			ApplicationWindow.ConsoleOutputAppend(" \n EPOCH " + e );
		}//Epochs for-loop

	}


	public void stepTrain(double[][] x_test, double[][] y_test, int epochs, double learning_rate) {
		ArrayList<RealMatrix> temp_batch_errors = new ArrayList<>();
		ArrayList<RealMatrix> temp_batch_activations = new ArrayList<RealMatrix>();
		double batch_error = 0.0;

		//Boucle des Epochs
		for (int e=0; e<epochs; e++) {
			System.out.println(" \n EPOCH " + e);
			this.batchErrors.clear();
			this.batchActivations.clear();

			// Boucle des Batch (chaque Batch correspond à une propagation en avant pour chaque entrée des données d'Input) 
			for (int f=0; f<x_test.length; f++) {

				//Pour chaque RegularNeuron de la Layer0, on initialise son activation (valeurs des données d'Input)
				for (int n=0; n<lcCouches.getElementAt(0).layerSize; n++) {
					Neuron current_neuron = lcCouches.getElementAt(0).neurons.get(n);
					if (current_neuron instanceof RegularNeuron) {
						current_neuron.activation = x_test[f][n];
					}
				}
				//FORWARD-PROP ICI
				for (int i = 0; i < lcCouches.getSize();i++) {
					lcCouches.getElementAt(i).forwardPropagate(x_test);
				}

				computeOutputError(y_test[f]);
				backPropagateError();


				// On garde les erreurs et les activations du RNA pour chaque x du Batch.
				// On crée des variables "temp" pour éviter de faire des shallow_copy
				temp_batch_errors = new ArrayList<>(netDataBase.layerError);
				temp_batch_activations = new ArrayList<RealMatrix>();
				for (int i=0; i<netDataBase.activations.size(); i++) {
					temp_batch_activations.add(i, netDataBase.activations.get(i).copy());
				}

				this.batchErrors.add(temp_batch_errors);
				this.batchActivations.add(temp_batch_activations);
			}

			// networkError utilisé pour faire le graph de l'erreur d'entraînement
			batch_error = 0.0;
			for (int i=0; i<this.batchErrors.size(); i++) {
				batch_error = batch_error + Math.abs(batchErrors.get(i).get(lcCouches.getSize()-1).getEntry(0, 0));
			}
			batch_error = batch_error / batchErrors.size();
			this.networkError.add(batch_error);
			updateNetworkWeights(batchErrors, batchActivations, learning_rate);

			// SUPER LENT => VOIR POUR AMELIORER
			//ApplicationWindow.ConsoleOutputAppend(" \n EPOCH " + e );
		}//Epochs for-loop
	}



	/* Méthode appelée à l'intérieur de Net.train()
	 * Crée les matrices de poids du RNA et les rajoute à l'objet DataBase.
	 * Initialise tous les éléments de ces matrices à des valeurs aléatoires dans [0, 1].
	 */
	public void initializeWeights() {
		//On modélise chaque "espace" entre 2 layers par une matrice de weights[][] qui connectent ces 2 layers
		for (int i=0; i<lcCouches.getSize()-1; i++) {
			int current_layer_size = lcCouches.getElementAt(i).layerSize;
			int next_layer_size = lcCouches.getElementAt(i+1).layerSize;
			//Les neurones d'une layer(l) ne doivent pas connecter au BiasNeuron de la layer(l+1)
			if (lcCouches.getElementAt(i+1).hasBiasNeuron == true) {next_layer_size -= 1;}

			//Chaque matrice est initialisée avec des valeurs aléatoires
			double [][] new_weights = new double[next_layer_size][current_layer_size];
			for (int j=0; j<new_weights.length; j++) {
				for (int k=0; k<new_weights[j].length; k++) {
					new_weights[j][k] = Math.random();
				}
			}
			// On transforme le double[][] en RealMatrix
			RealMatrix weights_matrix = new Array2DRowRealMatrix(new_weights);
			//On rajoute chaque matrice à l'objet DataBase
			this.netDataBase.weights.add(weights_matrix);
		}
		//On demande à la DataBase de mettre à jour les poids de chaque neurone
		this.netDataBase.sendWeightsToNeurons();	
	}


	/* Méthode appelée à l'intérieur de Net.train()
	 * Crée les matrices d'activations du RNA et les rajoute à l'objet DataBase.
	 * Ces matrices sont remplies de ,{0.0, sauf pour les index des BiasNeuron (où l'on a des 1.0).
	 */
	public void initializeActivations() {
		for (int i=0; i<lcCouches.getSize(); i++) {
			int current_layer_size = lcCouches.getElementAt(i).layerSize;
			double [][] activation_vector = new double[current_layer_size][1];
			// On transforme le double[][] en RealMatrix
			RealMatrix activations_matrix = new Array2DRowRealMatrix(activation_vector);

			// On initialise chaque valeur des activation_matrix
			for (int j=0; j<lcCouches.getElementAt(i).layerSize; j++) {
				Neuron current_neuron = lcCouches.getElementAt(i).neurons.get(j);
				activations_matrix.setEntry(j, 0, current_neuron.activation);
			}
			//On rajoute le tout à la DataBase.activations
			this.netDataBase.activations.add(activations_matrix);
		}
	}


	/* Méthode appelée à l'intérieur de Net.train()
	 * Crée les matrices de weighted inputs du RNA et les rajoute à l'objet DataBase.
	 * 
	 * Le weighted input d'un neurone représente son output AVANT de lui appliquer la fonction sigmoid.
	 * 
	 * z = SUM(x * poids) + bias
	 */
	public void initializeWeightedInputs() {
		for (int i=0; i<lcCouches.getSize(); i++) {
			int current_layer_size = lcCouches.getElementAt(i).layerSize;
			if (lcCouches.getElementAt(i).hasBiasNeuron == true) {current_layer_size -= 1;}

			double [][] weighted_inputs_vector = new double[current_layer_size][1];
			// On transforme le double[][] en RealMatrix
			RealMatrix weighted_inputs_matrix = new Array2DRowRealMatrix(weighted_inputs_vector);

			// On initialise chaque valeur des weighted_inputs_matrix
			for (int j=0; j<current_layer_size; j++) {
				Neuron current_neuron = lcCouches.getElementAt(i).neurons.get(j);
				weighted_inputs_matrix.setEntry(j, 0, current_neuron.activation);
			}
			//On rajoute le tout à la DataBase.activations
			this.netDataBase.weightedInputs.add(weighted_inputs_matrix);
		}
	}


	/* Méthode appelée à l'intérieur de Net.train()
	 * Crée les matrices d'erreur du RNA et les rajoute à l'objet DataBase.
	 * 
	 * L'erreur de chaque Layer est utilisée pour calculer les mises à jour des poids.
	 */
	public void initializeLayerError() {
		for (int i=0; i<lcCouches.getSize(); i++) {
			int current_layer_size = lcCouches.getElementAt(i).layerSize;
			if (lcCouches.getElementAt(i).hasBiasNeuron == true) {current_layer_size -= 1;}

			double [][] error_vector = new double[current_layer_size][1];
			// On transforme le double[][] en RealMatrix
			RealMatrix error_matrix = new Array2DRowRealMatrix(error_vector);

			// On initialise chaque valeur des weighted_inputs_matrix
			for (int j=0; j<current_layer_size; j++) {
				Neuron current_neuron = lcCouches.getElementAt(i).neurons.get(j);
				error_matrix.setEntry(j, 0, current_neuron.activation);
			}
			//On rajoute le tout à la DataBase.activations
			this.netDataBase.layerError.add(error_matrix);
		}
	}


	/* Méthode qui calcule l'erreur de l'Output.
	 * 
	 * Utilise le Mean Squared Error (MSE).
	 */
	public void computeOutputError(double[] real_output) {
		//On transforme l'output réel en double[][]
		int output_length = real_output.length;
		double[][] real_output_array = new double[output_length][1];
		for (int i=0; i<output_length; i++) {
			real_output_array[i][0] = real_output[i];
		}
		//Ensuite on crée une RealMatrix à partir de ce double[][]
		RealMatrix real_output_vector = new Array2DRowRealMatrix(real_output_array);

		// L'index de la dernière layer
		int index = netDataBase.layerError.size()-1;

		RealMatrix right_member = new Array2DRowRealMatrix();
		switch (this.lcCouches.getElementAt(lcCouches.getSize()-1).activation_function) {
		case "sigmoid":
			//right_member correspond à [sigmoid(z) * (1-sigmoid(z))]
			right_member = netDataBase.activations.get(index).copy();
			// (-sigmoid(z))
			right_member = right_member.scalarMultiply(-1);
			// (1-sigmoid(z))
			right_member = right_member.scalarAdd(1);
			// [sigmoid(z) * (1-sigmoid(z))]
			right_member = hadamardProduct(right_member, netDataBase.activations.get(index).copy()) ;
			//				System.out.println(right_member);
			break;

		case "relu":
			// Ici on utilise weightedInputs car on veut z^l, et non a^l
			right_member = netDataBase.weightedInputs.get(index).copy();
			reluPrime(right_member);
			break;
		case "lrelu":
			// Ici on utilise weightedInputs car on veut z^l, et non a^l
			right_member = netDataBase.weightedInputs.get(index).copy();
			lreluPrime(right_member);
			break;
		}


		//left_member correspond à (a - y)
		RealMatrix left_member = netDataBase.activations.get(index).copy().subtract(real_output_vector);

		//output_error = (a -y) dot [sigmoid(z) * (1-sigmoid(z))]
		RealMatrix output_error = hadamardProduct(left_member, right_member);

		netDataBase.layerError.set(index, output_error);
	}


	/* Backpropagation de l'erreur de l'OutputLayer à toutes
	 * les autres Layer.
	 */
	public void backPropagateError() {
		//Boucle sur toutes les layers l = L-1, L-2, ... , 1
		for (int i=lcCouches.getSize()-2; i>0; i--) {
			RealMatrix left_member = netDataBase.weights.get(i).copy();
			//Si la layer en question a un BiasNeuron, on ne veut pas ses poids pour le calcul
			if (lcCouches.getElementAt(i).hasBiasNeuron == true) {
				left_member = left_member.getSubMatrix(0, left_member.getRowDimension()-1, 0, left_member.getColumnDimension()-2);
			}
			left_member = left_member.transpose().multiply(netDataBase.layerError.get(i+1));


			RealMatrix right_member = netDataBase.weightedInputs.get(i).copy();

			switch (this.lcCouches.getElementAt(i).activation_function) {
			case "sigmoid":
				right_member = sigmoidPrime(right_member);
				break;
			case "relu":
				right_member = reluPrime(right_member);
				break;
			case "lrelu":
				right_member = lreluPrime(right_member);
				break;
			}


			//layer_error = [(w^l+1)^T * error^l+1] dot sigmoidPrime(z^l)
			RealMatrix layer_error = hadamardProduct(left_member, right_member);
			netDataBase.layerError.set(i, layer_error);
		}
	}


	/*
	 * Mets à jour les poids des RegularNeuron et des BiasNeuron.
	 */
	public void updateNetworkWeights(ArrayList<ArrayList<RealMatrix>> batchErrors, ArrayList<ArrayList<RealMatrix>> batchActivations, double learning_rate) {
		for (int i=lcCouches.getSize()-1; i>0; i--) {

			RealMatrix sum = MatrixUtils.createRealMatrix(netDataBase.weights.get(i-1).getRowDimension(), netDataBase.weights.get(i-1).getColumnDimension());
			if (lcCouches.getElementAt(i-1).hasBiasNeuron == true) {
				sum = sum.getSubMatrix(0, sum.getRowDimension()-1, 0, sum.getColumnDimension()-2);
			}

			for (int j=0; j<batchErrors.size(); j++) {
				RealMatrix current_batch_error = batchErrors.get(j).get(i).copy();
				RealMatrix a_transpose = batchActivations.get(j).get(i-1).copy();
				if (lcCouches.getElementAt(i-1).hasBiasNeuron == true) {
					a_transpose = a_transpose.getSubMatrix(0, a_transpose.getRowDimension()-2, 0, a_transpose.getColumnDimension()-1);
				}

				RealMatrix error_transpose_product = current_batch_error.multiply(a_transpose.transpose());
				sum = sum.add(error_transpose_product.copy());		
			}

			sum = sum.scalarMultiply(learning_rate/batchErrors.size());
			sum = sum.scalarMultiply(-1);


			RealMatrix weights_to_update = netDataBase.weights.get(i-1).copy();
			if (lcCouches.getElementAt(i-1).hasBiasNeuron == true) {
				weights_to_update = weights_to_update.getSubMatrix(0, weights_to_update.getRowDimension()-1, 0, weights_to_update.getColumnDimension()-2);
			}

			weights_to_update = weights_to_update.add(sum);	
			RealMatrix updated_weights = netDataBase.weights.get(i-1);
			updated_weights.setSubMatrix(weights_to_update.copy().getData(), 0, 0);

			netDataBase.sendWeightsToNeurons();
		}


		////////  UPDATE BIASES /////////////////////
		for (int i=lcCouches.getSize()-1; i>-1; i--) {
			if (lcCouches.getElementAt(i).hasBiasNeuron == true) {
				RealMatrix sum = MatrixUtils.createRealMatrix(netDataBase.layerError.get(i+1).getRowDimension(), netDataBase.layerError.get(i+1).getColumnDimension());

				for (int j=0; j<batchErrors.size(); j++) {
					RealMatrix current_batch_error = batchErrors.get(j).get(i+1).copy();
					sum = sum.add(current_batch_error.copy());	
				}

				sum = sum.scalarMultiply(learning_rate/batchErrors.size());
				sum = sum.scalarMultiply(-1);

				RealMatrix biases_to_update = netDataBase.weights.get(i).copy();
				biases_to_update = biases_to_update.getSubMatrix(0, biases_to_update.getRowDimension()-1, biases_to_update.getColumnDimension()-1, biases_to_update.getColumnDimension()-1);
				biases_to_update = biases_to_update.add(sum);

				RealMatrix updated_biases = netDataBase.weights.get(i);
				updated_biases.setSubMatrix(biases_to_update.getData(), 0, updated_biases.getColumnDimension()-1);

				netDataBase.sendWeightsToNeurons();
			}
		}
	}

	/* Transforme les données du fichier CSV dont le PATH est donné en paramètre,
	 * en un ArrayList<double[][]> avec 2 éléments.
	 * 
	 * élément 1 : les Inputs d'entrainement
	 * élément 2 : les Output d'entrainement
	 */
	public ArrayList<double[][]> importCSV(String file_path, boolean skip_first_row, int outputs_per_row) {
		BufferedReader csvReader = null;
		ArrayList<ArrayList<Double>> x_import = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> y_import = new ArrayList<ArrayList<Double>>();

		// On crée un Reader pour lire le fichier CSV
		try {
			csvReader = new BufferedReader(new FileReader(file_path));
			String row = "";
			ArrayList<Double> inputs = new ArrayList<Double>();
			ArrayList<Double> outputs = new ArrayList<Double>();

			// On lit le fichier CSV ligne par ligne
			while ((row = csvReader.readLine()) != null) {
				if (skip_first_row == true) {
					skip_first_row = false;
					continue;
				}

				inputs.clear();
				outputs.clear();
				String[] data = row.split(";");

				// Chaque ligne est transformée en un Array de Double
				for (String s : data) {
					inputs.add(Double.parseDouble(s));
				}

				// On enlève de chaque ligne les dernières valeurs, qui correspondent aux Outputs souhaités
				for (int i = 0; i<outputs_per_row; i++) {
					outputs.add(0, inputs.remove(inputs.size()-1));
				}

				x_import.add((ArrayList<Double>) inputs.clone());
				y_import.add((ArrayList<Double>) outputs.clone());	    
			}
			csvReader.close();

			// La méthode doit retourner des double[][], donc on doit transformer les ArrayList...
			double[][] x_result = new double[x_import.size()][];
			double[][] y_result = new double[x_import.size()][];

			// Transforme les ArrayList<ArrayList<Double>> en double[][]
			for (int i=0; i<x_import.size(); i++) {
				x_result[i] = new double[x_import.get(i).size()];
				for (int j=0; j<x_import.get(i).size(); j++) {
					x_result[i][j] = x_import.get(i).get(j);
				}
			}

			// Transforme les ArrayList<ArrayList<Double>> en double[][]
			for (int i=0; i<y_import.size(); i++) {
				y_result[i] = new double[y_import.get(i).size()];
				for (int j=0; j<y_import.get(i).size(); j++) {
					y_result[i][j] = y_import.get(i).get(j);
				}
			}

			// ArrayList retourné contenant 2 éléments : une liste d'Inputs, et une liste d'Outputs
			ArrayList<double[][]> result = new ArrayList<double[][]>();
			result.add(x_result);
			result.add(y_result);

			return result;
		}


		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	public static void main(String[] args) {

		/////////////////////////////////  TEST 1 : XOR LOGIC GATE ////////////////////////////////////////////////////////////////

//		// Création d'un objet Net
//		Net myNet = new Net();
//
//		// Importation des données
//		ArrayList<double[][]> imported_data = myNet.importCSV("../RNA_Projet/src/donneeEntrainement/XOR_data.csv", true, 1);
//		final double [][] x_train = imported_data.get(0);
//		final double [][] y_train = imported_data.get(1);
//
//
//		// On rajoute 3 layers au réseau
//		myNet.addLayer("input", "sigmoid", 2, true);
//		myNet.addLayer("hidden", "sigmoid", 4, true);
//		myNet.addLayer("output", "sigmoid", 1, false);	
//
//		// Entrainement, 8000 epochs, learning_rate 0.5
//		myNet.train(x_train, y_train, 8000, 0.5);
//
//		// On teste le réseau sur les mêmes données
//		System.out.println("\n PREDICTIONS : ");
//		System.out.println(Arrays.deepToString(myNet.testNetwork("../RNA_Projet/src/donneeEntrainement/XOR_data.csv", true, 1)));
//		
//		// Print du réseau
//		System.out.println(myNet.print());
//
//		// Print du graphe d'erreur
//		myNet.debugErrorGraph();
//		myNet.netDataBase.print();

		/////////////////////////////////  /TEST 1 : XOR LOGIC GATE ////////////////////////////////////////////////////////////////	     




		/////////////////////////////////  TEST 2 : IRIS DATASET ////////////////////////////////////////////////////////////////
				// Création d'un objet Net
				Net myNet = new Net();
				
				// Importation des données
				ArrayList<double[][]> imported_data = myNet.importCSV("../RNA_Projet/src/donneeEntrainement/V2_Iris_TRAINING.csv", true, 1);
				final double [][] x_train = imported_data.get(0);
				final double [][] y_train = imported_data.get(1);
				
				
				// On rajoute 3 layers au réseau
				myNet.addLayer("input", "relu", 4, true);
				myNet.addLayer("hidden", "relu", 10, true);
				myNet.addLayer("output", "relu", 1, false);	
		
				// Entrainement, 2000 epochs, learning_rate 0.001
				myNet.train(x_train, y_train, 2000, 0.001);
		
				// On teste le réseau sur les données mises à part
				System.out.println("\n PREDICTIONS : ");
				System.out.println(Arrays.deepToString(myNet.testNetwork("../RNA_Projet/src/donneeEntrainement/V2_Iris_TESTING.csv", true, 1)));
				
				// Print du réseau
				System.out.println(myNet.print());
				
				// Print du graphe d'erreur
				myNet.debugErrorGraph();
		/////////////////////////////////  /TEST 2 : IRIS DATASET ////////////////////////////////////////////////////////////////

	}
}
