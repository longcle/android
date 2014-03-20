package android.tgudapp.model;

import java.io.Serializable;
import java.util.List;

public class ListPlace implements Serializable {
	public static final String LISTPLACE = "listplace";
	private List<Place> listPlace;

	public List<Place> getListPlace() {
		return listPlace;
	}

	public void setListPlace(List<Place> listPlace) {
		this.listPlace = listPlace;
	}

	public ListPlace(List<Place> listPlace) {
		super();
		this.listPlace = listPlace;
	}
	
}
