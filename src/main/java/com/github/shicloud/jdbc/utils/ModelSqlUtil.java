package com.github.shicloud.jdbc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.shicloud.exception.NoDefinedMethodException;
import com.github.shicloud.jdbc.annotation.ID;
import com.github.shicloud.jdbc.annotation.Prefix;
import com.github.shicloud.jdbc.annotation.Suffix;
import com.github.shicloud.jdbc.exception.NoIdAnnotationFoundException;
import com.github.shicloud.jdbc.templete.SqlParamsPairs;
import com.github.shicloud.utils.CamelNameUtils;
import com.github.shicloud.utils.ReflectUtil;


@Component
public class ModelSqlUtil {

	private static String PREFIX;
	private static String SUFFIX;

	@Value("${jdbc.template.prefix}")
	public void setPrefix(String prefix) {
		ModelSqlUtil.PREFIX = prefix;
	}
	@Value("${jdbc.template.suffix}")
	public void setSuffix(String suffix) {
		ModelSqlUtil.SUFFIX = suffix;
	}

	private static final Logger logger = LoggerFactory.getLogger(ModelSqlUtil.class);

	private static final Map<Class<?>, String> tableNameCache = new ConcurrentHashMap<Class<?>, String>();

	private static <T> String getTableName(Class<T> clazz) {
		String tableName = tableNameCache.get(clazz);
		if (tableName != null) {
			return tableName;
		}
		Table tableAnno = clazz.getAnnotation(Table.class);
		if (tableAnno != null) {
			if (tableAnno.name() != null && !tableAnno.name().trim().equals("")) {
				tableName = tableAnno.name();
			}
		} else {
			String className = clazz.getName();
			tableName = CamelNameUtils.camel2underscore(className.substring(className.lastIndexOf(".") + 1));
			//默认加前缀
			Prefix prefixAnno = clazz.getAnnotation(Prefix.class);
			tableName = (prefixAnno != null && prefixAnno.value() == true) ? (PREFIX + tableName) : tableName;
			
			//添加后缀
			Suffix suffixAnno = clazz.getAnnotation(Suffix.class);
			tableName = (suffixAnno != null && suffixAnno.value() == true) ? (tableName + SUFFIX) : tableName;
		}
		tableNameCache.put(clazz, tableName);
		return tableName;
	}
	
	public static <T> String getColumnName(Field field) {
		Column columnAnno = field.getAnnotation(Column.class);
		if (columnAnno != null) {
			// 如果是列注解就读取name属性
			return columnAnno.name();
		}

		return CamelNameUtils.camel2underscore(field.getName());
	}

	public static SqlParamsPairs createInsert(Object obj, String tableName) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoDefinedMethodException {
		StringBuffer sql = new StringBuffer();
		StringBuffer paramsSql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();

		sql.append(" insert into " + (tableName == null ? getTableName(obj.getClass()) : tableName) + " (");

		Map<String, Field> fieldMap = ReflectUtil.getFields(obj.getClass());
		boolean first = true;
		for (Field field : fieldMap.values()) {
			// 不持久化这个Field
			Transient tranAnno = field.getAnnotation(Transient.class);
			if (tranAnno != null) {
				continue;
			}
			// 自增主键不需要生成到sql
			ID idAnno = field.getAnnotation(ID.class);
			if (idAnno != null && ID.TYPE.AUTO.equals(idAnno.value())) {
				continue;
			}

			Method getter = ReflectUtil.getGetter(obj, field.getName());

			String columnName = getColumnName(field);
			sql.append(columnName).append(",");

			if (!first) {
				paramsSql.append(",");
			}
			paramsSql.append("?");

			params.add(getter.invoke(obj));

			first = false;
		}
		// 删去逗号
		sql.delete(sql.length() - 1, sql.length()).append(")values(").append(paramsSql + ")");

		SqlParamsPairs sqlParamsPairs = new SqlParamsPairs(sql.toString(), params.toArray());
		logger.debug(sqlParamsPairs.toString());

		return sqlParamsPairs;
	}

