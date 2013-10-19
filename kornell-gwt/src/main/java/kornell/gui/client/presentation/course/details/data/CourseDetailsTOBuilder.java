package kornell.gui.client.presentation.course.details.data;

import java.util.ArrayList;
import java.util.List;

import kornell.core.shared.data.coursedetails.CertificationTO;
import kornell.core.shared.data.coursedetails.CourseDetailsTO;
import kornell.core.shared.data.coursedetails.HintTO;
import kornell.core.shared.data.coursedetails.InfoTO;
import kornell.core.shared.data.coursedetails.TopicTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class CourseDetailsTOBuilder {
	
	private String jsonString;
	private CourseDetailsTO courseDetailsTO;
	
	private enum ParseType {
		TOPICS("topics"),
		INFOS("infos"),
		HINTS("hints");

		private String code;
		private ParseType(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
	} 

	public CourseDetailsTOBuilder(String jsonString) {
		this.jsonString = jsonString;
		this.courseDetailsTO = new CourseDetailsTO();
	}
	
	public boolean buildCourseDetails(){
		JSONValue jsonValue;
	    JSONObject jsonObject;
	    jsonValue = JSONParser.parseStrict(jsonString);
	
		if ((jsonObject = jsonValue.isObject()) == null) {
			GWT.log("Error parsing the JSON");
		}
		boolean ret = true;
		for (ParseType parseType : ParseType.values()) {
			ret = ret && parseJSON(jsonObject.get(parseType.getCode()), parseType);
		}
	    return ret;
	}
	
	private boolean parseJSON(JSONValue jsonValue, ParseType parseType){
	    JSONArray jsonArray;
	    JSONObject jsonObject;
		JSONString type;
		JSONString text;
		JSONString index;
		JSONString title;
		JSONString certificationHeaderTitle;
		JSONString certificationHeaderText;
		
		if (jsonValue == null) {
			GWT.log("Error parsing the JSON");
			return false;
		}
		if ((jsonArray = jsonValue.isArray()) == null) {
			GWT.log("Error parsing the JSON");
			return false;
		}


		switch (parseType) {
		
		case HINTS:
			List<HintTO> hints = new ArrayList<HintTO>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonValue = jsonArray.get(i);
				if ((jsonObject = jsonValue.isObject()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				jsonValue = jsonObject.get("type");
				if ((type = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				
				jsonValue = jsonObject.get("text");
				if ((text = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}		
				hints.add(new HintTO(type.stringValue(),text.stringValue()));
				courseDetailsTO.setHints(hints);	
			}
			break;
			
			
		case INFOS:
			List<InfoTO> infos = new ArrayList<InfoTO>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonValue = jsonArray.get(i);
				if ((jsonObject = jsonValue.isObject()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				jsonValue = jsonObject.get("type");
				if ((type = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				
				jsonValue = jsonObject.get("text");
				if ((text = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}		
				infos.add(new InfoTO(type.stringValue(),text.stringValue()));
				courseDetailsTO.setInfos(infos);	
			}
			break;
			
		/*	
		case TOPICS:
			List<TopicTO> topics = new ArrayList<TopicTO>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonValue = jsonArray.get(i);
				if ((jsonObject = jsonValue.isObject()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				jsonValue = jsonObject.get("index");
				if ((index = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				
				jsonValue = jsonObject.get("title");
				if ((title = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}		
				topics.add(new TopicTO(index.stringValue(),title.stringValue(),"toStart"));
				courseDetailsTO.setTopics(topics);	
			}
			break;
			
			*/
		/*case GENERAL:
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonValue = jsonArray.get(i);
				if ((jsonObject = jsonValue.isObject()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				jsonValue = jsonObject.get("certificationHeaderTitle");
				if ((certificationHeaderTitle = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				
				jsonValue = jsonObject.get("certificationHeaderText");
				if ((certificationHeaderText = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}		
				courseDetailsTO.setCertificationHeaderInfoTO(new InfoTO(certificationHeaderTitle.stringValue(), certificationHeaderText.stringValue()));
			}
			break;*/
			
			
		/*case CERTIFICATIONS:
			List<CertificationTO> certifications = new ArrayList<CertificationTO>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonValue = jsonArray.get(i);
				if ((jsonObject = jsonValue.isObject()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				jsonValue = jsonObject.get("type");
				if ((type = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}
				
				jsonValue = jsonObject.get("name");
				if ((title = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}	
				
				jsonValue = jsonObject.get("text");
				if ((text = jsonValue.isString()) == null) {
					GWT.log("Error parsing the JSON");
					return false;
				}		
				certifications.add(new CertificationTO(type.stringValue(),title.stringValue(),text.stringValue()));
				courseDetailsTO.setCertifications(certifications);	
			}
			break;*/
		default:
			break;
		}
		return true;
	}

	public CourseDetailsTO getCourseDetailsTO() {
		return courseDetailsTO;
	}

	public void setCourseDetailsTO(CourseDetailsTO courseDetailsTO) {
		this.courseDetailsTO = courseDetailsTO;
	}
	
}
