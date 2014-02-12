package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.PhysicalBusiness;
import ool.com.ofpm.business.PhysicalBusiness.Order;
import ool.com.ofpm.business.PhysicalBusiness.RequestType;
import ool.com.ofpm.business.PhysicalBusinessImpl;
import ool.com.ofpm.business.PhysicalServiceBypusBusiness;
import ool.com.ofpm.json.PhysicalRequestIn;
import ool.com.ofpm.json.ResultOut;
import ool.com.ofpm.service.utils.ResponseGenerator;
import ool.com.ofpm.utils.Definition;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//Path("/PhysicalTopology")
@Component
public class PhysicalServiceImpl implements PhysicalService {

	private Gson gson = new Gson();

	private Response execBusiness(String body, RequestType req_type, Order order) {
		PhysicalRequestIn req_body = this.gson.fromJson(body, PhysicalRequestIn.TYPE);

		// GUIとAgent、どちらの通知か判別し、処理クラスをインスタンス化します。
		PhysicalBusiness device_manage = new PhysicalBusinessImpl();

		ResultOut obj_result = device_manage.execAPI(req_body, req_type, order);

		String        str_result = this.gson.toJson(obj_result, ResultOut.TYPE);
		Response        res_body = ResponseGenerator.generate(str_result,  Status.OK);
		return res_body;
	}

	public Response createDevice(@RequestBody String body) {
		Response res_body = this.execBusiness(body, RequestType.DEVICE, Order.APPEND);
		return res_body;
	}

	public Response deleteDevice(@RequestBody String body) {
		Response res_body = this.execBusiness(body, RequestType.DEVICE, Order.DELETE);
		return res_body;
	}
	public Response updateDevice(@RequestBody String body) {
		Response res_body = this.execBusiness(body, RequestType.DEVICE, Order.UPDATE);
		return res_body;
	}

	public Response createPort(@RequestBody String body) {
		Response res_body = this.execBusiness(body, RequestType.PORT, Order.APPEND);
		return res_body;
	}

	public Response deletePort(@RequestBody String body) {
		Response res_body = this.execBusiness(body, RequestType.PORT, Order.DELETE);
		return res_body;
	}
	public Response updatePort(@RequestBody String body) {
		Response res_body = this.execBusiness(body, RequestType.PORT, Order.UPDATE);
		return res_body;
	}

	public Response get() {
		PhysicalServiceBypusBusiness bypus = new PhysicalServiceBypusBusiness(Definition.OLD_TEST_APP_ADDR);
		ResultOut result_out = bypus.get();
		Type collectionType = new TypeToken<ResultOut>(){}.getType();
		String res_str =  this.gson.toJson(result_out, collectionType);
		return ResponseGenerator.generate(res_str, Status.OK);
	}

	public Response get(
			@PathParam("switchId") String switchId) {
		// TODO Auto-generated method stub
		return null;
	}

}

