package kornell.scorm.client.scorm12;

import java.util.Map;
import java.util.logging.Logger;

import kornell.core.entity.ActomEntries;
import kornell.core.entity.EnrollmentEntries;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Runtime implements ActomEnteredEventHandler {
	private static final Logger logger = Logger.getLogger(SCORM12Runtime.class.getName());
	private static SCORM12Runtime instance;
	private EnrollmentEntries entries;

	SCORM12Adapter currentAPI = null;
	
	private SCORM12Runtime(EventBus bus, EnrollmentEntries entries){
		this.entries = entries;
		bus.addHandler(ActomEnteredEvent.TYPE, this);
	}
	
	public static synchronized SCORM12Runtime launch(EventBus bus,EnrollmentEntries entries){		
		instance = new SCORM12Runtime(bus,entries);
		return instance;
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		logger.info("Loading [enrollmentUUID:"+event.getEnrollmentUUID()+"][actomKey:"+event.getActomKey()+"]");
		if (currentAPI != null) currentAPI.runtimeSync();
		bindNewAdapter(event.getEnrollmentUUID(),event.getActomKey());
	}

	private void bindNewAdapter(String enrollmentUUID, String actomKey) {
		Map<String, Map<String, ActomEntries>> moduleEntries = entries.getModuleEntries();
		Map<String, ActomEntries> moduleMap = moduleEntries.get(enrollmentUUID);
		ActomEntries entries = moduleMap.get(actomKey);
		SCORM12Adapter apiAdapter = SCORM12Adapter.create(enrollmentUUID,actomKey,entries);
		SCORM12Binder.bindToWindow(apiAdapter);
	}
}
