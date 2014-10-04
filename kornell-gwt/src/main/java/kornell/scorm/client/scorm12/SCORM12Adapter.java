package kornell.scorm.client.scorm12;

import static kornell.scorm.client.scorm12.Scorm12.logger;

import java.util.Map;
import java.util.logging.Logger;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.entity.ActomEntries;
import kornell.core.scorm.scorm12.rte.action.OpenSCO12Action;
import kornell.gui.client.sequence.NavigationRequest;

import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Adapter implements CMIConstants,NavigationRequest.Handler {
	private static final Logger log = Logger.getLogger(SCORM12Adapter.class
			.getName());
	/**
	 * How much time a client can stay unsynced with the server after setting a
	 * data model value, in milliseconds.
	 */
	private static final int DIRTY_TOLERANCE = 2000;
	private Timer dirtyTimer;
	private String lastError = NoError;

	private CMITree dataModel;

	private KornellClient client;

	private EventBus bus;

	private String actomKey;
	private String enrollmentUUID;

	public SCORM12Adapter(EventBus bus, KornellClient client, String enrollmentUUID, OpenSCO12Action openSCO) {
		this.client = client;
		this.bus = bus;
		this.actomKey = openSCO.getResourceId();
		this.enrollmentUUID=enrollmentUUID;
		Map<String, String> data = openSCO.getData();
		dataModel = CMITree.create(data);
		log.info("Initializing SCORM 1.2 API with [" + data.size()
				+ "] entries");
		bus.addHandler(NavigationRequest.TYPE, this);
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
		flush();
		logger.finer("LMSFinish[" + param + "]");
		return result;
	}

	public String LMSGetLastError() {
		logger.finer("LMSGetLastError[]");
		return lastError;
	}

	public String LMSGetValue(String param) {
		String result = null;
		if (isCMIElement(param))
			result = dataModel.getValue(param);
		logger.finer("LMSGetValue[" + param + "] = " + result);
		return result;
	}

	public String LMSCommit(String param) {
		String result = TRUE;
		flush();
		logger.finer("LMSCommit[" + param + "] = " + result);
		return result;
	}


	private boolean isCMIElement(String param) {
		return param != null && param.startsWith("cmi");
	}

	public String LMSSetDouble(String key, Double value) {
		String strValue = Double.toString(value);
		return LMSSetString(key, strValue);
	}

	public String LMSSetString(String key, String value) {
		String result = FALSE;
		if (isCMIElement(key)) {
			result = dataModel.setValue(key, value);
			scheduleSync();
		}
		logger.finer("LMSSetValue [" + key + " = " + value + "] = " + result);
		return result;
	}

	private void scheduleSync() {
		dirtyTimer = new Timer() {
			public void run() {
				syncAfterSet();
			}

			private void syncAfterSet() {
				flush();
			}
		};
		dirtyTimer.schedule(DIRTY_TOLERANCE);
	}

	private void flush() {
		class Scrub extends Callback<ActomEntries> {
			@Override
			public void ok(ActomEntries to) {
				// TODO: Scrub only verified puts
				dataModel.scrub();
			}
		}

		if (dataModel != null && dataModel.isDirty()) {
			Map<String, String> dirty = CMITree.collectDirty(dataModel);
			logger.info("Syncing [" + dirty.size() + "] entries");
			for (Map.Entry<String, String> entry : dirty.entrySet()) {
				logger.fine(entry.getKey() + "=" + entry.getValue());
			}
			
			client.enrollment(enrollmentUUID).actom(actomKey).put(dirty, new Scrub());
		}
	}


	// TODO: Fire an event
	// TODO: Find better place for this code
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

	@Override
	public void onContinue(NavigationRequest event) {
		suicide();
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		suicide();
	}

	@Override
	public void onDirect(NavigationRequest event) {
		suicide();
	}

	private void suicide() {
		flush();
		if (dirtyTimer != null) dirtyTimer.cancel();
		else log.warning("Re-stopping API adapter requested.");
		dirtyTimer = null;
	}

	



}
