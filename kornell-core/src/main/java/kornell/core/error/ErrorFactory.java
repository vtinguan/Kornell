package kornell.core.error;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface ErrorFactory extends AutoBeanFactory {
	AutoBean<KornellError> newError();
}
