package kornell.gui.client.content;

import kornell.api.client.Callback;
import kornell.api.client.KornellClient;
import kornell.core.shared.data.Course;
import kornell.core.shared.data.CourseTO;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

public class SurrogateRenderer implements Renderer {
	Renderer renderer;
	
	//TODO: Position is not necessary, remove.
	public SurrogateRenderer(KornellClient client,final String uuid, final Integer position) {
		client.getCourseTO(uuid, new Callback<CourseTO>(){
			@Override
			protected void ok(CourseTO course) {			
				setRenderer(new S3Renderer(course,position));
			}
		}); 
	}

	@Override
	public void render(final FlowPanel contentPanel) {
		if (renderer == null)
			new Timer() {
				@Override
				public void run() {
					render(contentPanel);
				}
			}.schedule(500);
		else
			renderer.render(contentPanel);
	}
	
	public void setRenderer(Renderer renderer){
		this.renderer = renderer;
	}

}
