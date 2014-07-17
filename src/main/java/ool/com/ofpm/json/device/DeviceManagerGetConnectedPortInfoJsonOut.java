/**
 * @author OOL 1131080355959
 * @date 2014/03/04
 * @TODO
 */
package ool.com.ofpm.json.device;

import java.util.ArrayList;
import java.util.List;

import ool.com.ofpm.json.common.BaseResponse;

import com.google.gson.annotations.SerializedName;

/**
 * @author 1131080355959
 *
 */
public class DeviceManagerGetConnectedPortInfoJsonOut extends BaseResponse {

	@SerializedName("result")
	private List<ResultData> resultData = new ArrayList<ResultData>();

	public List<ResultData> getResultData() {
		return resultData;
	}

	public void setResultData(final List<ResultData> resultData) {
		this.resultData = resultData;
	}

	public void addResultData(final ResultData resultData) {
		this.resultData.add(resultData);
	}

	public class ResultData {
		@SerializedName("link")
		private List<LinkData> linkData = new ArrayList<LinkData>();

		public List<LinkData> getLinkData() {
			return linkData;
		}

		public void setLinkData(final List<LinkData> linkData) {
			this.linkData = linkData;
		}

		public void addLinkData(final LinkData linkData) {
			this.linkData.add(linkData);
		}

		public class LinkData extends Node {
			private String portName;
			private int portNumber;
			private String ofpFlag;

			public String getPortName() {
				return portName;
			}
			public void setPortName(final String portName) {
				this.portName = portName;
			}
			public int getPortNumber() {
				return portNumber;
			}
			public void setPortNumber(final int portNumber) {
				this.portNumber = portNumber;
			}
			public String getOfpFlag() {
				return ofpFlag;
			}
			public void setOfpFlag(final String ofpFlag) {
				this.ofpFlag = ofpFlag;
			}
		}
	}
}
