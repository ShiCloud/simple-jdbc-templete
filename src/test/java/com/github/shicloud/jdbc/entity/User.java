package com.github.shicloud.jdbc.entity;

import java.util.Date;

import com.github.shicloud.jdbc.annotation.ID;
import com.github.shicloud.jdbc.annotation.Prefix;
import com.github.shicloud.jdbc.annotation.Suffix;

@Prefix
@Suffix
public class User {
	
	@ID(value = ID.TYPE.AUTO)
	private Integer id;
	
	private Integer age;
	
	private String login;
	
	private Date createTime;
	
	private Byte isDel;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Byte getIsDel() {
		return isDel;
	}
	public void setIsDel(Byte isDel) {
		this.isDel = isDel;
	}
	
}
