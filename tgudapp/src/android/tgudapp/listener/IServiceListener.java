package android.tgudapp.listener;

import android.tgudapp.service.Service;
import android.tgudapp.service.ServiceResponse;

public interface IServiceListener {
	/*
     * Called when a request has been fisnished without canceled.
     */
	public void onCompleted(Service service, ServiceResponse response);
}
