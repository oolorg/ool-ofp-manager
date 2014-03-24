package ool.com.ofpm.json;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Agents")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentInfoListConfigIn {
	@XmlElement(name="AgentInfo")
	private List<AgentInfo> agents = new ArrayList<AgentInfo>();

	public void setAgents(List<AgentInfo> agents) {
		this.agents = agents;
	}
	public List<AgentInfo> getAgents() {
		return agents;
	}
}
