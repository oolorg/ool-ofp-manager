/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO TODO
 */
package ool.com.ofpm.json;

import com.google.gson.annotations.SerializedName;

/**
 * @author 1131080355959
 *
 */
public class HelloWorldJsonPostOut {

	@SerializedName("status")
	private String enabled;
	
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled (final String enabled) {
		this.enabled = enabled;
	}

}
