package ool.com.ofpm.validate;

import java.util.List;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class CommonValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(CommonValidate.class);

	public void checkArrayStringBlank(List<String> params) throws ValidateException {
		String fname = "checkArrayStringBlank";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(params=%s) - start", fname, params));
		}
		for (String param : params) {
			if (StringUtils.isBlank(param)) {
				throw new ValidateException(String.format(ErrorMessage.THERE_IS_BLANK, "parameter"));
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
	public void checkArrayOverlapped(List<String> params) throws ValidateException {
		String fname = "checkArrayOverlapped";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(params=%s) - start", fname, params));
		}
		int size = params.size();
		for (int dni = 0; dni < size; dni++) {
			for (int ci = dni + 1; ci < size; ci++) {
				if (params.get(dni).equals(params.get(ci))) {
					throw new ValidateException(String.format(ErrorMessage.THERE_ARE_OVERLAPPED, params.get(dni)));
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	public void checkStringBlank(String param) throws ValidateException {
		String fname = "checkStringBlank";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(param=%s) - start", fname, param));
		}
		if (StringUtils.isBlank(param)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "parameter"));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
