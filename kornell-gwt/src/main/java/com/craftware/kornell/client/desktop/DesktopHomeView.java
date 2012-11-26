package com.craftware.kornell.client.desktop;

import com.craftware.kornell.client.presenter.home.HomeView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DesktopHomeView  extends Composite implements HomeView {
	interface MyUiBinder extends UiBinder<Widget, DesktopHomeView> {}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	//private PlaceController placeCtrl;

	 
	public DesktopHomeView(PlaceController placeCtrl) {
		//this.placeCtrl = placeCtrl;
	    initWidget(uiBinder.createAndBindUi(this));
	}
	

	@Override
	public void setPresenter(Presenter presenter) {
	 		
	}
	

}
