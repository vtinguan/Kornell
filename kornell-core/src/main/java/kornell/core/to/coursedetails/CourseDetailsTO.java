package kornell.core.to.coursedetails;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailsTO {
	
	private List<HintTO> hints;
	private List<InfoTO> infos;
	private List<TopicTO> topics;
	
	public CourseDetailsTO() {
		infos = new ArrayList<InfoTO>();
		hints = new ArrayList<HintTO>();
		topics = new ArrayList<TopicTO>(); 
	}
	

	public List<HintTO> getHints() {
		return hints;
	}


	public void setHints(List<HintTO> hints) {
		this.hints = hints;
	}


	public List<InfoTO> getInfos() {
		return infos;
	}


	public void setInfos(List<InfoTO> infos) {
		this.infos = infos;
	}


	public List<TopicTO> getTopics() {
		return topics;
	}


	public void setTopics(List<TopicTO> topics) {
		this.topics = topics;
	}


	@Override
	public String toString() {
		String ret = "";
		if(hints != null){
			ret += "----------HintsTO\n";
			for (HintTO hintTO : hints) {
				ret += hintTO + "\n";
			}
		}
		if(infos != null){
			ret += "----------InfosTO\n";
			for (InfoTO infoTO : infos) {
				ret += infoTO + "\n";
			}
		}
		if(topics != null){
			ret += "----------TopicsTO\n";
			for (TopicTO topicTO : topics) {
				ret += topicTO + "\n";
			}
		}
		return ret;
	}
}
