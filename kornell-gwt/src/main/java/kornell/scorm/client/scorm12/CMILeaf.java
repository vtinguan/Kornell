package kornell.scorm.client.scorm12;

import java.util.logging.Logger;

public class CMILeaf extends CMITree {
	Logger logger = Logger.getLogger(CMILeaf.class.getName());
	
	String value;
	boolean dirty = false;

	private CMINode parent;
	private String key;
	
	public CMILeaf(CMINode parent,String key) {
		this.parent = parent;
		this.key = key;
	}
	
	@Override
	public String getValue(String param) {
		return value;
	}
	
	@Override
	public String setValue(String key, String value) {
		boolean valueChanged = true;
		if (this.value == null) {
			valueChanged = value != null;
		}else {
			valueChanged = ! this.value.equals(value);
		}
		
		if(valueChanged){
			logger.warning("["+dirty+"/"+valueChanged+"] "+getKey()+" = "+this.value+" ==> "+value);
		}else{
			logger.warning("["+dirty+"/"+valueChanged+"] "+getKey()+" = "+this.value+" === "+value);
		}
		
		this.dirty = valueChanged;
		this.value = value;		
		return TRUE;
	}

	@Override
	boolean isLeaf() {
		return true;
	}
	
	@Override
	public String toString() {
		return 
				(dirty ? "* " : "")
				+ (value != null ? value.toString() : null);
	}

	public boolean isDirty() {
		return dirty;
	}

	@Override
	void scrub() {
		dirty = false;
		logger.warning("[SCRUB] "+getKey()+" = "+this.value);
	}
	
	String getKey(){
		return parent.getKey()+"."+key;
	}
}
