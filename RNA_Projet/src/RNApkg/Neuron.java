// test commit
package RNApkg;

import java.util.Arrays;

import org.apache.commons.math3.linear.*;

/* Classe qui représente les neurones individuels du RNA.
 * S'étend aux sous-classes RegularNeuron et BiasNeuron.
 * 
 * _______________________________________________________________________________
 * Attributs:
 * 
 * String activation_function : Définit à la construction le type de fonction d'activation du neurone ("sigmoid", "tanh")
 * 
 * double activation : La valeur actuelle du neurone. Par défaut 0.0 pour les RegularNeuron, et 1.0 pour les BiasNeuron
 * 
 * ArrayList<Double> forwardWeights : Contient les poids partant du neurone actuel, vers tous les RegularNeuron de la Layer suivante
 */
public class Neuron {
	String activation_function;
	double activation;
	RealMatrix forwardWeights;
	
	//Constructeur
	public Neuron(String activation_function, double activation) {
		// TODO Auto-generated constructor stub
		this.activation_function = activation_function;
		this.activation = activation;
		this.forwardWeights = MatrixUtils.createRealMatrix(1, 1);
	}
	
	@Override
	public String toString() {
		if (this instanceof RegularNeuron) {
			return " RegularNeuron [activation_function : " + activation_function + ", activation : " + activation + ", forwardWeights : " + Arrays.deepToString(forwardWeights.getData()) + " ]";
		}
		else {
			return " BiasNeuron [activation_function : " + activation_function + ", activation : " + activation + ", forwardWeights : " + Arrays.deepToString(forwardWeights.getData()) + " ]";
		}
	}

}



/* Sous-classe de Neuron
 */
class RegularNeuron extends Neuron {
	
	public RegularNeuron() {
		super("sigmoid", 0.0);
		
	}
}


/* Sous-classe de Layer
 */
class BiasNeuron extends Neuron {
	
	public BiasNeuron() {
		super("sigmoid", 0.01);
		
	}
}