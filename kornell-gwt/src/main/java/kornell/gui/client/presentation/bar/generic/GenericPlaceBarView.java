package kornell.gui.client.presentation.bar.generic;

import java.util.List;

import kornell.gui.client.presentation.bar.PlaceBarView;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GenericPlaceBarView extends Composite implements PlaceBarView {
	interface MyUiBinder extends UiBinder<Widget, GenericPlaceBarView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Icon icon;
	@UiField
	Label title;
	@UiField
	Label subtitle;
	@UiField
	FlowPanel btnPanel;
	
	public GenericPlaceBarView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void init(IconType iconType, String titleStr, String subtitleStr){
		this.icon.setIcon(iconType);
		this.title.setText(titleStr);
		this.subtitle.setText(subtitleStr);
		btnPanel.clear();
	}
	
	public void setWidgets(List<IsWidget> widgets){
		btnPanel.clear();
		for (IsWidget widget : widgets) {
			btnPanel.add(widget);
		}
	}

}