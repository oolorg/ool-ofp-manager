package ool.com.ofpm.service.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

public class ResponseGenerator {
	private static final Logger logger = Logger.getLogger(ResponseGenerator.class);

	public static Response generate(final String res_str, final Status status) {
		String fname = "generate";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(res_str=%s, status=%s) - start", fname, res_str, status));

		ResponseBuilder res_builder = Response.status(status);
		res_builder = res_builder.header("Access-Control-Allow-Origin", "*");
		res_builder = res_builder.header("Access-Control-Allow-Headers", "*, Content-Type");
		res_builder = res_builder.header("Allow", "GET,PUT,,OPTIONS");
		res_builder = res_builder.header("Access-Control-Allow-Methods", "GET,PUT,OPTIONS");
		res_builder = res_builder.entity(res_str);
		Response res = res_builder.build();

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}
}
