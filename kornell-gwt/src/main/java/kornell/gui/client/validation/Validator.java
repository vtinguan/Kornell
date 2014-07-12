package kornell.gui.client.validation;

import java.util.List;

import kornell.api.client.Callback;

import com.google.gwt.user.client.ui.Widget;

public interface Validator {

	void getErrors(Widget widget,Callback<List<String>> cb);

}
