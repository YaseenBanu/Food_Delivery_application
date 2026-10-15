package com.tap.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {

	// FIX: was `static` — that made ALL users share one cart across the
	// entire app. Now it's a normal instance field, so each session's
	// Cart object (stored via session.setAttribute("cart", cart)) has
	// its own private map.
	private Map<Integer, CartItems> items;

	public Cart() {
		items = new HashMap<Integer, CartItems>();
	}

	public Map<Integer, CartItems> getItems() {
		return items;
	}

	public void addItem(CartItems cartItem) {
		int menuId = cartItem.getMenuId();

		if (items.containsKey(menuId)) {
			CartItems existingCartItem = items.get(menuId);
			existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
		} else {
			items.put(menuId, cartItem);
		}
	}

	public void updateItem(int itemId, int quantity) {
		if (items.containsKey(itemId)) {
			if (quantity > 0) {
				items.get(itemId).setQuantity(quantity);
			} else {
				items.remove(itemId);
			}
		}
	}

	public void removeItem(int itemId) {
		items.remove(itemId);
	}

	// Added: cart.jsp needs these for the order summary — small, useful
	// additions that don't change any of your existing behavior.
	public int getTotalItemCount() {
		int count = 0;
		for (CartItems item : items.values()) {
			count += item.getQuantity();
		}
		return count;
	}

	public double getSubtotal() {
		double subtotal = 0;
		for (CartItems item : items.values()) {
			subtotal += item.getPrice() * item.getQuantity();
		}
		return subtotal;
	}
}