package io.appez;

import io.appez.constants.ServiceConstants;
import io.appez.constants.SmartConstants;
import io.appez.exceptions.ExceptionTypes;
import io.appez.exceptions.MobiletException;
import io.appez.listeners.SmartServiceListener;
import io.appez.services.CameraService;
import io.appez.services.DatabaseService;
import io.appez.services.FileService;
import io.appez.services.HttpService;
import io.appez.services.LocationService;
import io.appez.services.MapService;
import io.appez.services.PersistenceService;
import io.appez.services.SignatureService;
import io.appez.services.SmartService;
import io.appez.services.UIService;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

/**
 * SmartServiceRouter.java: It is based on factory pattern. It maintains the
 * pool of Services, on new requests it checks the availability of request in
 * pool. If it is available then it uses the reference else it creates the
 * object of new services makes entry in Pool and then returns the object of
 * service.
 */
public class SmartServiceRouter {

	private HashMap<String, SmartService> servicesSet = null;
	private SmartServiceListener smartServiceListener = null;

	public SmartServiceRouter() {

	}

	public SmartServiceRouter(SmartServiceListener smartServiceListener) {
		this.servicesSet = new HashMap<String, SmartService>();
		this.smartServiceListener = smartServiceListener;
	}

	/**
	 * Returns instance of SmartService based on the service type
	 * 
	 * @param context
	 *            : Application context
	 * @param serviceType
	 *            : Type of service
	 * 
	 * @return SmartService
	 * */
	public SmartService getService(Context context, int serviceType) {
		SmartService smartService = null;
		String key = String.valueOf(serviceType);

		if (servicesSet.containsKey(key)) {
			smartService = servicesSet.get(key);
		} else {
			switch (serviceType) {
			case ServiceConstants.UI_SERVICE:
				smartService = new UIService(context, smartServiceListener);
				break;

			case ServiceConstants.HTTP_SERVICE:
				smartService = new HttpService(context, smartServiceListener);
				break;

			case ServiceConstants.DATA_PERSISTENCE_SERVICE:
				smartService = new PersistenceService(context, smartServiceListener);
				break;

			case ServiceConstants.DEVICE_DATABASE_SERVICE:
				smartService = new DatabaseService(context, smartServiceListener);
				break;

			case ServiceConstants.MAPS_SERVICE:
				smartService = new MapService(context, smartServiceListener);
				break;

			case ServiceConstants.FILE_SERVICE:
				smartService = new FileService(context, smartServiceListener);
				break;

			case ServiceConstants.CAMERA_SERVICE:
				smartService = new CameraService(context, smartServiceListener);
				break;

			case ServiceConstants.LOCATION_SERVICE:
				smartService = new LocationService(context, smartServiceListener);
				break;

			case ServiceConstants.SIGNATURE_SERVICE:
				Log.d(SmartConstants.APP_NAME, "SmartServiceRouter->SIGNATURE_SERVICE");
				smartService = new SignatureService(context, smartServiceListener);
				break;

			default:
				throw new MobiletException(ExceptionTypes.SERVICE_TYPE_NOT_SUPPORTED_EXCEPTION);
			}

			if (smartService != null) {
				servicesSet.put(key, smartService);
			}
		}
		return smartService;
	}

	/**
	 * Removes the mentioned service from services set
	 * 
	 * @param serviceType
	 *            : Type of service
	 * @param isEventCompleted
	 *            : Indicates whether or not the event is complete
	 */
	public void releaseService(int serviceType, boolean isEventCompleted) {
		SmartService smartService = null;
		String key = String.valueOf(serviceType);
		if (servicesSet.containsKey(key) && isEventCompleted) {
			smartService = servicesSet.get(key);
			smartService.shutDown();
			servicesSet.remove(key);
		}
	}
}
