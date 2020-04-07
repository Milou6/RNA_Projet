package RNApkg;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

@SuppressWarnings("serial")// j'aime pas les triangles jaune
public class LIMCouche extends AbstractListModel<Layer> {
	private ArrayList<Layer> couches = new ArrayList<>();
	
	@Override
	public Layer getElementAt(int index) {
		return couches.get(index);
	}

	@Override
	public int getSize() {
		return couches.size();
	}

	public void addLayer(Layer couche) {
		couches.add(couche);
		fireContentsChanged(this, 0, getSize());
	}
}
