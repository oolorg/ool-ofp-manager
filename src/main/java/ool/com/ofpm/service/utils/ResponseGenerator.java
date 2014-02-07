package ool.com.ofpm.service.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

public class ResponseGenerator {
	public static Response generate(final String res_str, final Status status) {
		ResponseBuilder res_builder = Response.status(status);
		res_builder = res_builder.header("Access-Control-Allow-Origin", "*");
		res_builder = res_builder.header("Access-Control-Allow-Header", "*");
		res_builder = res_builder.header("Allow", "GET,POST,PUT,DELETE,OPTIONS");
		res_builder = res_builder.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
		res_builder = res_builder.entity(res_str);
		return res_builder.build();
	}
}
