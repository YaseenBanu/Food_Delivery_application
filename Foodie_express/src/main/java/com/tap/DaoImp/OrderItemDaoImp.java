package com.tap.DaoImp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tap.Dao.OrderItemDao;
import com.tap.utilities.DBconnection;
import com.tap.model.OrderItem;

public class OrderItemDaoImp implements OrderItemDao {

    private static final String INSERT_QUERY =
            "INSERT INTO orderitem(orderId, menuId, quantity, itemTotal) VALUES(?,?,?,?)";

    private static final String SELECT_ORDERITEM_QUERY =
            "SELECT * FROM orderitem WHERE orderItemId=?";

    private static final String SELECT_ALL_QUERY =
            "SELECT * FROM orderitem";

    // NEW — needed by orders.jsp to show every item within one order.
    private static final String SELECT_BY_ORDER_QUERY =
            "SELECT * FROM orderitem WHERE orderId=?";

    private static final String UPDATE_QUERY =
            "UPDATE orderitem SET orderId=?, menuId=?, quantity=?, itemTotal=? WHERE orderItemId=?";

    private static final String DELETE_QUERY =
            "DELETE FROM orderitem WHERE orderItemId=?";

    Connection con = null;

    @Override
    public void addOrderItem(OrderItem orderItem) {

        con = DBconnection.getConnection();

        try {
            PreparedStatement pst = con.prepareStatement(INSERT_QUERY);

            pst.setInt(1, orderItem.getOrderId());
            pst.setInt(2, orderItem.getMenuId());
            pst.setInt(3, orderItem.getQuantity());
            pst.setDouble(4, orderItem.getItemTotal());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public OrderItem getOrderItem(int orderItemId) {

        OrderItem orderItem = null;

        con = DBconnection.getConnection();

        try {

            PreparedStatement pst =
                    con.prepareStatement(SELECT_ORDERITEM_QUERY);

            pst.setInt(1, orderItemId);

            ResultSet res = pst.executeQuery();

            while (res.next()) {

                int id = res.getInt("orderItemId");
                int orderId = res.getInt("orderId");
                int menuId = res.getInt("menuId");
                int quantity = res.getInt("quantity");
                double itemTotal = res.getDouble("itemTotal");

                orderItem = new OrderItem(id,orderId,menuId,quantity,itemTotal);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderItem;
    }

    @Override
    public void updateOrderItem(OrderItem orderItem) {

        con = DBconnection.getConnection();

        try {

            PreparedStatement pst =
                    con.prepareStatement(UPDATE_QUERY);

            pst.setInt(1, orderItem.getOrderId());
            pst.setInt(2, orderItem.getMenuId());
            pst.setInt(3, orderItem.getQuantity());
            pst.setDouble(4, orderItem.getItemTotal());
            pst.setInt(5, orderItem.getOrderItemId());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrderItem(int orderItemId) {

        con = DBconnection.getConnection();

        try {

            PreparedStatement pst =
                    con.prepareStatement(DELETE_QUERY);

            pst.setInt(1, orderItemId);

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OrderItem> getAllOrderItems() {

        List<OrderItem> list = new ArrayList<>();

        con = DBconnection.getConnection();

        try {

            Statement stm = con.createStatement();

            ResultSet res = stm.executeQuery(SELECT_ALL_QUERY);

            while (res.next()) {

                int id = res.getInt("orderItemId");
                int orderId = res.getInt("orderId");
                int menuId = res.getInt("menuId");
                int quantity = res.getInt("quantity");
                double itemTotal = res.getDouble("itemTotal");

                OrderItem orderItem = new OrderItem(id,orderId,menuId,quantity,itemTotal );

                list.add(orderItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // NEW — used by orders.jsp to list every item within one past order.
    // Add `List<OrderItem> getOrderItemsByOrderId(int orderId);` to your
    // OrderItemDao interface too.
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {

        List<OrderItem> list = new ArrayList<>();

        con = DBconnection.getConnection();

        try {
            PreparedStatement pst = con.prepareStatement(SELECT_BY_ORDER_QUERY);
            pst.setInt(1, orderId);

            ResultSet res = pst.executeQuery();

            while (res.next()) {

                int id = res.getInt("orderItemId");
                int menuId = res.getInt("menuId");
                int quantity = res.getInt("quantity");
                double itemTotal = res.getDouble("itemTotal");

                OrderItem orderItem = new OrderItem(id, orderId, menuId, quantity, itemTotal);
                list.add(orderItem);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}