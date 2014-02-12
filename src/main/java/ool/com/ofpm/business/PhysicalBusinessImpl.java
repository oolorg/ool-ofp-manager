package ool.com.ofpm.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.BaseResultOut;
import ool.com.ofpm.json.GraphDBResult;
import ool.com.ofpm.json.GraphDBResult.GraphDBResultMessage;
import ool.com.ofpm.json.PhysicalRequestIn;
import ool.com.ofpm.json.PhysicalRequestIn.NodeStatus;
import ool.com.ofpm.json.PhysicalRequestIn.NodeType;
import ool.com.ofpm.json.ResultOut;
import ool.com.ofpm.utils.Definition;

public class PhysicalBusinessImpl implements PhysicalBusiness {

	private GraphDBClient gdb_client = OrientDBClientImpl.getInstance(Definition.GRAPH_DB_ADDRESS);
	private Map<String, String> ipToName = new HashMap<String, String>();

	public ResultOut execAPI(PhysicalRequestIn request_in, RequestType req_type, Order order) {
		ResultOut result_out = new ResultOut();
		List<PhysicalRequestIn.Data> devices = request_in.getDevices();

		// RESTサーバとして受け取ったリクエストを解析し、処理要求の配列をDBへ適用していきます。
		for(Iterator<PhysicalRequestIn.Data> devices_iterator = devices.iterator(); devices_iterator.hasNext(); ) {
			PhysicalRequestIn.Data device_info = devices_iterator.next();

			// TODO ここに入力変数チェックを入れましょう！！

			// Graph DBへ処理を要求します。
			String     device_name = device_info.getDeviceName();
			String       port_name = device_info.getPortName();
			NodeType     node_type = device_info.getType();
			NodeStatus node_status = device_info.getStatus();

			GraphDBResult gdb_result = null;
			if(RequestType.DEVICE == req_type) {
				     if(Order.APPEND == order) gdb_result = gdb_client.appendDevice(device_name, node_type);
				else if(Order.DELETE == order) gdb_result = gdb_client.deleteDevice(device_name, node_type);
				else if(Order.UPDATE == order) gdb_result = gdb_client.updateDevice(device_name, node_type, node_status);
			} else if(RequestType.PORT == req_type) {
				     if(Order.APPEND == order) gdb_result = gdb_client.appendPort(device_name, node_type, port_name);
				else if(Order.DELETE == order) gdb_result = gdb_client.deletePort(device_name, node_type, port_name);
				else if(Order.UPDATE == order) gdb_result = gdb_client.updatePort(device_name, node_type, port_name, node_status);
			}

			// エラーの場合は途中で抜けます。
			if(GraphDBResult.RESULT_OK != gdb_result.getResult()) {
				GraphDBResultMessage gdb_msg = gdb_result.getMessage();
				result_out.setStatus(BaseResultOut.Status.INTERNAL_SERVER_ERROR);
				result_out.setMessage(gdb_msg.getCode() + gdb_msg.getDesc());
			}
		}
		result_out.setStatus(BaseResultOut.Status.OK);
		return result_out;
	}
}
