package com.tap.model;

public class Menu {
	
	private int menuId;
	private int restaurantId;
	private String ItemName;
	private String description;
	private float price;
	private boolean isAvailable;
	private String category;
	private String imagePath;
	
	public Menu() {
		super();
	}

	public Menu(int menuId, int restaurantId, String itemName, String description, float price, boolean isAvailable,
			String category, String imagePath) {
		super();
		this.menuId = menuId;
		this.restaurantId = restaurantId;
		ItemName = itemName;
		this.description = description;
		this.price = price;
		this.isAvailable = isAvailable;
		this.category = category;
		this.imagePath=imagePath;
	}

	public Menu(int restaurantId, String itemName, String description, float price, boolean isAvailable,
			String category) {
		super();
		this.restaurantId = restaurantId;
		ItemName = itemName;
		this.description = description;
		this.price = price;
		this.isAvailable = isAvailable;
		this.category = category;
		
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public int getMenuId() {
		return menuId;
	}

	public void setMenuId(int menuId) {
		this.menuId = menuId;
	}

	public int getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(int restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Menu [menuId=" + menuId + ", restaurantId=" + restaurantId + ", ItemName=" + ItemName + ", description="
				+ description + ", price=" + price + ", isAvailable=" + isAvailable + ", category=" + category + "]";
	}
	
	
	

}
