package com.liuj.userjdbc.dao;

import com.liuj.userjdbc.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author hgvgh
 * @version 1.0
 * @description
 * @date 2019/5/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void insert() {
        User user = new User();
        user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        user.setName("山姆");
        user.setAge(26);
        int count = userDao.insert(user);
        System.out.println(count);
    }

    @Test
    public void batchInsert() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        user1.setName("琼恩");
        user1.setAge(27);

        User user2 = new User();
        user2.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        user2.setName("罗柏");
        user2.setAge(28);

        users.add(user1);
        users.add(user2);

        userDao.batchInsert(users);
    }

    @Test
    public void getById() {
        User byId = userDao.getById("66d1984301c24c2e93ad98a8de63fec3");
        System.out.println(byId);
    }

    @Test
    public void queryList() {
        User query = new User();
        query.setName("恩");
        List<User> users = userDao.queryList(query);
        for (User us : users) {
            System.out.println(us);
        }
    }

    @Test
    public void count() {
        User query = new User();
        query.setName("恩");
        System.out.println(userDao.count(query));
    }

    @Test
    public void update() {
        User user = new User();
        user.setId("66d1984301c24c2e93ad98a8de63fec3");
        user.setName("哈哈哈");
        user.setAge(100);
        System.out.println(userDao.update(user));
    }

    @Test
    public void delete() {
        System.out.println(userDao.deleteById("66d1984301c24c2e93ad98a8de63fec3"));
    }

    @Test
    public void batchUpdate() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId("557729bb2dec4f7abd8ae5afed2bd873");
        user.setName("fuck");
        user.setAge(200);
        User user2 = new User();
        user2.setId("28409441b0e2446ab541f5f107375bec");
        user2.setName("heihei");
        user2.setAge(300);
        users.add(user);
        users.add(user2);
        userDao.batchUpdate(users);
    }
}