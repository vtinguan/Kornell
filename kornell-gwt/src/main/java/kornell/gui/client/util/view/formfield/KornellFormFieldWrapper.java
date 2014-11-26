package kornell.gui.client.util.view.formfield;

import java.util.List;

import static kornell.core.util.StringUtils.*;
import kornell.api.client.Callback;
import kornell.gui.client.validation.ValidationChangedEvent;
import kornell.gui.client.validation.ValidationChangedHandler;
import kornell.gui.client.validation.ValidationMessages;
import kornell.gui.client.validation.Validator;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class KornellFormFieldWrapper extends Composite {
	EventBus fieldBus = new SimpleEventBus();
	
	interface MyUiBinder extends UiBinder<Widget, KornellFormFieldWrapper> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	Label fieldLabel;
	@UiField
	FlowPanel fieldPanel;

	TextBox fieldTextBox;
	ListBox fieldListBox;
	SimpleDatePicker fieldSimpleDatePicker;

	Label fieldError;
	Label fieldTxt;

	boolean isEditMode;
	KornellFormField<?> formField;

	private Validator validator;
	private String tooltipText;

	public KornellFormFieldWrapper(String label, KornellFormField<?> formField) {
		this(label, formField, true,null);
	}

	public KornellFormFieldWrapper(String label, KornellFormField<?> formField,
			boolean isEditMode){
		this(label, formField, isEditMode, null);
	}
	
	public KornellFormFieldWrapper(String label, KornellFormField<?> formField,
			boolean isEditMode, Validator validator) {
			this(label, formField, isEditMode, validator, null);
	}
	
	public KornellFormFieldWrapper(String label, KornellFormField<?> formField,
			boolean isEditMode, Validator validator, String tooltipText) {

		initWidget(uiBinder.createAndBindUi(this));
		fieldLabel.setText(label);
		this.formField = formField;
		this.isEditMode = isEditMode;
		this.validator = validator;
		this.tooltipText = tooltipText;
		initData(formField);
	}

	public void initData(KornellFormField<?> formField) {
		fieldPanel.clear();
		this.formField = formField;
		if (!isEditMode) {
			fieldTxt = new Label();
			fieldTxt.addStyleName("lblValue");
			fieldTxt.setText(formField.getDisplayText());
			fieldPanel.add(fieldTxt);
		} else {
			final Widget fieldWidget = formField.getFieldWidget();
			if(tooltipText != null){
				Tooltip tooltip = new Tooltip(tooltipText);
				tooltip.add(fieldWidget);
				fieldPanel.add(tooltip);
			} else {
				fieldPanel.add(fieldWidget);
			}

			if (fieldWidget instanceof HasChangeHandlers) {
				HasChangeHandlers changeable = (HasChangeHandlers) fieldWidget;
				if(validator != null){
					changeable.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							validator.getErrors(fieldWidget
									,new Callback<List<String>>() {
								
								@Override
								public void ok(List<String> errors) {
									if(errors.isEmpty()){
										setError("");
									}else {
										showErrors(errors);
									}
									fieldBus.fireEvent(new ValidationChangedEvent());
									
								}
							});
							
						}

						private void showErrors(List<String> errorKeys) {
							StringBuilder buf = new StringBuilder();
							for(String e:errorKeys){
								buf.append(e);
							}
							setError(buf.toString());
						}
					});
				}
			}
			
			fieldError = new Label();
			fieldError.addStyleName("error");
			fieldPanel.add(fieldError);
		}
	}

	public KornellFormField<?> getFormField() {
		return formField;
	}

	public Widget getFieldWidget() {
		return formField.getFieldWidget();
	}

	public String getFieldDisplayText() {
		return formField.getDisplayText();
	}

	public String getFieldPersistText() {
		return formField.getPersistText();
	}

	public void setError(String text) {
		if(fieldError != null)
			fieldError.setText(text);
	}

	public String getError() {
		return fieldError != null ? fieldError.getText() : "";
	}

	public void clearError() {
		if (fieldError != null) {
			fieldError.setText("");
		}
	}
	
	public boolean isValid(){		
		return fieldError == null || isNone(fieldError.getText());
	}

	public void addValidationListener(ValidationChangedHandler handler) {
		fieldBus.addHandler(ValidationChangedEvent.TYPE, handler);
	}
	
}