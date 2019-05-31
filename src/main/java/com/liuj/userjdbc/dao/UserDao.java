package com.liuj.userjdbc.dao;

import com.liuj.userjdbc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2019/5/31
 */
@Repository
public class UserDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public int insert(User user) {
        String sql = "INSERT INTO USER (ID, NAME, AGE) VALUES (:id, :name, :age)";
        MapSqlParameterSource ps = new MapSqlParameterSource();
        ps.addValue("id", user.getId());
        ps.addValue("name", user.getName());
        ps.addValue("age", user.getAge());

        return jdbcTemplate.update(sql, ps);
    }

    public void batchInsert(List<User> users) {
        String sql = "INSERT INTO USER(ID, NAME, AGE) VALUES (:id, :name, :age)";
        SqlParameterSource[] batchValues = SqlParameterSourceUtils.createBatch(users.toArray());

        jdbcTemplate.batchUpdate(sql, batchValues);
    }


    public User getById(String id) {
        String sql = "SELECT ID, NAME, AGE FROM USER WHERE ID = :id";
        MapSqlParameterSource ps = new MapSqlParameterSource();
        ps.addValue("id", id);
        RowMapper<User> rm = BeanPropertyRowMapper.newInstance(User.class);
        return jdbcTemplate.queryForObject(sql, ps, rm);
    }

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

    public int update(User user) {
        String sql = "UPDATE USER SET NAME = :name,AGE = :age WHERE ID = :id;";
        SqlParameterSource ps = new BeanPropertySqlParameterSource(user);
        return jdbcTemplate.update(sql, ps);
    }

    public int deleteById(String id) {
        MapSqlParameterSource ps = new MapSqlParameterSource();
        String sql = "DELETE FROM USER WHERE ID = :id";
        ps.addValue("id", id);
        return jdbcTemplate.update(sql, ps);
    }

    public void batchUpdate(List<User> users) {
        String sql = "UPDATE USER SET NAME = :name,AGE = :age WHERE id = :id;";
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(users.toArray());
        jdbcTemplate.batchUpdate(sql, batch);
    }
}
