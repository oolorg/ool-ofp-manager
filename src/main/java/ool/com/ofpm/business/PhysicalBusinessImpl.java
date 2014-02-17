package ool.com.ofpm.business;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.ResultOut;

public class PhysicalBusinessImpl
		implements PhysicalBusiness {

	private GraphDBClient gdb_client = OrientDBClientImpl.getInstance();

//	public ResultOut execAPI(PhysicalRequestIn request_in, RequestType req_type, Order order) {
//		ResultOut result_out = new ResultOut();
//		List<PhysicalRequestIn.Data> devices = request_in.getDevices();
//
//		// RESTサーバとして受け取ったリクエストを解析し、処理要求の配列をDBへ適用していきます。
//		for(PhysicalRequestIn.Data device_info : devices) {
//			// TODO ここに入力変数チェックを入れましょう！！
//
//			// Graph DBへ処理を要求します。
//			String device_name = device_info.getDeviceName();
//			String port_name = device_info.getPortName();
//			NodeType node_type = device_info.getType();
//			NodeStatus node_status = device_info.getStatus();
//
//			GraphDBResult gdb_result = null;
//			if (RequestType.DEVICE == req_type) {
//				if (Order.APPEND == order)
//					gdb_result = gdb_client.appendDevice(device_name, node_type);
//				else if (Order.DELETE == order)
//					gdb_result = gdb_client.deleteDevice(device_name, node_type);
//				else if (Order.UPDATE == order)
//					gdb_result = gdb_client.updateDevice(device_name, node_type, node_status);
//			} else if (RequestType.PORT == req_type) {
//				if (Order.APPEND == order)		gdb_result = gdb_client.appendPort(device_name, node_type, port_name);
//				else if (Order.DELETE == order)	gdb_result = gdb_client.deletePort(device_name, node_type, port_name);
//				else if (Order.UPDATE == order)	gdb_result = gdb_client.updatePort(device_name, node_type, port_name, node_status);
//			}
//
//			// エラーの場合は途中で抜けます。
//			if (GraphDBResult.RESULT_OK != gdb_result.getResult()) {
//				GraphDBResultMessage gdb_msg = gdb_result.getMessage();
//				result_out.setStatus(BaseResultOut.BaseResponse.INTERNAL_SERVER_ERROR);
//				result_out.setMessage(gdb_msg.getCode() + gdb_msg.getDesc());
//			}
//		}
//		result_out.setStatus(BaseResultOut.BaseResponse.OK);
//		return result_out;
//	}

	public ResultOut getPhysicalTopology() {
		// TODO Auto-generated method stub
		return null;
	}

	public BaseResponse updatePhysicalTopology() {
		// TODO Auto-generated method stub
		return null;
	}
}
