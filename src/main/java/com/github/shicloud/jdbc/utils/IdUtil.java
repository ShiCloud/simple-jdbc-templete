package com.github.shicloud.jdbc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.support.KeyHolder;

import com.github.shicloud.exception.NoDefinedMethodException;
import com.github.shicloud.jdbc.annotation.ID;
import com.github.shicloud.utils.ReflectUtil;

public class IdUtil {
	public static Field getIdColumn(Object obj) {
		Map<String, Field> fieldMap = ReflectUtil.getFields(obj.getClass());
		for (Field field : fieldMap.values()) {
			ID annotation = field.getAnnotation(ID.class);
			if (annotation != null) {
				return field;
			}
		}
		return null;
	}

	public static void setIdValue(Object obj, Field idField, KeyHolder keyHolder) 
			throws InvalidDataAccessApiUsageException, IllegalAccessException, 
			IllegalArgumentException, InvocationTargetException, NoDefinedMethodException {
		
		Method setter = ReflectUtil.getSetter(obj, idField.getName());
		
		Class<?>[] returnType = setter.getParameterTypes();
		if(returnType[0].equals(Integer.class) ){
			setter.invoke(obj, keyHolder.getKey().intValue());
		}
		if(returnType[0].equals(Long.class)){
			setter.invoke(obj, keyHolder.getKey().longValue());
		}
	}
}
