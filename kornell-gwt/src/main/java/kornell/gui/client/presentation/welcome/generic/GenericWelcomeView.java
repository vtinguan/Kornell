package kornell.gui.client.presentation.welcome.generic;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.gui.client.presentation.activity.AtividadePlace;
import kornell.gui.client.presentation.welcome.WelcomeView;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

public class GenericWelcomeView extends Composite implements WelcomeView {
	interface MyUiBinder extends UiBinder<Widget, GenericWelcomeView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField()
	FlowPanel pnlActivities;

	private KornellClient client;

	private PlaceController placeCtrl;

	public GenericWelcomeView(KornellClient client, PlaceController placeCtrl) {
		this.client = client;
		this.placeCtrl = placeCtrl;
		initWidget(uiBinder.createAndBindUi(this));
		initData();
	}

	private void initData() {
		client.getCourses(new Callback() {
			@Override
			protected void ok(JSONValue json) {
				JSONArray arr = json.isArray();
				if (arr != null)
					display(arr);
			}

		});
	}

	//TODO: Refactor to use "activity manager (todo)"
	private void display(JSONArray arr) {
		for (int i = 0; i < arr.size(); i++) {
			final String url = arr.get(i).isString().stringValue();
			final FlowPanel pnlCurso = new FlowPanel();
			pnlCurso.addStyleName("pnlActivity");
			//pnlCurso.add(new Label(url));
			RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, url+"imsmanifest.xml");			
			try{
			reqBuilder.sendRequest(null, new Callback(){
				@Override
				protected void ok(Response response) {					
					String text = response.getText();
				    Document dom = XMLParser.parse(text);
				    Node node = dom.getElementsByTagName("title").item(0).getFirstChild();
				    final String title = ((Text)node).getData();
				    Heading hTitle = new Heading(1);
				    hTitle.setText(title);
				    pnlCurso.add(hTitle);
				    pnlCurso.sinkEvents(Event.ONCLICK);
				    pnlCurso.addHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							placeCtrl.goTo(new AtividadePlace(url));
						}
					}, ClickEvent.getType());
				    
					GWT.log(node.getClass().getName());
				}
			});}catch(RequestException e){
				GWT.log(e.getMessage(),e);
			}
			pnlActivities.add(pnlCurso);
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
	}

}
