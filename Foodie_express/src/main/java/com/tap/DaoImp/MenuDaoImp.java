package com.tap.DaoImp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.Dao.MenuDao;
import com.tap.utilities.DBconnection;
import com.tap.model.Menu;

public class MenuDaoImp implements MenuDao {

    // FIX: both queries now include imagePath — previously it was silently
    // dropped on every insert/update, so admin-added items never got a photo.
    private static final String INSERT_QUERY =
            "INSERT INTO menu(restaurantId,itemName,description,price,isAvailable,category,imagePath) VALUES(?,?,?,?,?,?,?)";

    private static final String SELECT_MENU_QUERY =
            "SELECT * FROM menu WHERE menuId=?";

    private static final String SELECT_MENU_BY_RESID =
    		"SELECT * FROM menu WHERE restaurantId=?";

    private static final String SELECT_ALL_QUERY =
            "SELECT * FROM menu";

    private static final String UPDATE_QUERY =
            "UPDATE menu SET restaurantId=?, itemName=?, description=?, price=?, isAvailable=?, category=?, imagePath=? WHERE menuId=?";

    private static final String DELETE_QUERY =
            "DELETE FROM menu WHERE menuId=?";

    Connection con = null;

    @Override
    public void addMenu(Menu menu) {

        con = DBconnection.getConnection();

        try {
            PreparedStatement pst = con.prepareStatement(INSERT_QUERY);

            pst.setInt(1, menu.getRestaurantId());
            pst.setString(2, menu.getItemName());
            pst.setString(3, menu.getDescription());
            pst.setFloat(4, menu.getPrice());
            pst.setBoolean(5, menu.isAvailable());
            pst.setString(6, menu.getCategory());
            pst.setString(7, menu.getImagePath());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Menu getMenu(int menuId) {

        Menu menu = null;

        con = DBconnection.getConnection();

        try {
            PreparedStatement pst = con.prepareStatement(SELECT_MENU_QUERY);

            pst.setInt(1, menuId);

            ResultSet res = pst.executeQuery();

            while (res.next()) {

                int id = res.getInt("menuId");
                int restaurantId = res.getInt("restaurantId");
                String itemName = res.getString("itemName");
                String description = res.getString("description");
                float price = res.getFloat("price");
                boolean isAvailable = res.getBoolean("isAvailable");
                String category = res.getString("category");
                String imagePath=res.getString("imagePath");

                menu = new Menu(id, restaurantId, itemName, description, price, isAvailable, category,imagePath);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return menu;
    }

    @Override
    public void updateMenu(Menu menu) {

        con = DBconnection.getConnection();

        try {

            PreparedStatement pst = con.prepareStatement(UPDATE_QUERY);

            pst.setInt(1, menu.getRestaurantId());
            pst.setString(2, menu.getItemName());
            pst.setString(3, menu.getDescription());
            pst.setFloat(4, menu.getPrice());
            pst.setBoolean(5, menu.isAvailable());
            pst.setString(6, menu.getCategory());
            pst.setString(7, menu.getImagePath());
            pst.setInt(8, menu.getMenuId());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMenu(int menuId) {

        con = DBconnection.getConnection();

        try {

            PreparedStatement pst = con.prepareStatement(DELETE_QUERY);

            pst.setInt(1, menuId);

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Menu> getAllMenu() {

        List<Menu> list = new ArrayList<>();

        con = DBconnection.getConnection();

        try {

            Statement stm = con.createStatement();

            ResultSet res = stm.executeQuery(SELECT_ALL_QUERY);

            while (res.next()) {

                int id = res.getInt("menuId");
                int restaurantId = res.getInt("restaurantId");
                String itemName = res.getString("itemName");
                String description = res.getString("description");
                float price = res.getFloat("price");
                boolean isAvailable = res.getBoolean("isAvailable");
                String category = res.getString("category");
                String imagePath=res.getString("imagePath");

                Menu menu = new Menu(id, restaurantId, itemName,description, price, isAvailable, category,imagePath);

                list.add(menu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

	@Override
	public List<Menu> getMenuByResId(int restaurantId) {

		    List<Menu> list = new ArrayList<>();

		    try {

		        con = DBconnection.getConnection();

		        PreparedStatement pst =
		                con.prepareStatement(SELECT_MENU_BY_RESID);

		        pst.setInt(1, restaurantId);

		        ResultSet res = pst.executeQuery();

		        while(res.next()) {

		            Menu menu = new Menu(res.getInt("menuId"), res.getInt("restaurantId"), res.getString("itemName"),
		                    res.getString("description"),res.getFloat("price"),res.getBoolean("isAvailable"),
		                    res.getString("category"),res.getString("imagePath")
		            );

		            list.add(menu);
		        }

		    } catch (SQLException e) {
		        e.printStackTrace();
		    }

		    return list;
	}

}