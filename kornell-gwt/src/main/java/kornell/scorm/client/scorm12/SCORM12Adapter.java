package kornell.scorm.client.scorm12;

import static kornell.scorm.client.scorm12.Scorm12.logger;
import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.entity.ActomEntries;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;
import kornell.gui.client.presentation.course.ClassroomPlace;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Adapter implements CMIConstants, ActomEnteredEventHandler {

	/**
	 * How much time a client can stay unsynced with the server after setting a
	 * data model value, in milliseconds.
	 */
	private static final int DIRTY_TOLERANCE = 2000;

	private String lastError = NoError;

	private String currentEnrollmentUUID;
	private String currentActomKey;

	private CMITree dataModel = new CMINode();

	private KornellClient client;

	private EventBus bus;

	private PlaceController placeCtrl;

	public SCORM12Adapter(EventBus bus, KornellClient client, PlaceController placeCtrl) {
		logger.info("SCORM API 1.2.2015_04_23_15_48");
		this.client = client;
		this.bus = bus;
		this.placeCtrl = placeCtrl;
		bus.addHandler(ActomEnteredEvent.TYPE, this);
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

	public String LMSGetValue(String param) {
		String result = dataModel.getValue(param);
		logger.finer("LMSGetValue[" + param + "] = " + result);
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
		String strValue = Double.toString(value);
		return LMSSetString(key, strValue);
	}

	public String LMSSetString(String key, String value) {
		String result = FALSE;
		result = dataModel.setValue(key, value);
		scheduleSync();
		logger.finer("LMSSetValue [" + key + " = " + value + "] = " + result);
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

	private void sync() {
		class Scrub extends Callback<ActomEntries> {
			@Override
			public void ok(ActomEntries to) {
				// TODO: Scrub only verified
				dataModel.scrub();
			}
		}

		if (dataModel != null && dataModel.isDirty()) {
			client.enrollment(currentEnrollmentUUID).actom(currentActomKey)
					.put(CMITree.collectDirty(dataModel), new Scrub());
		}
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		logger.finer("ActomEntered [" + event.getActomKey() + "]");
		refreshDataModel(event);
	}

	// TODO: Fire an event
	public static native void stopAllVideos() /*-{
		var frms = $wnd.parent.document.getElementsByTagName("IFRAME")
		if (console) {
			console.debug("Stopping videos in # iframes: " + frms.length);
		}
		for (var i = 0; i < frms.length; i++) {
			var frm = frms[i];
			var stopThem = frm.contentWindow.stopAllVideos;
			if (stopThem)
				stopThem();
		}
	}-*/;

	private void refreshDataModel(ActomEnteredEvent event) {
		syncBeforeLoadinNewActom();
		this.currentActomKey = event.getActomKey();
		this.currentEnrollmentUUID = event.getEnrollmentUUID();
		loadDataModel();
	}

	private void syncBeforeLoadinNewActom() {
		sync();
	}

	private void loadDataModel() {
		client.enrollment(currentEnrollmentUUID).actom(currentActomKey)
				.get(new Callback<ActomEntries>() {
					@Override
					public void ok(ActomEntries to) {
						dataModel = CMITree.create(to.getEntries());
						logger.finest("Loaded data model for [enrollment:"
								+ to.getEnrollmentUUID() + ",actomKey:"
								+ to.getActomKey() + "] with ["
								+ to.getEntries().size() + "] entries");
					}
				});
	}
	
	

}
