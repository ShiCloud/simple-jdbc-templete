package com.github.shicloud.jdbc.templete;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.github.shicloud.jdbc.annotation.ID;
import com.github.shicloud.jdbc.utils.IdUtil;
import com.github.shicloud.jdbc.utils.ModelSqlUtil;

@Component
public final class JdbcTemplateTool {
	private static Logger logger = LoggerFactory.getLogger(JdbcTemplateTool.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static Boolean insertGetId = false;
    @Value("${jdbc.template.insertGetId}")
    public void setPrefix(Boolean insertGetId) {
    	JdbcTemplateTool.insertGetId = insertGetId;
    }
	
	private void logError(String sql, Object[] params) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(Object p:params){
			sb.append(p + " | ");
		}
		sb.append("]");
		logger.error("Error SQL: " + sql + " Params: " + sb.toString());
	}
	
	public void insert(Object obj) throws Exception{
		insert(obj,null,insertGetId);
    }
	
	public void insert(Object obj,String tableName) throws Exception{
		insert(obj,tableName,insertGetId);
    }
	
	public int insert(Object obj,String tableName,boolean getId) throws Exception {
    	SqlParamsPairs sqlAndParams = ModelSqlUtil.createInsert(obj, tableName);
    	Field idField = IdUtil.getIdColumn(obj);
    	if(getId && ID.TYPE.AUTO.equals(idField.getAnnotation(ID.class).value())){
    		int update = 0;
    		
    		if(idField!=null){
    			ReturnIdPreparedStatementCreator psc = 
    					new ReturnIdPreparedStatementCreator(sqlAndParams,ModelSqlUtil.getColumnName(idField));
    			KeyHolder keyHolder = new GeneratedKeyHolder();
    			try{
    				update = jdbcTemplate.update(psc, keyHolder);
    			}catch(DataAccessException e){
    				logError(sqlAndParams.getSql(), sqlAndParams.getParams());
    				throw e;
    			}
    			IdUtil.setIdValue(obj,idField,keyHolder);
    		}
    		return update;
    	}else{
	    	return jdbcTemplate.update(sqlAndParams.getSql(), sqlAndParams.getParams());
		}
    }
	
	public int updateById(Object obj) throws Exception {
		return updateById(obj,null,false);
    }
	
	public int updateById(Object obj,String tableName) throws Exception {
    	return updateById(obj,tableName,false);
    }
	
	public int updateByIdWithNull(Object obj,String tableName) throws Exception {
    	return updateById(obj,tableName,true);
    }
	
	public int updateById(Object obj,String tableName,boolean withNull) throws Exception {
    	SqlParamsPairs sqlAndParams = ModelSqlUtil.createUpdateById(obj, tableName,withNull);
    	return jdbcTemplate.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }
	
	public int deleteById(Object obj) throws Exception {
    	SqlParamsPairs sqlAndParams = ModelSqlUtil.createDeleteById(obj, null);
    	return jdbcTemplate.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }
	
	public int deleteById(Object obj,String tableName) throws Exception {
    	SqlParamsPairs sqlAndParams = ModelSqlUtil.createDeleteById(obj, tableName);
    	return jdbcTemplate.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }
	
	public <T> List<T> list(String sql, Object[] params, Class<T> clazz) {
		List<T> list = null;
		if (params == null || params.length == 0) {
			list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(clazz));
		} else {
			list = jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
		}
		
		return list;
	}
	
	public <T> T get(String sql, Object[] params, Class<T> clazz) throws Exception {
		List<T> list = list(sql, params, clazz);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	public <T> T getById(Class<T> clazz,String tableName, Object id) throws Exception {
		SqlParamsPairs sqlAndParams = ModelSqlUtil.createSelectById(clazz, tableName, id);
		return get(sqlAndParams.getSql(), sqlAndParams.getParams(), clazz);
	}
	
	public <T> T getById(Class<T> clazz,Object id) throws Exception {
		SqlParamsPairs sqlAndParams = ModelSqlUtil.createSelectById(clazz, null, id);
		return get(sqlAndParams.getSql(), sqlAndParams.getParams(), clazz);
	}
}
