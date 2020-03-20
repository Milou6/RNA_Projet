package RNApkg;


import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.linear.*;
import org.tc33.jheatchart.HeatChart;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.*;
import org.jfree.data.xy.*;

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
 */
public class Net {
	ArrayList<Layer> layers;
	DataBase netDataBase;
	ArrayList<Double> networkError;
	
	//Constructeur
	public Net() {
		this.layers = new ArrayList<Layer>();
		this.netDataBase = new DataBase(this);
		this.networkError = new ArrayList<Double>();
	}
	
	private void print() {
		System.out.println("\n NETWORK PRINT ( " + layers.size() + " layers )");
		
		for (Layer i : layers) {
			System.out.println("\n Layer " + layers.indexOf(i) + " :");
			for (Neuron n : i.neurons) {
				System.out.println(n);
			}
		}
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
		if (layers.get(m1_index).hasBiasNeuron == true) {
			//Ligne très longue, mais qui simplement supprime la dernière ligne de la matrice
			corresponding_activation_matrix = corresponding_activation_matrix.getSubMatrix(0, corresponding_activation_matrix.getRowDimension()-2, 0, corresponding_activation_matrix.getColumnDimension()-1);
		}
		RealMatrix result = corresponding_activation_matrix.copy();
		
		result = result.scalarMultiply(-1);
		result = result.scalarAdd(1);
		result = hadamardProduct(result, corresponding_activation_matrix);
		
		return result;
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
	public void addLayer(String layer_type, String layer_activation_function, int layer_size, boolean add_bias_neuron) {
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
		//On rajoute la layer vide à Net.layers
		layers.add(new_layer);
		//On rajoute les neurones à la layer
		for (int i=0; i<layer_size; i++) {
			new_layer.addNeuron("regular");
		}
		//Rajout du neurone biais
		if (add_bias_neuron == true) {
			new_layer.addNeuron("bias");
			new_layer.hasBiasNeuron = true;
		}
	}

	
	public void predict(double[][] x_test) {
		double[] predicted = new double[x_test.length];
		for (int f=0; f<x_test.length; f++) {
//			for (int f=0; f<1; f++) {
				
				//Pour chaque RegularNeuron de la Layer0, on initialise son activation (valeurs des données d'Input)
				for (int n=0; n<layers.get(0).layerSize; n++) {
					Neuron current_neuron = layers.get(0).neurons.get(n);
					if (current_neuron instanceof RegularNeuron) {
						current_neuron.activation = x_test[f][n];
					}
				}
				
			for (Layer l : this.layers) {
				l.forwardPropagate(x_test);
			}
			predicted[f] = layers.get(layers.size()-1).neurons.get(0).activation;
		}
		System.out.println("PREDICTION: ");
		System.out.println(Arrays.toString(predicted));
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
	 */
	public void train(double[][] x_test, double[][] y_test, int epochs, double learning_rate) {
		//On initialise l'objet DataBase, ainsi que les matrices de poids et activations de l'objet
		DataBase dataBase = this.netDataBase;
		initializeWeights();
		initializeActivations();
		initializeWeightedInputs();
		initializeLayerError();
		
//		this.print();
//		this.netDataBase.print();
		
		int print_int = 1;
		//Boucle des Epochs
		for (int e=0; e<epochs; e++) {
			ArrayList<RealMatrix> batch_errors = new ArrayList<RealMatrix>();
			
			// Boucle des Batch (chaque Batch correspond à une propagation en avant pour chaque entrée des données d'Input) 
			for (int f=0; f<x_test.length; f++) {
//			for (int f=0; f<1; f++) {
				
				//Pour chaque RegularNeuron de la Layer0, on initialise son activation (valeurs des données d'Input)
				for (int n=0; n<layers.get(0).layerSize; n++) {
					Neuron current_neuron = layers.get(0).neurons.get(n);
					if (current_neuron instanceof RegularNeuron) {
						current_neuron.activation = x_test[f][n];
					}
				}
				//FORWARD-PROP ICI
				for (Layer l : this.layers) {
					l.forwardPropagate(x_test);
				}

//				this.print();
//				this.netDataBase.print();
				
//				System.out.println("trace");
				//Calculer l'erreur moyenne (de tous les données du Batch!) entre output du RNA / output réel
//				RealMatrix output_error = computeOutputError(y_test[f]);
				computeOutputError(y_test[f]);
//				batch_errors.add(f, output_error);
				
				this.networkError.add(netDataBase.layerError.get(netDataBase.layerError.size()-1).getEntry(0, 0));


				backPropagateError();
//				netDataBase.print();
//				this.print();
//				this.netDataBase.print();
				
				updateNetworkWeights(x_test, learning_rate);
				
				System.out.println(" Epoch " + e);
//				System.out.println(Arrays.toString(y_test[f]));
//				System.out.println(Arrays.deepToString(netDataBase.activations.get(netDataBase.activations.size()-1).getData()));
//				System.out.println(netDataBase.layerError.get(netDataBase.layerError.size()-1));
				//FORWARD-PROP ICI

			}
			
//			System.out.println("AFTER F-PROP :");
//			this.print();
//			System.out.println("\n BATCH ERRORS :");
//			System.out.println(batch_errors);
			
			//Ce bloc de code calcule dans output_error la moyenne de l'erreur de chaque élément du Batch
//			RealMatrix output_error = batch_errors.get(0).copy();
//			for (int i=1; i<batch_errors.size(); i++) {
//				output_error = output_error.add(batch_errors.get(i));
////				System.out.println(output_error);
//			}
//			output_error = output_error.scalarMultiply(1.0/batch_errors.size());
//			System.out.println("ERROR UPDATE : ");
//			System.out.println(output_error);
//			netDataBase.layerError.set(netDataBase.layerError.size()-1, output_error);
			
//			backPropagateError();
////		netDataBase.print();
//			updateNetworkWeights(x_test, learning_rate);
					
		}//Epochs for-loop

	}
	
	
	/* Méthode appelée à l'intérieur de Net.train()
	 * Crée les matrices de poids du RNA et les rajoute à l'objet DataBase.
	 * Initialise tous les éléments de ces matrices à des valeurs aléatoires dans [0, 1].
	 */
	public void initializeWeights() {
		//On modélise chaque "espace" entre 2 layers par une matrice de weights[][] qui connectent ces 2 layers
		for (int i=0; i<layers.size()-1; i++) {
			int current_layer_size = layers.get(i).layerSize;
			int next_layer_size = layers.get(i+1).layerSize;
			//Les neurones d'une layer(l) ne doivent pas connecter au BiasNeuron de la layer(l+1)
			if (layers.get(i+1).hasBiasNeuron == true) {next_layer_size -= 1;}
			
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
		for (int i=0; i<layers.size(); i++) {
			int current_layer_size = layers.get(i).layerSize;
			double [][] activation_vector = new double[current_layer_size][1];
			// On transforme le double[][] en RealMatrix
			RealMatrix activations_matrix = new Array2DRowRealMatrix(activation_vector);
			
			// On initialise chaque valeur des activation_matrix
			for (int j=0; j<layers.get(i).layerSize; j++) {
				Neuron current_neuron = layers.get(i).neurons.get(j);
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
		for (int i=0; i<layers.size(); i++) {
			int current_layer_size = layers.get(i).layerSize;
			if (layers.get(i).hasBiasNeuron == true) {current_layer_size -= 1;}
			
			double [][] weighted_inputs_vector = new double[current_layer_size][1];
			// On transforme le double[][] en RealMatrix
			RealMatrix weighted_inputs_matrix = new Array2DRowRealMatrix(weighted_inputs_vector);
			
			// On initialise chaque valeur des weighted_inputs_matrix
			for (int j=0; j<current_layer_size; j++) {
				Neuron current_neuron = layers.get(i).neurons.get(j);
				weighted_inputs_matrix.setEntry(j, 0, current_neuron.activation);
			}
			//On rajoute le tout à la DataBase.activations
			this.netDataBase.weightedInputs.add(weighted_inputs_matrix);
		}
	}
	
	
	/* Méthode appelée à l'intérieur de Net.train()
	 * Crée les matrices d'erreur du RNA et les rajoute à l'objet DataBase.
	 * 
	 * L'erreur de chawue Layer est utilisée pour calculer les mises à jour des poids.
	 */
	public void initializeLayerError() {
		for (int i=0; i<layers.size(); i++) {
			int current_layer_size = layers.get(i).layerSize;
			if (layers.get(i).hasBiasNeuron == true) {current_layer_size -= 1;}
			
			double [][] error_vector = new double[current_layer_size][1];
			// On transforme le double[][] en RealMatrix
			RealMatrix error_matrix = new Array2DRowRealMatrix(error_vector);
			
			// On initialise chaque valeur des weighted_inputs_matrix
			for (int j=0; j<current_layer_size; j++) {
				Neuron current_neuron = layers.get(i).neurons.get(j);
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
//		System.out.println(real_output_vector);
		
		int index = netDataBase.layerError.size()-1;
		
		//right_member correspond à [sigmoid(z) * (1-sigmoid(z))]
		RealMatrix right_member = netDataBase.activations.get(index).copy();
		right_member = right_member.scalarMultiply(-1);
		right_member = right_member.scalarAdd(1);
		right_member = hadamardProduct(right_member, netDataBase.activations.get(index).copy()) ;
//		System.out.println(right_member);
		

		//left_member correspond à (a - y)
		RealMatrix left_member = netDataBase.activations.get(index).copy().subtract(real_output_vector);
//		netDataBase.layerError.set(index, output_error);
//		System.out.println(left_member);
		
		//output_error = (a -y) dot [sigmoid(z) * (1-sigmoid(z))]
		RealMatrix output_error = hadamardProduct(left_member, right_member);
		
//		System.out.println("\n real_output_vector : " + real_output_vector);
//		System.out.println("network output : " + netDataBase.activations.get(index));
//		System.out.println("left_member : " + left_member);
//		System.out.println("output_error : " + output_error);

		//REMOVED FOR BATCH VERSION
		netDataBase.layerError.set(index, output_error);
//		System.out.println(output_error);
//		return output_error;
	}
	
	
	/* Backpropagation de l'erreur de l'OutputLayer à toutes
	 * les autres Layer.
	 */
	public void backPropagateError() {
		//Boucle sur toutes les layers l = L-1, L-2, ... , 1
		for (int i=layers.size()-2; i>0; i--) {
//			System.out.println(i);
			
			RealMatrix left_member = netDataBase.weights.get(i).copy();
			//Si la layer en question a un BiasNeuron, on ne veut pas ses poids pour le calcul
			if (layers.get(i).hasBiasNeuron == true) {
				left_member = left_member.getSubMatrix(0, left_member.getRowDimension()-1, 0, left_member.getColumnDimension()-2);
			}
			left_member = left_member.transpose().multiply(netDataBase.layerError.get(i+1));
//			System.out.println(left_member);
			
			
			RealMatrix right_member = netDataBase.weightedInputs.get(i).copy();
			right_member = sigmoidPrime(right_member);
//			System.out.println(right_member);
			
			//layer_error = [(w^l+1)^T * error^l+1] dot sigmoidPrime(z^l)
			RealMatrix layer_error = hadamardProduct(left_member, right_member);
			netDataBase.layerError.set(i, layer_error);
//			System.out.println(layer_error);
		}
	}

	
	/*
	 * Mets à jour les poids des RegularNeuron et des BiasNeuron.
	 */
	public void updateNetworkWeights(double[][] x_test, double learning_rate) {
		// i :  2 and 1
		for (int i=layers.size()-1; i>0; i--) {
			
			RealMatrix to_subtract = netDataBase.layerError.get(i).copy();
			RealMatrix a_transpose = netDataBase.activations.get(i-1).copy();
			if (layers.get(i-1).hasBiasNeuron == true) {
				a_transpose = a_transpose.getSubMatrix(0, a_transpose.getRowDimension()-2, 0, a_transpose.getColumnDimension()-1);
			}
//			System.out.println("\n UPDATE WEIGHT trace");
//			System.out.println(a_transpose);
			
			to_subtract = to_subtract.multiply(a_transpose.transpose());
			
			to_subtract = to_subtract.scalarMultiply( (learning_rate/x_test.length) );
//			System.out.println("to subtract : " + to_subtract);
			
			RealMatrix updated_weights = netDataBase.weights.get(i-1).copy();
			if (layers.get(i-1).hasBiasNeuron == true) {
//				System.out.println("old weights : " + updated_weights);
				updated_weights = updated_weights.getSubMatrix(0, updated_weights.getRowDimension()-1, 0, updated_weights.getColumnDimension()-2);
			}
			updated_weights = updated_weights.subtract(to_subtract);
			RealMatrix old_weights = netDataBase.weights.get(i-1);
			old_weights.setSubMatrix(updated_weights.getData(), 0, 0);
//			System.out.println("new weights : " + old_weights);
			netDataBase.sendWeightsToNeurons();
		}
		////////  UPDATE BIASES /////////////////////
		for (int i=layers.size()-1; i>-1; i--) {
			if (layers.get(i).hasBiasNeuron == true) {
//				System.out.println(i + " has bias");
				RealMatrix to_subtract = netDataBase.layerError.get(i+1).copy();
				to_subtract = to_subtract.scalarMultiply( (learning_rate/x_test.length) );
				
				RealMatrix old_bias_matrix = netDataBase.weights.get(i).copy();
				old_bias_matrix = old_bias_matrix.getSubMatrix(0, old_bias_matrix.getRowDimension()-1, old_bias_matrix.getColumnDimension()-1, old_bias_matrix.getColumnDimension()-1);
//				System.out.println(old_bias_matrix);
				
				RealMatrix new_bias_weights = old_bias_matrix.subtract(to_subtract);
				
				
				RealMatrix final_bias_weights = netDataBase.weights.get(i);
//				System.out.println("old weights : " + final_bias_weights);
//				System.out.println("Bias to subtract : " + to_subtract);
				final_bias_weights.setSubMatrix(new_bias_weights.getData(), 0, final_bias_weights.getColumnDimension()-1);
//				System.out.println("new bias weights : " + final_bias_weights);
				netDataBase.sendWeightsToNeurons();
				
			}
		}
		
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public static void main(String[] args) {
		
//		// Tableau XOR: nos données d'input pour l'entrainement du Réseau
		final double [][] x_test = {{0,0}, {0,1}, {1,0}, {1,1}};
		final double [][] y_test = {{0}, {1}, {1}, {0}};
		
		
//		final double [][] y_test =  { {0},{0.198669331},{0.389418342},{0.564642473},{0.717356091},{0.841470985},{0.932039086},{0.98544973},{0.999573603},{0.973847631},{0.909297427},{0.808496404},{0.675463181},{0.515501372},{0.33498815},{0.141120008},{-0.058374143},{-0.255541102},{-0.442520443},{-0.611857891},{-0.756802495},{-0.871575772},{-0.951602074},{-0.993691004},{-0.996164609},{-0.958924275},{-0.883454656},{-0.772764488},{-0.631266638},{-0.464602179},{-0.279415498},{-0.083089403},{0.116549205},{0.311541364},{0.494113351},{0.656986599},{0.793667864},{0.898708096},{0.967919672},{0.998543345},{0.989358247},{0.940730557} };		
//		final double [][] x_test = { {0},{0.2},{0.4},{0.6},{0.8},{1},{1.2},{1.4},{1.6},{1.8},{2},{2.2},{2.4},{2.6},{2.8},{3},{3.2},{3.4},{3.6},{3.8},{4},{4.2},{4.4},{4.6},{4.8},{5},{5.2},{5.4},{5.6},{5.8},{6},{6.2},{6.4},{6.6},{6.8},{7},{7.2},{7.4},{7.6},{7.8},{8},{8.2} };
		

//		final double [][] x_test = {{1,3}, {2,4}, {3,1}, {4,2}};
//		final double [][] y_test = {{0,0}, {1,1}, {0,0}, {1,1}};
		
//		final double [][] x_test = {{1,1}, {1,2}, {2,1}, {2,2}, {3,3}, {3,4}, {4,3}, {4,4},
//									{1,3}, {1,4}, {2,3}, {2,4}, {3,1}, {3,2}, {4,1}, {4,4}};
//		final double [][] y_test = {{0}, {0},{0}, {0},{0}, {0},{0}, {0},  
//									{1}, {1},{1}, {1},{1}, {1},{1}, {1}};
		
		// logic gate AND
//		final double [][] x_test = {{0,0}, {0,1}, {1,0}, {1,1}};
//		final double [][] y_test = {{0}, {0}, {0}, {1}};
		
		// x au carré
//		final int [][] x_test = {{1}, {3}, {12}, {11}, {2}, {4}};
//		final int [][] y_test = {{1}, {9}, {144}, {121}, {4}, {16}};
		
		// nombre pair?
//		final int [][] x_test = {{1}, {3}, {12}, {11}, {2}, {4}, {8}, {10}, {32}, {5}, {9}};
//		final int [][] y_test = {{0}, {0}, {1}, {0}, {1}, {1}, {1}, {1}, {1}, {0}, {0}};
		
		
		//Création d'un objet Net
		Net myNet = new Net();
		
		//On rajoute 3 layers à l'objet
		myNet.addLayer("input", "sigmoid", 2, true);
		myNet.addLayer("hidden", "sigmoid", 4, true);
//		myNet.addLayer("hidden", "sigmoid", 4, true);
//		myNet.addLayer("hidden", "sigmoid", 4, true);
		myNet.addLayer("output", "sigmoid", 1, false);
		


		
		myNet.train(x_test, y_test,  2000, 2);	
//		generateMap(1);
//		myNet.print();
		
		//TEMPORAIRE POUR TESTER		
//		myNet.netDataBase.print();
		
//		double[] test = {0,,{0.4};
//		System.out.println(myNet.predictSingle(test));
/////

		System.out.println(myNet.networkError);
		

		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Object 1", false, true);
		
		for (int i=0; i<myNet.networkError.size()-1; i++) {
			series1.add(i, myNet.networkError.get(i));
		}
		dataset.addSeries(series1);
		
		
		JFreeChart chart = ChartFactory.createXYLineChart("Network_Error", "error", "Epoch", dataset, PlotOrientation.VERTICAL, true, true, false);		
//		JFreeChart chart = ChartFactory.createXYLineChart("Network_Error", "error", "Epoch", dataset, "horizontal", false, false, false);
		
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
	     
// test commit
	}
}
