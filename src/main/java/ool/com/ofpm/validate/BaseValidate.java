/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO TODO
 */
package ool.com.ofpm.validate;

/**
 * @author 1131080355959
 *
 */
public abstract class BaseValidate {
	/**
	 * Determine if Object is null.
	 *
	 * @param value
	 * @return
	 * true : Not null. <br>
	 * false: null.
	 */
	protected static boolean checkNull(Object value) {
		return value == null;
	}

	/**
	 * Determine if length of string is less than or equal length.
	 *
	 * @param value
	 * @param length
	 * @return
	 * true : Not over. <br>
	 * false: Over.
	 */
	protected static boolean checkOverLength(String value, int length) {
		if (value.length() > length) {
			return true;
		}
		return false;
	}

	/**
	 * Determine if string contains multi-byte charactor.
	 *
	 * @param value
	 * @return
	 * true : Not contains multi-byte charactor. <br>
	 * false: Contains multi-byte charactor.
	 */
	protected static boolean checkHalfNum(String value) {
		if (value == null || !value.matches("^[0-9]+$")) {
			return false;
		}
		return true;
	}

}
