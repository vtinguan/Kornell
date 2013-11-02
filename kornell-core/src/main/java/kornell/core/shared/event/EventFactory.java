package kornell.core.shared.event;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface EventFactory extends AutoBeanFactory  {
	static final String PREFIX = "application/vnd.kornell.v1.event."; 
	AutoBean<ActomEntered> newActomEntered();
}
