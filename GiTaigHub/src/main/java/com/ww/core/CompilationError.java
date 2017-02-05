package com.ww.core;

public class CompilationError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CompilationError() {
		super();
	}
	public CompilationError(String message) {
		super(message);
	}
	public CompilationError(Throwable exception) {
		super(exception);
	}

}
