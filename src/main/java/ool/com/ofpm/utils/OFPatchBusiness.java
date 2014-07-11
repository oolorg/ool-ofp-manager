/**
 * @author OOL 1131080355959
 * @date 2014/07/09
 * @TODO TODO
 */
package ool.com.ofpm.utils;

import java.util.List;

import ool.com.ofpm.json.GraphDBPatchLinkJsonRes;

/**
 * @author 1131080355959
 *
 */
public interface OFPatchBusiness {

	GraphDBPatchLinkJsonRes connectPatch(List<String> deviceNameList);

	GraphDBPatchLinkJsonRes disConnectPatch(List<String> deviceNameList);
}
