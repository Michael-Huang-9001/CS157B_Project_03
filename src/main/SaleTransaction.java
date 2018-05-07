package main;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity(name = "sales")
@Table(name = "sales")
public class SaleTransaction {

	@Id
	@Column(name = "sales_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int sales_id;

	@Column(name = "timestamp")
	@CreationTimestamp
	private Timestamp timestamp;

	@Column(name = "product_name")
	private String product_name;

	@Column(name = "quantity")
	private int quantity;

	@Column(name = "unit_cost")
	private double unit_cost;

	@Column(name = "total_cost")
	private double total_cost;
	
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setProductName(String product_name) {
		this.product_name = product_name;
	}

	public String getProductName() {
		return product_name;
	}

	public void setUnitCost(double unit_cost) {
		this.unit_cost = unit_cost;
	}

	public double getUnitCost() {
		return unit_cost;
	}

	public void setTotalCost(double total_cost) {
		this.total_cost = total_cost;
	}

	public double getTotalCost() {
		return total_cost;
	}

	public String toString() {
		return String.format("%-10s %-30s %-10s %-10s %-10s %-20s", sales_id, product_name, quantity,
				Math.round(unit_cost * 100.0) / 100.0, Math.round(total_cost * 100.0) / 100.0, timestamp.toString());
	}

}
