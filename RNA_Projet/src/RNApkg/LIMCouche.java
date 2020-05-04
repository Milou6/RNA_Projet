package RNApkg;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

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
	
	public void removeLayer(int index) {
		couches.remove(index);
		fireContentsChanged(this, 0, getSize());
	}

	public int indexOf(Layer Layer) {
		
		return couches.indexOf(Layer);
	}
	
	@Override
	public ListDataListener[] getListDataListeners() {
		return super.getListDataListeners();
	}
}