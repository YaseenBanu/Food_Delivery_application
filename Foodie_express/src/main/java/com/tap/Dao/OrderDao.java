package com.tap.Dao;

import java.util.List;

import com.tap.model.Order;

public interface OrderDao {

	int addOrder(Order order);   // was: void addOrder(Order order);
	Order getOrder(int orderId);
	void updateOrder(Order order);
	void deleteOrder(int orderId);
    List<Order> getAllOrders();
    List<Order> getOrdersByUser(int userId);
    List<Order> getOrdersByRestaurant(int restaurantId);
    void updateOrderStatus(int orderId, String status);
    List<Order> getActiveOrdersForDelivery();
}
