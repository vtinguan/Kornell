package kornell.gui.client.presentation.message.compose;

import java.util.Date;
import java.util.List;

import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.Message;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentTO;
import kornell.gui.client.presentation.admin.home.generic.GenericCourseClassReportsView;
import kornell.gui.client.presentation.util.FormHelper;
import kornell.gui.client.uidget.KornellPagination;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class GenericMessageComposeView extends Composite implements MessageComposeView {

	interface MyUiBinder extends UiBinder<Widget, GenericMessageComposeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private KornellSession session;
	private EventBus bus;
	private EntityFactory entityFactory;
	private MessageComposeView.Presenter presenter;


	Tab adminsTab;
	FlowPanel adminsPanel;
	private String viewType;

	// TODO i18n xml
	public GenericMessageComposeView(final KornellSession session, EventBus bus, EntityFactory entityFactory) {
		this.session = session;
		this.bus = bus;
		this.entityFactory = entityFactory;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
	public Message getMessage(){
		Message message = entityFactory.newMessage().as();
		message.setSubject("");
		message.setBody("");
		message.setSenderUUID(session.getCurrentUser().getPerson().getUUID());
		message.setParentMessageUUID(null);
		message.setSentAt(new Date());
		message.setUUID(null);
		return message;
	}

	@Override
	public void setPresenter(Presenter p) {
		presenter = p;
		// TODO Auto-generated method stub
	}
}