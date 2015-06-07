package kornell.scorm.client.scorm12;

import static kornell.scorm.client.scorm12.Scorm12.logger;
import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.entity.ActomEntries;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;
import kornell.gui.client.presentation.course.ClassroomPlace;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Adapter implements CMIConstants  {

	/**
	 * How much time a client can stay unsynced with the server after setting a
	 * data model value, in milliseconds.
	 */
	private static final int DIRTY_TOLERANCE = 2000;
	
	public static SCORM12Adapter create(String enrollmentUUID, String actomKey,
			ActomEntries entries) {
		return new SCORM12Adapter(enrollmentUUID,actomKey,entries);
	}
	
	//Scope Parameters
	private String enrollmentUUID;
	private String actomKey;
	
	//State
	private CMITree dataModel = new CMINode();
	private String lastError = NoError;

	//UI/Client
	private KornellClient client;
	private EventBus bus;
	private PlaceController placeCtrl;

	
	public SCORM12Adapter(String enrollmentUUID, String actomKey,ActomEntries ae) {
		logger.info("SCORM API 1.2.2015_05_07_20_00");
		this.enrollmentUUID = enrollmentUUID;
		this.actomKey = actomKey;
		this.dataModel = CMITree.create(ae.getEntries());
		// EventBus bus, KornellClient client, PlaceController placeCtrl
	}

	
	public String LMSInitialize(String param) {
		String result = TRUE;
		if (dataModel == null) {
			logger.warning("LMS initialized without a data model ready.");
		}
		logger.finer("LMSInitialize[" + param + "] = " + result);
		return result;
	}

	public String LMSFinish(String param) {
		String result = TRUE;
		logger.finer("LMSFinish[" + param + "]");
		return result;
	}

	public String LMSGetLastError() {
		logger.finer("LMSGetLastError[]");
		return lastError;
	}

	public String LMSGetValue(String param,String moduleUUID) {
		String result = dataModel.getValue(param);
		logger.finer("LMSGetValue["+moduleUUID+"][" + param + "] = " + result);
		return result;
	}

	public String LMSCommit(String param) {
		String result = TRUE;
		syncOnLMSCommit();
		logger.finer("LMSCommit[" + param + "] = " + result);
		return result;
	}

	private void syncOnLMSCommit() {
		sync();
	}

	public String LMSSetDouble(String key, Double value) {
		return LMSSetDouble(null,key,value);
	}
	
	public String LMSSetDouble(String moduleUUID, String key, Double value) {
		String strValue = Double.toString(value);
		return LMSSetString(moduleUUID,key, strValue);
	}

	public String LMSSetString(String key, String value) {
		return LMSSetString(null,key,value); 
	}
	
	public String LMSSetString(String moduleUUID, String key, String value) {
		String result = FALSE;
		result = dataModel.setValue(key, value);
		scheduleSync();
		logger.finer("LMSSetValue ["+moduleUUID+"][" + key + " = " + value + "] = " + result);
		return result;
	}
	
	public void launch(String enrollmentUUID){
		placeCtrl.goTo(new ClassroomPlace(enrollmentUUID));
	}

	private void scheduleSync() {
		(new Timer() {
			public void run() {
				syncAfterSet();
			}

			private void syncAfterSet() {
				sync();
			}
		}).schedule(DIRTY_TOLERANCE);
	}

	public void runtimeSync(){
		sync();
	}
	
	private void sync() {
		class Scrub extends Callback<ActomEntries> {
			@Override
			public void ok(ActomEntries to) {
				// TODO: Scrub only verified
				dataModel.scrub();
			}
		}

		if (dataModel != null && dataModel.isDirty()) {
			client.enrollment(enrollmentUUID).actom(actomKey)
					.put(CMITree.collectDirty(dataModel), new Scrub());
		}
	}
/*
	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		logger.finer("ActomEntered [" + event.getActomKey() + "]");
		refreshDataModel(event);
	}


	private void refreshDataModel(ActomEnteredEvent event) {
		syncBeforeLoadinNewActom();
		this.currentActomKey = event.getActomKey();
		this.currentEnrollmentUUID = event.getEnrollmentUUID();
		loadDataModel();
	}

	private void syncBeforeLoadinNewActom() {
		sync();
	}
*/	
	

}
