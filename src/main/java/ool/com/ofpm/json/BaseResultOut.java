package ool.com.ofpm.json;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

public class BaseResultOut {
	private Status status;
	private String message;

	public static Type TYPE_TOKEN = new TypeToken<BaseResultOut>(){}.getType();

	public String getMessage() {
		return this.message;
	}
	public void setMessage(final String message) {
		this.message = message;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(final Status status) {
		this.status = status;
	}

	public enum Status {
		OK("200"),
		INTERNAL_SERVER_ERROR("500");

		private String name;
		public String getName() {
			return this.name;
		}
		private Status(String name) {
			this.name = name;
		}
	}
}
