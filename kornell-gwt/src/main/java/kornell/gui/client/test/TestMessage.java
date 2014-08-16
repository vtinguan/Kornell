package kornell.gui.client.test;

import kornell.api.client.KornellSession;
import kornell.api.client.MessagesClient;
import kornell.core.entity.Person;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.GenericViewFactoryImpl;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.message.compose.MessageComposePresenter;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class TestMessage extends GWTTestCase{

	
	@Override
	public String getModuleName() {
		return "kornell.gui.Kornell";
	}

	@Test
	public void testCreate() {
		ClientFactory clientFactory = new GenericClientFactoryImpl();
		ViewFactory viewFactory = new GenericViewFactoryImpl(clientFactory);
		KornellSession session = new KornellSession(null);
		
		UserInfoTO user = clientFactory.getTOFactory().newUserInfoTO().as();
		Person person = clientFactory.getEntityFactory().newPerson().as();
		person.setUUID("9656f210-d812-44e8-8c12-d6ac1b7ed73a");
		user.setPerson(person);
		session.setCurrentUser(user);
		
		clientFactory.setKornellSession(session);
		MessagesClient messagesClient = session.messages();
		messagesClient.setApiUrl("http://localhost:8888/api");
		
		MessageComposePresenter presenter = new MessageComposePresenter(session, viewFactory, messagesClient);
		presenter.okButtonClicked();
		//presenter
    assertTrue(presenter != null);
	}

}
