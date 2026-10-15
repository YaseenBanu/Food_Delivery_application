package com.tap.Dao;

import java.util.List;

import com.tap.model.User;

public interface UserDao {
	int addUser(User user);
	User getUser(int userId);
	void updateUser(User user);
	void deleteUser(int userId);
	User getUserByMail(String mail);
    List<User> getAllUser();
	

}
