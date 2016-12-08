/**
 * 
 */
package com.hhi.vaas.platform.vdm.handler;

/**
 * 
 * VDM Conf. XML 로딩 및 vdmpath search 중 발생 에러.
 * @author BongJin Kwon
 *
 */
public class VDMException extends RuntimeException {

	/**
	 * 
	 */
	public VDMException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public VDMException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public VDMException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public VDMException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
