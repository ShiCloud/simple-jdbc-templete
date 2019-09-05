package com.github.shicloud.jdbc.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标注该注解后会根据主键类型生成sql语句
 * <ul>
 * <li>2019年6月12日 | 史锋 | 新增</li>
 * </ul>
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface ID {
	
	public enum TYPE {  
		AUTO,INPUT
	}
	
	TYPE value() default TYPE.AUTO;
    String column() default " id ";
}
