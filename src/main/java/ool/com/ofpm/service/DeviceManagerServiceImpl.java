/**
 * @author OOL 1131080355959
 * @date 2014/03/04
 * @TODO
 */
package ool.com.ofpm.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ool.com.ofpm.business.DeviceManagerBusiness;
import ool.com.ofpm.business.DeviceManagerBusinessImpl;

import org.apache.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author 1131080355959
 *
 */
public class DeviceManagerServiceImpl implements DeviceManagerService {

	private static final Logger logger = Logger.getLogger(DeviceManagerServiceImpl.class);

	@Inject
	DeviceManagerBusiness dmb;

	Injector injector;

	/* (non-Javadoc)
	 * @see ool.com.orientdb.service.DeviceManagerService#getConnectedPortInfo(java.lang.String)
	 */
	@Override
	public Response getConnectedPortInfo(String deviceName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(deviceName=%s) - start ", deviceName));
    	}

        this.injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            	bind(DeviceManagerBusiness.class).to(DeviceManagerBusinessImpl.class);
            }
        });

        DeviceManagerServiceImpl main = injector.getInstance(DeviceManagerServiceImpl.class);
        String ret = main.dmb.getConnectedPortInfo(deviceName);

        if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(ret=%s) - end ", ret));
    	}
		return Response.ok(ret).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

}
