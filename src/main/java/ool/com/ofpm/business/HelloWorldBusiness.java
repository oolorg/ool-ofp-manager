/**
 * @author OOL 1131080355959
 * @date 2014/02/05
 * @TODO TODO
 */
package ool.com.ofpm.business;

import javax.servlet.http.HttpServletRequest;

import ool.com.ofpm.json.HelloWorldJsonPostIn;
import ool.com.ofpm.json.HelloWorldJsonPostOut;

/**
 * @author 1131080355959
 *
 */
public interface HelloWorldBusiness {

	public String sayHello(String message, HttpServletRequest req);
	
	public HelloWorldJsonPostOut createHello(HelloWorldJsonPostIn params);
}
