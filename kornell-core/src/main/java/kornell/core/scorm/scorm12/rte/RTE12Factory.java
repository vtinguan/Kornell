package kornell.core.scorm.scorm12.rte;

import kornell.core.scorm.scorm12.rte.action.OpenSCO12Action;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface RTE12Factory extends AutoBeanFactory { 
	public static String PREFIX = "application/vnd.kornell.v1.scorm12.rte";
	
	AutoBean<OpenSCO12Action> newOpenSCO12Action();


}
