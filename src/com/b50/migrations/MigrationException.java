package com.b50.migrations;

public class MigrationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 123400001L;

	public MigrationException() {
	}

	public MigrationException(String arg0) {
		super(arg0);
	}

	public MigrationException(Throwable arg0) {
		super(arg0);
	}

	public MigrationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
