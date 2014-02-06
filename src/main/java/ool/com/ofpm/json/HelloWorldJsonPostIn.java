/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO TODO
 */
package ool.com.ofpm.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author 1131080355959
 *
 */
public class HelloWorldJsonPostIn {
	
	class ChildHelloWorld {
		@SerializedName("word")
		private String param;
		
		public String getParam() {
			return param;
		}
		public void setHello(final String param) {
			this.param = param;
		}
	}
	
	@SerializedName("params")
	private List<ChildHelloWorld> listchw; 
	
	@SerializedName("status")
	private String enabled;
	
	public List<ChildHelloWorld> getListchw() {
		return listchw;
	}
	public void setListchw (final List<ChildHelloWorld> listchw) {
		this.listchw = listchw;
	}
	
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled (final String enabled) {
		this.enabled = enabled;
	}

}
