package kornell.scorm.client.scorm12;

import java.util.Map;
import java.util.logging.Logger;

import kornell.api.client.KornellSession;
import kornell.core.entity.ActomEntries;
import kornell.core.entity.EnrollmentEntries;
import kornell.core.entity.EnrollmentsEntries;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Runtime implements ActomEnteredEventHandler {
	private static final Logger logger = Logger.getLogger(SCORM12Runtime.class.getName());
	private static SCORM12Runtime instance;
	private EnrollmentsEntries entries;

	private SCORM12Adapter currentAPI = null;
	private KornellSession session;
	private PlaceController placeCtrl;
	
	private SCORM12Runtime(EventBus bus, KornellSession session, PlaceController placeCtrl, EnrollmentsEntries entries){
		this.entries = entries;
		this.session = session;
		this.placeCtrl = placeCtrl;
		bus.addHandler(ActomEnteredEvent.TYPE, this);
	}
	
	public static synchronized SCORM12Runtime launch(EventBus bus, KornellSession session, PlaceController placeCtrl,EnrollmentsEntries entries){		
		instance = new SCORM12Runtime(bus, session, placeCtrl, entries);
		return instance;
	}

	@Override
	public void onActomEntered(ActomEnteredEvent event) {
		logger.info("Loading [enrollmentUUID:"+event.getEnrollmentUUID()+"][actomKey:"+event.getActomKey()+"]");
		if (currentAPI != null) currentAPI.runtimeSync();
		bindNewAdapter(event.getEnrollmentUUID(),event.getActomKey());
	}

	private void bindNewAdapter(String enrollmentUUID, String actomKey) {
		Map<String, EnrollmentEntries> enrollmentEntriesMap = entries.getEnrollmentEntriesMap();
		EnrollmentEntries enrollmentEntries = enrollmentEntriesMap.get(enrollmentUUID);
		Map<String, ActomEntries> actomEntriesMap = enrollmentEntries.getActomEntriesMap();
		ActomEntries actomEntries = actomEntriesMap.get(actomKey);
		SCORM12Adapter apiAdapter = SCORM12Adapter.create(session,placeCtrl,enrollmentUUID,actomKey,actomEntries);
		SCORM12Binder.bindToWindow(apiAdapter);
	}
}
