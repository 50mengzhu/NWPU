package com.zyw.nwpu.jifen.leancloud;

/**
 * 
 * 商品类
 * 
 * @author Rocket
 * 
 */
public class Product {

	private String productId;// 商品id，用于购买商品
	private String name;
	private String description;
	private String imageUrl;

	private int score;
	private boolean isDiscount;// 是否打折
	private double discount;

	public Product() {
	}

	public boolean isDiscount() {
		return isDiscount;
	}

	public void setIsDiscount(boolean isDiscount) {
		this.isDiscount = isDiscount;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
}
