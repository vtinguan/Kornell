package kornell.scorm.client.scorm12;

import com.google.web.bindery.event.shared.EventBus;

import kornell.gui.client.event.LogoutEvent;

public class CMICoreModel extends DataModel implements CMIConstants {
	static {
		register(new CMIDataElement("cmi.core.exit", null, new Action() {
			@Override
			public void execute(EventBus bus) {
				bus.fireEvent(new LogoutEvent());
			}
		}));

		register(new CMIDataElement("cmi.core.lesson_status", NOT_ATTEMPTED));
	}

}