	public static SqlParamsPairs createUpdateById(Object obj, String tableName,boolean withNull) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoDefinedMethodException, NoIdAnnotationFoundException {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();

		sql.append(" update " + (tableName == null ? getTableName(obj.getClass()) : tableName) + " set ");

		Map<String, Field> fieldMap = ReflectUtil.getFields(obj.getClass());

		Object idValue = null;
		String idName = null;
		for (Field field : fieldMap.values()) {
			// 不持久化这个Field
			Transient tranAnno = field.getAnnotation(Transient.class);
			if (tranAnno != null) {
				continue;
			}

			Method getter = ReflectUtil.getGetter(obj, field.getName());

			Object value = getter.invoke(obj);
			
			if(value == null && !withNull) {
				continue;
			}

			String columnName = getColumnName(field);

			ID idAnno = field.getAnnotation(ID.class);
			if (idAnno != null) {
				idValue = value;
				idName = columnName;
				continue;
			}
			sql.append(columnName).append(" = ?,");

			params.add(value);

		}
		if(idValue == null) {
			throw new NoIdAnnotationFoundException(obj.getClass());
		}
		// 删去逗号
		sql.delete(sql.length() - 1, sql.length()).append(" where " + idName + " = ? ");
		params.add(idValue);

		SqlParamsPairs sqlParamsPairs = new SqlParamsPairs(sql.toString(), params.toArray());
		logger.debug(sqlParamsPairs.toString());

		return sqlParamsPairs;
	}

	public static SqlParamsPairs createDeleteById(Object obj, String tableName) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoDefinedMethodException, NoIdAnnotationFoundException {
		StringBuffer sql = new StringBuffer();
		List<Object> params = new ArrayList<Object>();

		sql.append(" delete from " + (tableName == null ? getTableName(obj.getClass()) : tableName) + " where ");

		Map<String, Field> fieldMap = ReflectUtil.getFields(obj.getClass());

		Object idValue = null;
		
		for (Field field : fieldMap.values()) {

			Method getter = ReflectUtil.getGetter(obj, field.getName());

			Object value = getter.invoke(obj);

			String columnName = getColumnName(field);

			ID idAnno = field.getAnnotation(ID.class);
			if (idAnno != null) {
				sql.append(columnName).append(" = ? ");
				params.add(value);
				idValue = value;
				break;
			}
		}
		
		if(idValue == null) {
			throw new NoIdAnnotationFoundException(obj.getClass());
		}

		SqlParamsPairs sqlParamsPairs = new SqlParamsPairs(sql.toString(), params.toArray());
		logger.debug(sqlParamsPairs.toString());

		return sqlParamsPairs;
	}
	
	
	public static <T> SqlParamsPairs createSelectById(Class<T> clazz, String tableName,Object idValue) {
		StringBuffer sql = new StringBuffer();

		sql.append(" select * from " + (tableName == null ? getTableName(clazz) : tableName) + " where ");

		Map<String, Field> fieldMap = ReflectUtil.getFields(clazz);

		for (Field field : fieldMap.values()) {
			ID idAnno = field.getAnnotation(ID.class);
			if (idAnno != null) {
				String columnName = getColumnName(field);
				sql.append(columnName).append(" = ? ");
				break;
			}
		}

		SqlParamsPairs sqlParamsPairs = new SqlParamsPairs(sql.toString(), new Object[] {idValue});
		logger.debug(sqlParamsPairs.toString());

		return sqlParamsPairs;
	}
	
    public static String getInStr(String[] values){

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if(i <  (values.length -1)){
                sb.append("'" + values[i] + "', ");
            }else{
                sb.append("'" + values[i] + "'");
            }
                
        }
        return sb.toString();
    }
    
    public static <T> String getInStr(List<T> values){

        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < values.size(); i++) {
            if(i <  (values.size() -1)){
                sb.append("'" + values.get(i) + "', ");
            }else{
                sb.append("'" + values.get(i) + "'");
            }
        }

        return sb.toString();
    }
}
