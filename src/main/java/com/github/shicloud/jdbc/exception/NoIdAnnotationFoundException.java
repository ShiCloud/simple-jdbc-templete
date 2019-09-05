package com.github.shicloud.jdbc.exception;

public class NoIdAnnotationFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	public NoIdAnnotationFoundException(Class<? extends Object> clazz){
		super(clazz + " doesn't have an id field, please make sure the getters of " + clazz + " contain a column with an @ID annotation.");
	}
}
