package kornell.scorm.client.scorm12;

import java.util.logging.Logger;

import kornell.api.client.KornellClient;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Adapter implements CMIConstants, ActomEnteredEventHandler {
	Logger logger = Logger.getLogger(SCORM12Adapter.class.getName());

	private String lastError = "0";

	private String currentEnrollmentUUID;
	private String currentActomKey;

	private CMIDataModel dataModel;

	private KornellClient client;

	public SCORM12Adapter(EventBus bus, KornellClient client) {
		this.client = client;
		bus.addHandler(ActomEnteredEvent.TYPE, this);
	}

	public String LMSInitialize(String param) {
		GWT.log("LMSInitialize[" + param + "]");
		if (dataModel == null) {
			logger.warning("Can not initialize API Adapter without a Data Model");
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
			syncDataModel();

		this.currentActomKey = event.getActomKey();
		this.currentEnrollmentUUID = event.getEnrollmentUUID();
		dataModel = new CMIDataModel(this);
	}

	private void syncDataModel() {
		client.enrollment(currentEnrollmentUUID)
			  .actom(currentActomKey)
			  .sync(dataModel.getValues());
	}
	
	public void onDirtyData(){
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {			
			@Override
			public void execute() {
				syncDataModel();
			}
		});		
	}

}
