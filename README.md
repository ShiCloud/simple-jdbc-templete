# 这是一个简单的ORM工具，基于Spring jdbcTemplete，可以方便的根据对象生成相应的CRUD SQL语句。结合springboot使用可以大量的减少代码量。

```java
# application.yml
jdbc: 
  template: 
    prefix: t_
    suffix: _test
    insertGetId: true

# User 实体类
@Prefix//读取配置文件加入表名前缀
@Suffix//读取配置文件加入表名后缀
public class User //对象名自动生成表名，或者设置@Table注解指定表名 @Table(name="t_user")
	@ID(value = ID.TYPE.AUTO) //@ID注解 可以自增 或者 手动输入
	private Integer id;

	private Integer age;

	private String login;

	private Date createTime;

	private Byte isDel;

# 测试代码
	User u = new User();//新建一个对象,id自增,根据配置文件 前缀 t_ 后缀 _test
	u.setAge(20);
	u.setLogin("user1");
	u.setCreateTime(new Date());
	u.setIsDel(Byte.valueOf("0"));
	jtt.insert(u);//根据配置文件insertGetId自动获取自增id
	System.out.println(u.getId());
	u.setIsDel(Byte.valueOf("1"));
	jtt.updateById(u);
	List<User> list = jtt.list("select * from t_user_test where is_del = ?", 
			new Object[]{Byte.valueOf("1")}, User.class);//根据条件查询结果

```
