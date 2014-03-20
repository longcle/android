package android.tgudapp.listener;

import java.util.List;

import android.tgudapp.model.Place;

public interface IServerConnector {
	public List<Place> getPlaceByCategory();
}
