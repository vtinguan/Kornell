package kornell.core.value;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;


@Category(DateCategory.class)
public interface ValueFactory extends AutoBeanFactory {
	AutoBean<Date> newDate();
}
