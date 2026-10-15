package com.tap.DaoImp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.Dao.RestaurantDao;
import com.tap.utilities.DBconnection;
import com.tap.model.Restaurant;

public class RestaurantDaoImp implements RestaurantDao {

    private static final String INSERT_QUERY =
            "INSERT INTO restaurant " +
            "(restaurantName,cuisineType,deliveryTime,address,adminUserId,rating,isActive,imagePath) " +
            "VALUES (?,?,?,?,?,?,?,?)";

    private static final String SELECT_QUERY =
            "SELECT * FROM restaurant WHERE restaurantId=?";

    private static final String SELECT_ALL_QUERY =
            "SELECT * FROM restaurant";

    // NEW — needed so a logged-in Admin can find "their" restaurant.
    private static final String SELECT_BY_ADMIN_QUERY =
            "SELECT * FROM restaurant WHERE adminUserId=?";

    private static final String UPDATE_QUERY =
            "UPDATE restaurant SET " +
            "restaurantName=?, " +
            "cuisineType=?, " +
            "deliveryTime=?, " +
            "address=?, " +
            "adminUserId=?, " +
            "rating=?, " +
            "isActive=?, " +
            "imagePath=? " +
            "WHERE restaurantId=?";

    private static final String DELETE_QUERY =
            "DELETE FROM restaurant WHERE restaurantId=?";



    @Override
    public void addRestaurant(Restaurant restaurant) {

        try (
                Connection con = DBconnection.getConnection();
                PreparedStatement pst = con.prepareStatement(INSERT_QUERY)
        ) {

            pst.setString(1, restaurant.getRestaurantName());
            pst.setString(2, restaurant.getCuisineType());
            pst.setInt(3, restaurant.getDeliveryTime());
            pst.setString(4, restaurant.getAddress());
            pst.setInt(5, restaurant.getAdminUserId());
            pst.setFloat(6, restaurant.getRating());
            pst.setBoolean(7, restaurant.isActive());
            pst.setString(8, restaurant.getImagePath());

            pst.executeUpdate();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public Restaurant getRestaurant(int restaurantId) {

        Restaurant restaurant = null;

        try (

                Connection con = DBconnection.getConnection();
                PreparedStatement pst = con.prepareStatement(SELECT_QUERY)

        ) {

            pst.setInt(1, restaurantId);

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {

                restaurant = new Restaurant(

                        rs.getInt("restaurantId"),
                        rs.getString("restaurantName"),
                        rs.getString("cuisineType"),
                        rs.getInt("deliveryTime"),
                        rs.getString("address"),
                        rs.getInt("adminUserId"),
                        rs.getFloat("rating"),
                        rs.getBoolean("isActive"),
                        rs.getString("imagePath")

                );

            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return restaurant;

    }



    @Override
    public List<Restaurant> getAllRestaurant() {

        List<Restaurant> restaurantList = new ArrayList<>();

        try (

                Connection con = DBconnection.getConnection();
                Statement stmt = con.createStatement()

        ) {

            ResultSet rs = stmt.executeQuery(SELECT_ALL_QUERY);

            while(rs.next()) {

                Restaurant restaurant = new Restaurant(

                        rs.getInt("restaurantId"),
                        rs.getString("restaurantName"),
                        rs.getString("cuisineType"),
                        rs.getInt("deliveryTime"),
                        rs.getString("address"),
                        rs.getInt("adminUserId"),
                        rs.getFloat("rating"),
                        rs.getBoolean("isActive"),
                        rs.getString("imagePath")

                );

                restaurantList.add(restaurant);

            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return restaurantList;

    }



    @Override
    public void updateRestaurant(Restaurant restaurant) {

        try (

                Connection con = DBconnection.getConnection();
                PreparedStatement pst = con.prepareStatement(UPDATE_QUERY)

        ) {

            pst.setString(1, restaurant.getRestaurantName());
            pst.setString(2, restaurant.getCuisineType());
            pst.setInt(3, restaurant.getDeliveryTime());
            pst.setString(4, restaurant.getAddress());
            pst.setInt(5, restaurant.getAdminUserId());
            pst.setFloat(6, restaurant.getRating());
            pst.setBoolean(7, restaurant.isActive());
            pst.setString(8, restaurant.getImagePath());

            pst.setInt(9, restaurant.getRestaurantId());

            pst.executeUpdate();

        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void deleteRestaurant(int restaurantId) {

        try (

                Connection con = DBconnection.getConnection();
                PreparedStatement pst = con.prepareStatement(DELETE_QUERY)

        ) {

            pst.setInt(1, restaurantId);

            pst.executeUpdate();

        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    // NEW — used by AdminDashboardServlet to find the restaurant this
    // logged-in Admin owns. Add
    // `Restaurant getRestaurantByAdminId(int adminUserId);` to RestaurantDao.
    public Restaurant getRestaurantByAdminId(int adminUserId) {

        Restaurant restaurant = null;

        try (
                Connection con = DBconnection.getConnection();
                PreparedStatement pst = con.prepareStatement(SELECT_BY_ADMIN_QUERY)
        ) {

            pst.setInt(1, adminUserId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                restaurant = new Restaurant(
                        rs.getInt("restaurantId"),
                        rs.getString("restaurantName"),
                        rs.getString("cuisineType"),
                        rs.getInt("deliveryTime"),
                        rs.getString("address"),
                        rs.getInt("adminUserId"),
                        rs.getFloat("rating"),
                        rs.getBoolean("isActive"),
                        rs.getString("imagePath")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return restaurant;
    }

}