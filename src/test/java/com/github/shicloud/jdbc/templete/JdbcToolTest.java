package com.github.shicloud.jdbc.templete;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.shicloud.jdbc.entity.User;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = JdbcToolTest.class)
@SpringBootApplication(scanBasePackages="com.github.shicloud.jdbc")
public class JdbcToolTest {
	
	@Autowired
	JdbcTemplateTool jtt;
	
	@Test
	public void test() throws Exception {
//		User u = new User();//新建一个对象,id自增,根据配置文件 前缀 t_ 后缀 _test
//		u.setAge(20);
//		u.setLogin("user1");
//		u.setCreateTime(new Date());
//		u.setIsDel(Byte.valueOf("0"));
//		
//		jtt.insert(u);//根据配置文件自动获取自增id
//		System.out.println(u.getId());
//		
//		u.setIsDel(Byte.valueOf("1"));
//		jtt.updateById(u);
//		
//		List<User> list = jtt.list("select * from t_user_test where is_del = ?", 
//				new Object[]{Byte.valueOf("1")}, User.class);//根据条件查询结果
//		
//		for (User user : list) {
//			System.out.println(user.getId()+":"+user.getLogin()+":"+user.getCreateTime());
//		}
	}
}
