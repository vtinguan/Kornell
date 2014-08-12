package kornell;

import static org.easymock.EasyMock.*;
import junit.framework.TestCase;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.message.MessagePresenter;
import kornell.gui.client.presentation.message.MessageView;

import org.junit.Test;


public class MessageTest extends TestCase {

   @Test
   public void testSendMessage() {
/*
  	 final MessageView view = createMock(MessageView.class);
  	 final MessagePresenter presenter = createMock(MessagePresenter.class);

	   expect(presenter.getView()).andReturn(false);
     replay(view);

     verify(presenter);
     verify(view);*/
  	 
  	 /* final MeetingView view = createMock(MeetingView.class);
    final RoomScheduler scheduler = createMock(RoomScheduler.class);

     final Meeting meeting = new Meeting();
     final Presenter presenter = new Presenter(meeting, view, scheduler);

     // The schedule service will reply with no available capacity
     expect(scheduler.canAcceptCapacityFor(meeting)).andReturn(false);
     view.disableSaveButton();
     replay(scheduler);
     replay(view);
     presenter.requiredCapacityChanged(new FakeTextContainer("225"));

     verify(scheduler);
     verify(view);
 
   assertEquals("Should have updated the model's capacity", 225, meeting.getCapacity());*/
  }
}