package ool.com.ofpm.json;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

public class GraphDBResult {
	public static Type TYPE = new TypeToken<GraphDBResult>(){}.getType();
	public static String RESULT_OK = "OK";
	public static String RESULT_NG = "NG";

	public class GraphDBResultMessage {
		private String Code;
		private String Desc;
		public String getCode() {
			return Code;
		}
		public void setCode(String code) {
			Code = code;
		}
		public String getDesc() {
			return Desc;
		}
		public void setDesc(String desc) {
			Desc = desc;
		}
	}

	private String Result;
	private GraphDBResultMessage Message;
	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public GraphDBResultMessage getMessage() {
		return Message;
	}

	public void setMessage(
			GraphDBResultMessage message) {
		Message = message;
	}
}
