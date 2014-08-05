/**
 * @author OOL 1131080355959
 * @date 2014/07/25
 * @TODO 
 */
package ool.com.ofpm.json.ofc;

import java.lang.reflect.Type;

import ool.com.ofpm.json.device.DeviceInfoCreateJsonIn;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author 1131080355959
 *
 */
public class SetFlowToOFC {
	
	private String dpId;
	
	public String getDpId() {
		return dpId;
	}
	public void setDpId(String dpId) {
		this.dpId = dpId;
	}
	
	public class Match {
		private String inPort;
		private String srcMac;
		
		public String getInPort() {
			return inPort;
		}
		public void setInPort(String inPort) {
			this.inPort = inPort;
		}
		public String getSrcMac() {
			return srcMac;
		}
		public void setSrcMac(String srcMac) {
			this.srcMac = srcMac;
		}
	}
	
	private Match match;
	
	public Match getMatch() {
		return match;
	}
	public void setMatch(Match match) {
		this.match = match;
	}
	
	public class Action {
		private String outPort;
		private String modSrcMac;
		private String modDstMac;
		private Boolean packetIn;
		private Boolean drop;
		
		public String getOutPort() {
			return outPort;
		}
		public void setOutPort(String outPort) {
			this.outPort = outPort;
		}
		
		public String getModSrcMac() {
			return modSrcMac;
		}
		public void setModSrcMac(String modSrcMac) {
			this.modSrcMac = modSrcMac;
		}
		
		public String getModDstMac() {
			return modDstMac;
		}
		public void setModDstMac(String modDstMac) {
			this.modDstMac = modDstMac;
		}
		
		public Boolean getPacketIn() {
			return packetIn;
		}
		public void setPacketIn(Boolean packetIn) {
			this.packetIn = packetIn;
		}
		
		public Boolean getDrop() {
			return drop;
		}
		public void setDrop(Boolean drop) {
			this.drop = drop;
		}
	}
	
	private Action action;
	
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	
	public static SetFlowToOFC fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<SetFlowToOFC>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<SetFlowToOFC>(){}.getType();
		return gson.toJson(this, type);
	}
}
