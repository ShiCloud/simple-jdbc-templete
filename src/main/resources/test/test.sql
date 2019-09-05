DROP TABLE IF EXISTS t_user_test;
CREATE TABLE t_user_test (
  id int NOT NULL AUTO_INCREMENT,
  login varchar(100) NOT NULL,
  age int,
  create_time datetime ,
  is_del tinyint(4) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;