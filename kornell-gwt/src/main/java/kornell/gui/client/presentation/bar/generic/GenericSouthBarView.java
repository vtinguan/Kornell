package kornell.gui.client.presentation.bar.generic;

import kornell.gui.client.content.NavigationRequest;
import kornell.gui.client.presentation.atividade.AtividadePlace;
import kornell.gui.client.presentation.bar.ActivityBarView;
import kornell.gui.client.presentation.bar.CourseBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.course.CoursePlace;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;


public class GenericSouthBarView extends Composite implements SouthBarView {
	
	interface MyUiBinder extends UiBinder<Widget, GenericSouthBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	private ActivityBarView activityBarView;
	
	private CourseBarView courseBarView;
	
	private boolean visible = false;
	
	private PlaceController placeCtrl;

	@UiField
	FlowPanel southBar;

	private EventBus bus;
	
	public GenericSouthBarView(EventBus bus, PlaceController placeCtrl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.bus = bus;
		this.placeCtrl = placeCtrl;

		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						Place newPlace = event.getNewPlace();
						
						if(newPlace instanceof AtividadePlace){
							southBar.clear();
							southBar.add(getActivityBarView());
							visible = true;
						} else if(newPlace instanceof CoursePlace){
							southBar.clear();
							southBar.add(getCourseBarView());
							visible = true;
						} else {
							visible = false;
						}
						
					}});
	}
		
	private ActivityBarView getActivityBarView() {
		if (activityBarView == null)
			activityBarView = new GenericActivityBarView(bus);
		return activityBarView;
	}
	
	private CourseBarView getCourseBarView() {
		if (courseBarView == null)
			courseBarView = new GenericCourseBarView(bus,placeCtrl);
		return courseBarView;
	}


	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
	}

}
