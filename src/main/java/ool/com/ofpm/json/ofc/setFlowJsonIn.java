/**
 * @author OOL 1131080355959
 * @date 2014/07/25
 * @TODO 
 */
package ool.com.ofpm.json.ofc;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author 1131080355959
 *
 */
public class setFlowJsonIn {
	private String dpId;
	private String inPort;
	private String srcMac;
	private String dstMac;
	
	public String getDpId() {
		return dpId;
	}
	public void setDpId(String dpId) {
		this.dpId = dpId;
	}
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
	public String getDstMac() {
		return dstMac;
	}
	public void setDstMac(String dstMac) {
		this.dstMac = dstMac;
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<setFlowJsonIn>() {}.getType();
		return gson.toJson(this, type);
	}
}
