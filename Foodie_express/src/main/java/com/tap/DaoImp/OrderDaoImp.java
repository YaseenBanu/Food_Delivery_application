package com.tap.DaoImp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.tap.Dao.OrderDao;
import com.tap.utilities.DBconnection;
import com.tap.model.Order;

public class OrderDaoImp implements OrderDao {

	private static final String INSERT_QUERY =
			"INSERT INTO orders(userId, restaurantId, orderDate, totalAmount, status, paymentMethod) VALUES(?,?,?,?,?,?)";

	private static final String SELECT_ORDER_QUERY =
			"SELECT * FROM orders WHERE orderId=?";

	private static final String SELECT_ALL_QUERY =
			"SELECT * FROM orders";

	// NEW — needed for order history, filtered to just the logged-in user.
	private static final String SELECT_BY_USER_QUERY =
			"SELECT * FROM orders WHERE userId=? ORDER BY orderDate DESC";

	private static final String UPDATE_QUERY =
			"UPDATE orders SET userId=?, restaurantId=?, orderDate=?, totalAmount=?, status=?, paymentMethod=? WHERE orderId=?";

	private static final String DELETE_QUERY =
			"DELETE FROM orders WHERE orderId=?";

	Connection con = null;

	@Override
	public int addOrder(Order order) {
		// FIX: was `void addOrder(...)` — couldn't get the new orderId back,
		// which PlaceOrderServlet needs to attach OrderItems to this order.
		// Remember to also change OrderDao interface: void -> int.

		con = DBconnection.getConnection();
		int generatedOrderId = 0;

		try {
			PreparedStatement pst = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);

			pst.setInt(1, order.getUserId());
			pst.setInt(2, order.getRestaurantId());
			pst.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			pst.setDouble(4, order.getTotalAmount());
			pst.setString(5, order.getStatus());
			pst.setString(6, order.getPaymentMethod());

			pst.executeUpdate();

			ResultSet keys = pst.getGeneratedKeys();
			if (keys.next()) {
				generatedOrderId = keys.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return generatedOrderId;
	}

	@Override
	public Order getOrder(int orderId) {

		Order order = null;

		con = DBconnection.getConnection();

		try {
			PreparedStatement pst = con.prepareStatement(SELECT_ORDER_QUERY);

			pst.setInt(1, orderId);

			ResultSet res = pst.executeQuery();

			while (res.next()) {

				int id = res.getInt("orderId");
				int userId = res.getInt("userId");
				int restaurantId = res.getInt("restaurantId");
				Timestamp orderDate = res.getTimestamp("orderDate");
				double totalAmount = res.getDouble("totalAmount");
				String status = res.getString("status");
				String paymentMethod = res.getString("paymentMethod");

				order = new Order(id, userId, restaurantId, orderDate, totalAmount, status, paymentMethod);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return order;
	}

	@Override
	public void updateOrder(Order order) {

		con = DBconnection.getConnection();

		try {
			PreparedStatement pst = con.prepareStatement(UPDATE_QUERY);

			pst.setInt(1, order.getUserId());
			pst.setInt(2, order.getRestaurantId());
			pst.setTimestamp(3, order.getOrderDate());
			pst.setDouble(4, order.getTotalAmount());
			pst.setString(5, order.getStatus());
			pst.setString(6, order.getPaymentMethod());
			pst.setInt(7, order.getOrderId());

			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteOrder(int orderId) {

		con = DBconnection.getConnection();

		try {
			PreparedStatement pst = con.prepareStatement(DELETE_QUERY);

			pst.setInt(1, orderId);

			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Order> getAllOrders() {

		List<Order> list = new ArrayList<>();

		Connection con = DBconnection.getConnection();

		try {

			Statement stm = con.createStatement();

			ResultSet res = stm.executeQuery(SELECT_ALL_QUERY);

			while (res.next()) {

				int id = res.getInt("orderId");
				int userId = res.getInt("userId");
				int restaurantId = res.getInt("restaurantId");
				Timestamp orderDate = res.getTimestamp("orderDate");
				double totalAmount = res.getDouble("totalAmount");
				String status = res.getString("status");
				String paymentMethod = res.getString("paymentMethod");

				Order order = new Order(id, userId, restaurantId, orderDate, totalAmount, status, paymentMethod);

				list.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// Used by orders.jsp (order history) to show only this user's orders.
	public List<Order> getOrdersByUser(int userId) {

		List<Order> list = new ArrayList<>();

		con = DBconnection.getConnection();

		try {
			PreparedStatement pst = con.prepareStatement(SELECT_BY_USER_QUERY);
			pst.setInt(1, userId);

			ResultSet res = pst.executeQuery();

			while (res.next()) {

				int id = res.getInt("orderId");
				int restaurantId = res.getInt("restaurantId");
				Timestamp orderDate = res.getTimestamp("orderDate");
				double totalAmount = res.getDouble("totalAmount");
				String status = res.getString("status");
				String paymentMethod = res.getString("paymentMethod");

				Order order = new Order(id, userId, restaurantId, orderDate, totalAmount, status, paymentMethod);
				list.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// NEW — used by AdminDashboardServlet to show every order placed for
	// this admin's restaurant. Add
	// `List<Order> getOrdersByRestaurant(int restaurantId);` to OrderDao.
	public List<Order> getOrdersByRestaurant(int restaurantId) {

		List<Order> list = new ArrayList<>();
		String query = "SELECT * FROM orders WHERE restaurantId=? ORDER BY orderDate DESC";

		con = DBconnection.getConnection();

		try {
			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, restaurantId);

			ResultSet res = pst.executeQuery();

			while (res.next()) {

				int id = res.getInt("orderId");
				int userId = res.getInt("userId");
				Timestamp orderDate = res.getTimestamp("orderDate");
				double totalAmount = res.getDouble("totalAmount");
				String status = res.getString("status");
				String paymentMethod = res.getString("paymentMethod");

				Order order = new Order(id, userId, restaurantId, orderDate, totalAmount, status, paymentMethod);
				list.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// Lightweight status-only update, so callers don't need to reconstruct
	// a full Order object just to change one field.
	public void updateOrderStatus(int orderId, String status) {

		String query = "UPDATE orders SET status=? WHERE orderId=?";

		con = DBconnection.getConnection();

		try {
			PreparedStatement pst = con.prepareStatement(query);
			pst.setString(1, status);
			pst.setInt(2, orderId);
			pst.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// NEW — used by DeliveryDashboardServlet. Shows orders across ALL
	// restaurants that are ready for a delivery agent to act on — orders
	// still "Placed" haven't been confirmed by the restaurant yet.
	// Add `List<Order> getActiveOrdersForDelivery();` to OrderDao.
	public List<Order> getActiveOrdersForDelivery() {

		List<Order> list = new ArrayList<>();
		String query = "SELECT * FROM orders WHERE status IN ('Preparing','Out for Delivery') ORDER BY orderDate ASC";

		con = DBconnection.getConnection();

		try {
			Statement stm = con.createStatement();
			ResultSet res = stm.executeQuery(query);

			while (res.next()) {

				int id = res.getInt("orderId");
				int userId = res.getInt("userId");
				int restaurantId = res.getInt("restaurantId");
				Timestamp orderDate = res.getTimestamp("orderDate");
				double totalAmount = res.getDouble("totalAmount");
				String status = res.getString("status");
				String paymentMethod = res.getString("paymentMethod");

				Order order = new Order(id, userId, restaurantId, orderDate, totalAmount, status, paymentMethod);
				list.add(order);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}