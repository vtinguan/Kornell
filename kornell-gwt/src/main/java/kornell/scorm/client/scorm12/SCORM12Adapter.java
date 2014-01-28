package kornell.scorm.client.scorm12;

import java.util.logging.Logger;

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
	Logger logger = Logger.getLogger(SCORM12Adapter.class.getName());

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
		GWT.log("LMSInitialize[" + param + "]");
		if (dataModel == null) {
			logger.warning("Can not initialize API Adapter without a Data Model");
			//TODO: Wait for data model
			return FALSE;
		}
		return TRUE;
	}

	public String LMSFinish(String param) {
		GWT.log("LMSFinish[" + param + "]");
		return TRUE;
	}

	public String LMSGetLastError() {
		GWT.log("LMSGetLastError[]");
		return lastError;
	}

	public String LMSGetValue(String param) {
		String result = null;
		if (isCMIElement(param))
			result = dataModel.getValue(param);
		GWT.log("LMSGetValue[" + param + "] = " + result);
		return result;
	}

	private boolean isCMIElement(String param) {
		return param != null && param.startsWith("cmi");
	}

	public String LMSSetValue(String param, String value) {
		String result = FALSE;
		if (isCMIElement(param))
			result = dataModel.setValue(param, value);
		GWT.log("LMSSetValue[" + param + "," + value + "] = " + result);
		return result;
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		GWT.log("ACTOM ENTERED");
		// TODO: Make reliable, offlineable, what not-able...
		if (dataModel != null)
			putDataModel(new Callback<ActomEntries>() {
				@Override
				public void ok(ActomEntries to) {
					GWT.log("Data model put without action");
					
				}
			});
		this.currentActomKey = event.getActomKey();
		this.currentEnrollmentUUID = event.getEnrollmentUUID();
		dataModel = null;
		getDataModel();
	}

	private void getDataModel() {
		client.enrollment(currentEnrollmentUUID)
			  .actom(currentActomKey)
			  .get(new Callback<ActomEntries>() {
				@Override
				public void ok(ActomEntries to) {
					GWT.log("Populating new data model");
					dataModel = new CMIDataModel(SCORM12Adapter.this,bus, to.getEntries());
				}
			  });
	}

	private void putDataModel(Callback<ActomEntries> callback) {
		client.enrollment(currentEnrollmentUUID).actom(currentActomKey)
				.put(dataModel.getValues(),callback);
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
