package RNApkg;

import java.util.ArrayList;
import org.apache.commons.math3.linear.*;

/* Classe qui repr�sente une "couche" du RNA.
 * S'�tend aux sous-classes InputLayer, HiddenLayer et OutputLayer.
 * 
 * Un objet Layer garde un ArrayList contenant des objets Neuron.
 * 
 * 
 * _______________________________________________________________________________
 * Attributs:
 * 
 * final Net parent : R�f�rence � l'objet Net qui contient la Layer
 * 
 * String activation_function : choix de fonction d'activation pour les neurones
 * de cette Layer ( "sigmoid" pour l'instant )
 * 
 * ArrayList<Neuron> neurons: ArrayList qui store les objets Neuron de la Layer
 * 
 * int layerSize : Taille de la Layer, en nombre de neurones (inclue les BiasNeuron)
 * 
 * boolean hasBiasNeuron : true si la Layer contient un BiasNeuron
 * 
 */
public class Layer {
	final Net parent;
	String activation_function;
	ArrayList<Neuron> neurons;
	int layerSize;
	boolean hasBiasNeuron;
	
	//Constructeur
	public Layer(Net parent, String activation) {
		this.neurons = new ArrayList<Neuron>();
		this.activation_function = activation;
		this.layerSize = 0;
		this.parent = parent;
		this.hasBiasNeuron = false;
	}
	
	
//	@Override
//	public String toString() {
//		return "Layer [parent=" + parent + ", activation_function=" + activation_function + ", neurons=" + neurons
//				+ ", layerSize=" + layerSize + ", hasBiasNeuron=" + hasBiasNeuron + "]";
//	}
	
	@Override
	public String toString() {
		return "Layer : " + this.getActivation() + "/ Nbr Neurone : " + this.getlayerSize() + "/ Neurone de Biais : " + getHasBiasNeuron();
	}
	
	public String getActivation() {
		return this.activation_function;
	}
	
	public int getlayerSize() {
		return this.layerSize;
	}
	
	public Net getParent() {
		return this.parent;
	}
	
	public boolean getHasBiasNeuron() {
		return this.hasBiasNeuron;	
	}
	
	

	/* Applique la fonction sigmoid sur chaque entr�e d'un matrice 1D (vecteur) d'activations.
	 */
	public static void sigmoid(RealMatrix x) {
		for (int row=0; row<x.getRowDimension(); row++) {
			x.setEntry(row, 0, (1/( 1 + Math.pow(Math.E,(-1*x.getEntry(row, 0))))) );
		}
	}
	

	public static void relu(RealMatrix x) {
		for (int row=0; row<x.getRowDimension(); row++) {
			x.setEntry( row, 0, (Math.max(0, x.getEntry(row, 0))) );
		}
	}
	
	
	public static void lrelu(RealMatrix x) {
		for (int row=0; row<x.getRowDimension(); row++) {			
			if (x.getEntry(row, 0) < 0) {
				x.setEntry( row, 0, (0.01 * x.getEntry(row, 0)) );
			}
		}
	}
	

	/* Rajoute un objet Neuron (RegularNeuron ou BiasNeuron) � la Layer
	 * ________________________________________________________________
	 * Param�tres :
	 * 
	 * String neuron_type : "regular" ou "bias"
	 */
	public void addNeuron(String neuron_type) {
		Neuron new_neuron = new Neuron();
		
		switch (neuron_type) {
			case "regular":
				new_neuron = new RegularNeuron(this.activation_function, 0);
				this.neurons.add(new_neuron);
				break;
			case "bias":
				new_neuron = new BiasNeuron(this.activation_function, 0);
				this.neurons.add(new_neuron);
				break;
		}
		//Met � jour la taille de la Layer
		this.layerSize += 1;
	}


	/* M�thode qui effectue la forward-propagation.
	 * Diff�rent corps de m�thode selon la sous-classe de Layer.
	 * 
	 */
	public void forwardPropagate(double[][] x_test) {
	}
	
}


