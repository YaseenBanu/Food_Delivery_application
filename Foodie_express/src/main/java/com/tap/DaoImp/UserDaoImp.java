package com.tap.DaoImp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.tap.Dao.UserDao;
import com.tap.utilities.DBconnection;
import com.tap.model.User;

public class UserDaoImp implements UserDao{
	
	static Scanner scan=new Scanner(System.in);
	
	private static final String SELECT_USERQUERY="select * from user where userId=?";
	private static final String SELECT_QUERY = "select * from user";
	private static final String INSERT_QUERY="insert into "+
	"user(userName, password, email, address, role, CreateDate, LastLoginDate ) values (? ,?, ?, ?, ?, ?,?)";
	private static final String UPDATE_QUERY="update user set userName=? ,password=?, email=?, address=?, "
		+ " lastLoginDate=?  where userId=?";
	private static final String DELETE_QUERY="DELETE FROM user Where userid=?";
	private static final String GETUSER_QUERY="SELECT * FROM user Where email=?";
	
	public static Connection con;
	 
		@Override
		public int addUser(User user) {

		    try {

		        con = DBconnection.getConnection();

		        PreparedStatement pstm = con.prepareStatement(INSERT_QUERY);
  
		        pstm.setString(1, user.getUserName());
		        pstm.setString(2, user.getPassword());
		        pstm.setString(3, user.getEmail());
		        pstm.setString(4, user.getAddress());
		        pstm.setString(5, user.getRole()); 
		        pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
		        pstm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

		        return pstm.executeUpdate();
		            
		    } catch (SQLException e) {

		    	e.printStackTrace();
		    }
		    return 0;
		}
		
	@Override
	public User getUser(int userId) {
		User user=null;

		con=DBconnection.getConnection();
		try {
			PreparedStatement pst= con.prepareStatement(SELECT_USERQUERY);
			
			pst.setInt(1, userId);
			
			ResultSet res= pst.executeQuery();
			
			while(res.next()) {
				
				int id=res.getInt("userId");
				String name=res.getString("userName");
				String password=res.getString("password");
				String email=res.getString("email");
				String address=res.getString("address");
				String role=res.getString("role");
				Timestamp createDate=res.getTimestamp("createDate");
				Timestamp lastLoginDate=res.getTimestamp("lastLoginDate");
				 
				 user =new User(id,name,password,email,address,role,createDate,lastLoginDate);
			}
			
//			System.out.print(res.getInt(userId));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}

	@Override
	public void updateUser(User user) {
		
		con=DBconnection.getConnection();
		
		try {
			PreparedStatement pst= con.prepareStatement(UPDATE_QUERY);
			pst.setString(1, user.getUserName());
			pst.setString(2, user.getPassword());
			pst.setString(3, user.getEmail());
			pst.setString(4,user.getAddress() );
			pst.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pst.setInt(6, user.getUserId());
			
			pst.executeUpdate();
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deleteUser(int userId) {
		
		con=DBconnection.getConnection();
		try {
			PreparedStatement pst=con.prepareStatement(DELETE_QUERY);
			
			pst.setInt(1, userId);
			pst.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public List<User> getAllUser() {

		List<User> list=new ArrayList<User>();
		con=DBconnection.getConnection();
		try {
			Statement stm=con.createStatement();
			ResultSet res=stm.executeQuery(SELECT_QUERY);
			
			while(res.next()) {
				int id=res.getInt("userId");
				String name=res.getString("userName");
				String password=res.getString("password");
				String email=res.getString("email");
				String address=res.getString("address");
				String role=res.getString("role");
				Timestamp createDate=res.getTimestamp("createDate");
				Timestamp lastLoginDate=res.getTimestamp("lastLoginDate");
				 
				User user =new User(id,name,password,email,address,role,createDate,lastLoginDate);
				list.add(user);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public User getUserByMail(String mail) {
		User user=null;
		con=DBconnection.getConnection();
		try {
			PreparedStatement pst=con.prepareStatement(GETUSER_QUERY);
			pst.setString(1, mail);
			ResultSet res=pst.executeQuery();
			
			while(res.next()) {
				int id=res.getInt("userId");
				String name=res.getString("userName");
				String password=res.getString("password");
				String email=res.getString("email");
				String address=res.getString("address");
				String role=res.getString("role");
				Timestamp createDate=res.getTimestamp("createDate");
				Timestamp lastLoginDate=res.getTimestamp("lastLoginDate");
				 user =new User(id,name,password,email,address,role,createDate,lastLoginDate);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}

}
