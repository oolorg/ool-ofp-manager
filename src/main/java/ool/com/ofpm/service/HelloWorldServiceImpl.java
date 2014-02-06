/**
 * 
 */
package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ool.com.ofpm.business.HelloWorldBusiness;
import ool.com.ofpm.business.HelloWorldBusinessImpl;
import ool.com.ofpm.json.HelloWorldJsonPostIn;
import ool.com.ofpm.json.HelloWorldJsonPostOut;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author 1131080355959
 *
 */
@Component
public class HelloWorldServiceImpl implements HelloWorldService {

    @Inject
    HelloWorldBusiness hwb;

    Injector injector;
    
    Gson gson = new Gson();
	
    @Override
    public String sayHello(String message, HttpServletRequest req, HttpServletResponse res) {
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
            	bind(HelloWorldBusiness.class).to(HelloWorldBusinessImpl.class);
            }
        });
    	HelloWorldServiceImpl main = injector.getInstance(HelloWorldServiceImpl.class);
        return String.format("Hello, %s", main.hwb.sayHello(message, req));
    }

    @Override
    public String allHello() {
	// TODO Auto-generated method stub
	return "all";
    }

    /* (non-Javadoc)
     * @see ool.com.ofm.service.ResourceService#createHello(java.lang.String)
     */
    @Override
    public Response createHello(String params) {
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
            	bind(HelloWorldBusiness.class).to(HelloWorldBusinessImpl.class);
            }
        });
        Type type = new TypeToken<HelloWorldJsonPostIn>(){}.getType();
        HelloWorldJsonPostIn inPara = gson.fromJson(params, type);
        
    	HelloWorldServiceImpl main = injector.getInstance(HelloWorldServiceImpl.class);
    	HelloWorldJsonPostOut res = main.hwb.createHello(inPara);
    	
        type = new TypeToken<HelloWorldJsonPostOut>(){}.getType();
        String outPara = gson.toJson(res, type);
        return Response.ok(outPara).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
