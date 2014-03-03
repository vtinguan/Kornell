package kornell.scorm.client.scorm12;

import kornell.api.client.KornellClient;
import kornell.core.lom.Contents;
import kornell.gui.client.presentation.course.ClassroomPlace;
import kornell.gui.client.sequence.NavigationRequest;
import kornell.gui.client.sequence.Sequencer;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

public class SCORM12Sequencer implements Sequencer{
	
	private ClassroomPlace place;
	private KornellClient client;

	public SCORM12Sequencer(EventBus bus, KornellClient client) {
		this.client = client;
		bus.addHandler(NavigationRequest.TYPE, this);
	}

	@Override
	public void onContinue(NavigationRequest event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPrevious(NavigationRequest event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDirect(NavigationRequest event) {
		// TODO Auto-generated method stub
	}

	@Override
	public Sequencer withPanel(FlowPanel contentPanel) {
		IFrameElement iframe = Document.get().createIFrameElement();
		iframe.setSrc("http://eduvem.com/repository/fc68f492-8646-4ec1-8ca7-a5a8e9c63dee/SingleLocalSCOEx/sco06.htm");
		return null;
	}

	@Override
	public Sequencer withPlace(ClassroomPlace place) {
		this.place = place;
		return this;
	}

	@Override
	public void go(Contents contents) {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

}
