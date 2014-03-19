package kornell.scorm.client.scorm12;

import static kornell.scorm.client.scorm12.Scorm12.logger;
import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.entity.ActomEntries;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Adapter implements CMIConstants, ActomEnteredEventHandler {

	private String lastError = NoError;

	private String currentEnrollmentUUID;
	private String currentActomKey;

	private CMIDataModel dataModel;

	private KornellClient client;

	private EventBus bus;

	public SCORM12Adapter(EventBus bus, KornellClient client) {
		this.client = client;
		this.bus = bus;
		bus.addHandler(ActomEnteredEvent.TYPE, this);
	}

	public String LMSInitialize(String param) {
		String result = TRUE;
		if (dataModel == null) {
			logger.warning("Can not initialize API Adapter without a Data Model");
			// TODO: Wait for data model
			result = FALSE;
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
		String result = null;
		if (isCMIElement(param))
			result = dataModel.getValue(param);
		logger.finer("LMSGetValue[" + param + "] = " + result);
		return result;
	}

	public String LMSCommit(String param) {
		String result = TRUE;
		onDirtyData(null);
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
		if (isCMIElement(key))
			result = dataModel.setValue(key, value);
		GWT.log("LMSSetValue [" + key + " = " + value + "] = " + result);
		return result;
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
		if (dataModel != null)
			putDataModel(new Callback<ActomEntries>() {
				@Override
				public void ok(ActomEntries to) {
					logger.finest("Saved data model for [enrollment:"+to.getEnrollmentUUID()+",actomKey:"+to.getActomKey()+"] with ["+to.getEntries().size()+"] entries");

				}
			});
		this.currentActomKey = event.getActomKey();
		this.currentEnrollmentUUID = event.getEnrollmentUUID();
		dataModel = null;
		getDataModel();
	}

	private void getDataModel() {
		client.enrollment(currentEnrollmentUUID).actom(currentActomKey)
				.get(new Callback<ActomEntries>() {
					@Override
					public void ok(ActomEntries to) {
						dataModel = new CMIDataModel(SCORM12Adapter.this, bus,
								to.getEntries());
						logger.finest("Loaded data model for [enrollment:"+to.getEnrollmentUUID()+",actomKey:"+to.getActomKey()+"] with ["+to.getEntries().size()+"] entries");
					}
				});
	}

	private void putDataModel(Callback<ActomEntries> callback) {
		if (dataModel != null)
			client.enrollment(currentEnrollmentUUID).actom(currentActomKey)
					.put(dataModel.getValues(), callback);
	}

	public void onDirtyData(final Callback<ActomEntries> callback) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				putDataModel(callback);
			}
		});
	}

}
