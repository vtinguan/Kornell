package kornell.gui.client.presentation.activity;

import kornell.api.client.Callback;
import kornell.gui.client.presentation.activity.generic.GenericAtividadeView;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class AtividadePresenter implements AtividadeView.Presenter{
	AtividadeView view;
	private AtividadePlace place;
	private PlaceController placeCtrl;
	private NodeList items;
	
	public AtividadePresenter(AtividadeView view,
							 PlaceController placeCtrl) {
		this.placeCtrl = placeCtrl;
		this.view = view;
		view.setPresenter(this);		
	}
	

	private void displayPlace() {
		final GenericAtividadeView v = (GenericAtividadeView) view;
		final String packageURL = place.getPackageURL();
		RequestBuilder reqBuilder = new RequestBuilder(RequestBuilder.GET, packageURL+"imsmanifest.xml");
		try {
			reqBuilder.sendRequest(null, new Callback(){
				

				@Override
				protected void ok(Response response) {
					String text = response.getText();
				    Document dom = XMLParser.parse(text);
				    items = dom.getElementsByTagName("item");				    
					GWT.log("*** Showing item "+place.getItem() +" of "+ items.getLength());
					Node item = items.item(place.getItem());
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


	@Override
	public void goContinue() {
		if(place.getItem().compareTo(items.getLength()-1) < 0)
		placeCtrl.goTo(place.next());
	}


	@Override
	public void goPrevious() {
		if(place.getItem().compareTo(new Integer("0")) > 0)
		placeCtrl.goTo(place.previous());
	}


	public void setPlace(AtividadePlace place) {
		this.place = place;
		displayPlace();
	}
}
