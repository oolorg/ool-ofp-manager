package ool.com.ofpm.business;

import ool.com.ofpm.json.PhysicalRequestIn;
import ool.com.ofpm.json.ResultOut;

public interface PhysicalBusiness {
	public ResultOut execAPI(PhysicalRequestIn req_body, RequestType req_type, Order order);

	enum Order {
		APPEND,
		DELETE,
		UPDATE
	}
	enum RequestType {
		PORT,
		DEVICE
	}
}
