package kornell;

import org.junit.Test;

import junit.framework.TestCase;


public class MessageTest extends TestCase {

   @Test
   public void testSendMessage() {
  	 
  	 /*
  	 final MessageComposeView view = createMock(MessageComposeView.class);
  	 final MessageComposePresenter presenter = createMock(MessageComposePresenter.class);

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