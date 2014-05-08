/**
 * @author OOL 1134380013430
 * @date 2014/04/23
 * @TODO TODO
 */
package ool.com.ofpm.client;

import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.NetworkConfigSetupperIn;

/**
 * @author 1134380013430
 *
 */
public interface NetworkConfigSetupperClient {

	public BaseResponse sendPlaneSwConfigData(NetworkConfigSetupperIn networkConfigSetupperIn);
}
