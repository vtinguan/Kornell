package kornell.gui.client.presentation.activity;

import kornell.api.client.Callback;
import kornell.gui.client.presentation.activity.generic.GenericAtividadeView;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class AtividadePresenter implements AtividadeView.Presenter{
	AtividadeView view;
	
	public AtividadePresenter(AtividadeView view,
							 AtividadePlace place) {
		this.view = view;
		view.setPresenter(this);
		init(place);
	}
	

	private void init(AtividadePlace place) {
		final GenericAtividadeView v = (GenericAtividadeView) view;
		final String packageURL = place.getPackageURL();
		RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, packageURL+"imsmanifest.xml");			
		try {
			reqBuilder.sendRequest(null, new Callback(){
				@Override
				protected void ok(Response response) {
					String text = response.getText();
				    Document dom = XMLParser.parse(text);
				    Node item = dom.getElementsByTagName("item").item(0);
				    Attr identifierref = (Attr) item.getAttributes().getNamedItem("identifierref");
				    String identifierrefVal = identifierref.getValue();
				    NodeList resources = dom.getElementsByTagName("resource");
				    Element resource = null;
				    for (int i = 0; i < resources.getLength(); i++) {
				    	Element res = (Element) resources.item(i);
				    	String ident = res.getAttributeNode("identifier").getValue();
				    	if(identifierrefVal.equals(ident)){
				    		resource = res;
				    	}
					}
				    if(resource != null){
				    	Element file = (Element) resource.getElementsByTagName("file").item(0);
				    	String href = file.getAttribute("href");
				    	String displayUrl = packageURL+href;
				    	v.display(displayUrl);
				    }
				    GWT.log("stop");
				    
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public Widget asWidget() {
		return view.asWidget();
	}
}
