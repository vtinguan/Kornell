package kornell.gui.client.uidget.formfield;

import java.util.Date;

import kornell.gui.client.presentation.profile.ProfileView;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


public class CopyOfKornellFormFieldWrapper extends Composite implements ProfileView {
	interface MyUiBinder extends UiBinder<Widget, CopyOfKornellFormFieldWrapper> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	
	@UiField Label fieldLabel;
	@UiField FlowPanel fieldPanel;
	
	TextBox fieldTextBox;
	ListBox fieldListBox;
	SimpleDatePicker fieldSimpleDatePicker;
	
	Label fieldError;
	Label fieldTxt;
	
	Widget widget;
	KornellFormFieldType formFieldType;
	Object value;
	boolean isEdit;
	
	public CopyOfKornellFormFieldWrapper(String label, KornellFormFieldType formFieldType, Object value, boolean isEdit) {

		initWidget(uiBinder.createAndBindUi(this));
		fieldLabel.setText(label);
		this.formFieldType = formFieldType;
		this.value = value;
		this.isEdit = isEdit;
		
		initData(formFieldType, value);
	}
	
	public void initData(KornellFormFieldType formFieldType, Object value) {
		fieldPanel.clear();
		String valueStr = "";
		switch (formFieldType) {
		case TextBox:
			widget = fieldTextBox = new TextBox();
			widget.addStyleName("field");
			widget.addStyleName("textField");
			fieldTextBox.setValue((String) value);
			valueStr = (String) value;
			break;
		case ListBox:
			widget =  fieldListBox = (ListBox) value; 
			valueStr = "-".equals(((ListBox) value).getValue()) ? "" : ((ListBox) value).getItemText(((ListBox) value).getSelectedIndex());
			break;
		case SimpleDatePicker:
			if(value == null){
				widget = fieldSimpleDatePicker = new SimpleDatePicker();
				valueStr = "";
			} else {
				widget = fieldSimpleDatePicker = new SimpleDatePicker((Date) value);
				valueStr = fieldSimpleDatePicker.toString();
			}
			break;
		default:
			break;
		}		

		if(!isEdit){
			fieldTxt = new Label();
			fieldTxt.addStyleName("lblValue");
			fieldTxt.setText(valueStr);
			fieldPanel.add(fieldTxt);
		} else {
			fieldPanel.add(widget);
			fieldError = new Label();
			fieldError.addStyleName("error");
			fieldPanel.add(fieldError);
		}
		
	}
	
	public Widget getFieldWidget(){
		return widget;
	}
	
	@Override
	public String toString(){
		if(!isEdit)
			return fieldTxt.getText();
		switch (formFieldType) {
		case TextBox: return fieldTextBox.getValue();
		case ListBox: return fieldListBox.getValue();
		case SimpleDatePicker: return fieldSimpleDatePicker.toString();
		default: return "";
		}	
	}
	
	public void setError(String text){
		fieldError.setText(text);
	}
	
	public String getError(){
		return fieldError == null ? "" : fieldError.getText();
	}

	public void clearError(){
		if(fieldError != null){
			fieldError.setText("");
		}
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
	}
}