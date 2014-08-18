package kornell.gui.client;
import static com.googlecode.gwt.test.assertions.GwtAssertions.assertThat;
import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.api.client.MessagesClient;
import kornell.core.entity.Person;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.presentation.message.compose.GenericMessageComposeView;
import kornell.gui.client.presentation.message.compose.MessageComposePresenter;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;
import com.googlecode.gwt.test.utils.events.Browser;

//@GwtModule("kornell.gui.Kornell")
public class KornellSampleTest {	/*extends GwtTestWithMockito  {


  @Mock
  private MessagesClient messagesClient;
  
  @Test
  public void clickOnButtonShouldDisplayMessageInLabel() {
   Kornell app;
    ClientFactory clientFactory;
    app = new Kornell();
    clientFactory = app.getClientFactory();
     // Arrange
		ViewFactory viewFactory = new GenericViewFactoryImpl(clientFactory);
		KornellSession session = new KornellSession(clientFactory.getEventBus());
		
		UserInfoTO user = clientFactory.getTOFactory().newUserInfoTO().as();
		Person person = clientFactory.getEntityFactory().newPerson().as();
		person.setUUID("9656f210-d812-44e8-8c12-d6ac1b7ed73a");
		user.setPerson(person);
		session.setCurrentUser(user);
		
		clientFactory.setKornellSession(session);
		
  		GenericMessageComposeView view = (GenericMessageComposeView) viewFactory.getMessageComposeView();
   		MessageComposePresenter presenter = new MessageComposePresenter(session, viewFactory, messagesClient, clientFactory.getEntityFactory());
  		view.setPresenter(presenter);
     view.textBox.setText("Ben Linus");
     view.button.setEnabled(true);
     assertThat(view.label).isNotVisible();



    // mock service failed invocation
    doFailureCallback(new RuntimeException("expected mocked runtime exception")).when(
    		messagesClient).create(Mockito.eq(view.getMessage()),
             Mockito.any(Callback.class));

    // Act
    Browser.click(view.button);

    // Assert
    Mockito.verify(messagesClient).create(view.getMessage(),
             Mockito.any(Callback.class));
    assertThat(view.label).isVisible().textEquals(
             "Server error: expected mocked runtime exception");
  }
  @Test
  public void fillTextShouldEnableButton() {
     // Arrange
     RpcSampleView view = new RpcSampleView();
     // ensure the widgets state at init
     assertThat(view.label).isNotVisible();
     assertThat(view.button).isVisible().isNotEnabled();

     // Act
     Browser.fillText(view.textBox, "John Locke");

     // Assert
     assertThat(view.button).isVisible().isEnabled();
     assertThat(view.label).isNotVisible();
  }*/

}
