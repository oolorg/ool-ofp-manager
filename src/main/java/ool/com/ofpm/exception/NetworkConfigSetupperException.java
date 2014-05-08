/**
 * @author OOL 1134380013430
 * @date 2014/04/23
 * @TODO TODO
 */
package ool.com.ofpm.exception;

import ool.com.commons.exception.RestClientException;

/**
 * @author 1134380013430
 *
 */
public class NetworkConfigSetupperException extends RestClientException {
	private Integer status;

	public NetworkConfigSetupperException(String msg) {
		super(msg);
	}
	public NetworkConfigSetupperException(String msg, Integer status) {
		super(msg);
		this.status = status;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
