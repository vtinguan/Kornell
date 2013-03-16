package kornell.gui.client.content;

import kornell.core.shared.data.CourseTO;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class S3Renderer implements Renderer {
	
	public S3Renderer(CourseTO course, Integer position) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(FlowPanel contentPanel) {
		contentPanel.add(new Label("Uala!"));
	}

}
