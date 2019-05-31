# user-jdbc
文章地址：https://blog.csdn.net/qq_34464926/article/details/90720099

# 文件介绍：
- pom.xml：maven依赖管理文件
- application.properties：项目配置文件
- User：用户实体类
- UserDao:用户操作dao类
- UserDaoTest：用户接口单元测试类



JdbcTemplate是Spring用来简化JDBC操作的核心类，有助于与Spring集成，并且避免了过多冗长的JDBC代码。不过在实际使用中，我们通常使用NamedParameterJdbcTemplate代替JdbcTemplate，因为在传入的参数不确定的时候，使用它会是更好的选择。在本文中，我会介绍如何在SpringBoot中使用NamedParameterJdbcTemplate来完成一整个增删改查的流程。
- 环境介绍
  - JDK1.8+
  - SpringBoot：2.1.5.RELEASE
  - MYSQL：8.0.15
- 项目搭建：
  - maven 依赖:主要是Jdbc、mysql-connector、lombok三个，lombok用于省略实体类中的getter/setter方法，看起来更加清爽
```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.15</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.8</version>
            <scope>provided</scope>
        </dependency>
```
  - application.properties配置：
```
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=true&autoReconnect=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.pool-size=30
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

server.port=8885
spring.application.name=user-service
```
有些版本url不添加`serverTimezone=UTC`的话会报错：
```
Caused by: com.mysql.cj.exceptions.InvalidConnectionAttributeException: The server time zone value '
```
此问题为时区问题,在 JDBC 的连接 url 部分加上 serverTimezone=UTC 即可。 
- User实体类：
```
package com.liuj.userjdbc.domain;

import lombok.Data;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2019/5/31
 */
@Data
public class User {

    private String id;

    private String name;

    private Integer age;

}
```
- DAO操作类：UserDao:
```
@Repository
public class UserDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    //其余代码省略
}

```
- 插入数据：
```
    public int insert(User user) {
        String sql = "INSERT INTO USER (ID, NAME, AGE) VALUES (:id, :name, :age)";
        MapSqlParameterSource ps = new MapSqlParameterSource();
        ps.addValue("id", user.getId());
        ps.addValue("name", user.getName());
        ps.addValue("age", user.getAge());

        return jdbcTemplate.update(sql, ps);
    }
```
- 根据ID查询单条数据：
```
public User getById(String id) {
        String sql = "SELECT ID, NAME, AGE FROM USER WHERE ID = :id";
        MapSqlParameterSource ps = new MapSqlParameterSource();
        ps.addValue("id", id);
        RowMapper<User> rm = BeanPropertyRowMapper.newInstance(User.class);
        return jdbcTemplate.queryForObject(sql, ps, rm);
    }
```
- 多条件查询数据集合(带有模糊查询，需要注意的是`LIKE '%' :name '%'`中空格不能少)：
```
public List<User> queryList(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ID, NAME, AGE FROM USER WHERE 1 =1 ");
        MapSqlParameterSource ps = new MapSqlParameterSource();

        if (!StringUtils.isEmpty(user.getId())) {
            sb.append(" AND ID = :id ");
            ps.addValue("id", user.getId());
        }
        if (!StringUtils.isEmpty(user.getName())) {
            sb.append(" AND NAME LIKE '%' :name '%' ");
            ps.addValue("name", user.getName());
        }
        if (!StringUtils.isEmpty(user.getAge())) {
            sb.append(" AND AGE = :age ");
            ps.addValue("age", user.getAge());
        }

        return jdbcTemplate.query(sb.toString(), ps, BeanPropertyRowMapper.newInstance(User.class));
    }
```
- 根据条件查询数据条数：
```
public int count(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(1) FROM USER WHERE 1 =1 ");
        MapSqlParameterSource ps = new MapSqlParameterSource();

        if (!StringUtils.isEmpty(user.getId())) {
            sb.append(" AND ID = :id ");
            ps.addValue("id", user.getId());
        }
        if (!StringUtils.isEmpty(user.getName())) {
            sb.append(" AND NAME LIKE '%' :name '%' ");
            ps.addValue("name", user.getName());
        }
        if (!StringUtils.isEmpty(user.getAge())) {
            sb.append(" AND AGE = :age ");
            ps.addValue("age", user.getAge());
        }

        return jdbcTemplate.queryForObject(sb.toString(), ps, Integer.class).intValue();
    }
```
- 更新数据：
```
public int update(User user) {
        String sql = "UPDATE USER SET NAME = :name,AGE = :age WHERE ID = :id;";
        SqlParameterSource ps = new BeanPropertySqlParameterSource(user);
        return jdbcTemplate.update(sql, ps);
    }
```
- 根据ID删除单条数据：
```
public int deleteById(String id) {
        MapSqlParameterSource ps = new MapSqlParameterSource();
        String sql = "DELETE FROM USER WHERE ID = :id";
        ps.addValue("id", id);
        return jdbcTemplate.update(sql, ps);
    }
```
- 批量插入数据：
```
public void batchInsert(List<User> users) {
        String sql = "INSERT INTO USER(ID, NAME, AGE) VALUES (:id, :name, :age)";
        SqlParameterSource[] batchValues = SqlParameterSourceUtils.createBatch(users.toArray());

        jdbcTemplate.batchUpdate(sql, batchValues);
    }
```
- 批量更新(删除操作类似):
```
public void batchUpdate(List<User> users) {
        String sql = "UPDATE USER SET NAME = :name,AGE = :age WHERE id = :id;";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(users.toArray());
        jdbcTemplate.batchUpdate(sql, batch);
    }
```


- 附：数据库脚本：
```
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