/* Sous-classe de Layer
 */
class InputLayer extends Layer {

	public InputLayer(Net parent, String activation_function) {
		super(parent, activation_function);
	}
	

	@Override
	public void forwardPropagate(double[][] x_test) {
		ArrayList<RealMatrix> activations = parent.netDataBase.activations;
		
		//A chaque it�ration d'entra�nement, on met � jour dans la DataBase les activations d'Input
		for (int n=0; n<layerSize; n++) {
			Neuron current_neuron = this.neurons.get(n);
			activations.get(0).setEntry(n, 0, current_neuron.activation);
		}
	}
	
}


/* Sous-classe de Layer
 */
class HiddenLayer extends Layer {

	public HiddenLayer(Net parent, String activation_function) {
		super(parent, activation_function);
	}
	
	@Override
	public void forwardPropagate(double[][] x_test) {	
		DataBase dataBase = parent.netDataBase;
		int index = parent.lcCouches.indexOf(this);
		
		RealMatrix current_layer_activation = dataBase.activations.get(index);
		RealMatrix previous_layer_activation = dataBase.activations.get(index-1).copy();
		//On multiplie la matrice des poids par la matrice des activations
		previous_layer_activation = dataBase.weights.get(index-1).multiply(previous_layer_activation);
		
		//Avant d'appliquer sigmoid, on sauve la valeur pour chaque neurone z = [w * x + b] dans weightedInputs
		RealMatrix weighted_inputs = new Array2DRowRealMatrix(previous_layer_activation.getData());
		dataBase.weightedInputs.set(index, weighted_inputs);
		
		//On applique la fonction d'activation de la Layer à la matrice obtenue
		switch (activation_function) {
			case "sigmoid":
				sigmoid(previous_layer_activation);
				break;
			case "relu":
				relu(previous_layer_activation);
				break;
			case "lrelu":
				lrelu(previous_layer_activation);
				break;
		}
		
		current_layer_activation.setSubMatrix(previous_layer_activation.getData(), 0, 0);
		dataBase.activations.set(index, current_layer_activation);
//		System.out.println(current_layer_activation);
		
		//Mets � jour le RNA avec la nouvelle matrice d'activation du DataBase.
		this.parent.netDataBase.sendActivationsToNeurons();		
	}
}


/* Sous-classe de Layer
 */
class OutputLayer extends Layer {

	public OutputLayer(Net parent, String activation_function) {
		super(parent, activation_function);
	}
	
	@Override
	public void forwardPropagate(double[][] x_test) {
		DataBase dataBase = parent.netDataBase;
		int index = parent.lcCouches.indexOf(this);
		
		RealMatrix current_layer_activation = dataBase.activations.get(index);
		RealMatrix previous_layer_activation = dataBase.activations.get(index-1).copy();
		//On multiplie la matrice des poids par la matrice des activations
		previous_layer_activation = dataBase.weights.get(index-1).multiply(previous_layer_activation);
		
		//Avant d'appliquer sigmoid, on sauve la valeur pour chaque neurone z = [w * x + b] dans weightedInputs
		RealMatrix weighted_inputs = new Array2DRowRealMatrix(previous_layer_activation.getData());
		dataBase.weightedInputs.set(index, weighted_inputs);
		
		//On applique la fonction d'activation de la Layer � la matrice obtenue
		switch (activation_function) {
			case "sigmoid":
				sigmoid(previous_layer_activation);
				break;
			case "relu":
				relu(previous_layer_activation);
				break;
			case "lrelu":
				lrelu(previous_layer_activation);
				break;
		}
		
		current_layer_activation.setSubMatrix(previous_layer_activation.getData(), 0, 0);
		dataBase.activations.set(index, current_layer_activation);
//		System.out.println(current_layer_activation);
		
		//Mets � jour le RNA avec la nouvelle matrice d'activation du DataBase.
		this.parent.netDataBase.sendActivationsToNeurons();
	}

}