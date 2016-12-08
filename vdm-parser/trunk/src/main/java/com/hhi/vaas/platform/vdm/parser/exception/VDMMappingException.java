/**
 * 
 */
package com.hhi.vaas.platform.vdm.parser.exception;

/**
 * Mapping XML 에서 원하는 정보를 가져오지 못했을때 발생 Exception
 * @author BongJin Kwon
 *
 */
public class VDMMappingException extends RuntimeException {

	/**
	 * 
	 */
	public VDMMappingException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public VDMMappingException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public VDMMappingException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public VDMMappingException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
