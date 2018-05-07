package main;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.hibernate.query.Query;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Hibernate {
	private static SessionFactory sf = new Configuration().configure().buildSessionFactory();
	private static Session s;
	private static Scanner in;
	private static String input = "";
	private static final int seed = 0;
	private static Random rng = new Random(seed);
	private static HashMap<String, Double> inventory = new HashMap<String, Double>() {
		{
			put("Hammer", 4.99);
			put("Apple Juice", 2.99);
			put("Nails", 2.99);
			put("Lip Balm", 0.99);
			put("Potato Chips", 1.79);
			put("The Nutshack DVD", 10.99);
			put("Bong Hits 4 Jesus Documentary", 13.99);
			put("Huge 25 ft bong", 54.99);
			put("Dank Maymays", 420.69);
		}
	};

	/**
	 * Runs the hibernate program.
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		try {
			s = sf.openSession();
			// insertSales();
			listSales();
			repl();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (s != null) {
				s.close();
			}
			sf.close();
		}
	}

	/**
	 * A repl console to interact with the back end.
	 */
	public static void repl() {
		in = new Scanner(System.in);
		try {
			while (true) {
				System.out.println("What would you like to do?");
				System.out.println("1: Buy\t\t2: Update\t3: Delete\t4: See all sales");
				System.out.print("> ");
				input = in.nextLine();
				switch (input) {
				case ("1"):
					buy();
					break;
				case ("2"):
					update();
					break;
				case ("3"):
					delete();
					break;
				case ("4"):
					listSales();
					break;
				case ("q"):
					System.out.println("See ya.");
					break;
				default:
					System.out.println("Unrecognized operation. Please try again.");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}
	
	/**
	 * Lists all items in the inventory, NOT IN THE SQL LEVEL.
	 */
	public static void listItems() {
		System.out.println(String.format("%-25s %-10s", "Item name", "Item Price"));
		for (String key : inventory.keySet()) {
			System.out.println(String.format("%-25s %-10s", key, inventory.get(key)));
		}
	}

	/**
	 * Prompts to insert a sale transaction.
	 */
	public static void buy() {
		System.out.println("What would you like to buy? Enter the item name.");
		System.out.println("- Listing all items...");
		listItems();
		System.out.print("> ");
		input = in.nextLine();
		if (inventory.containsKey(input)) {
			String product_name = input;
			System.out.println("How many would you like?");
			System.out.print("> ");
			input = in.nextLine();
			int quantity = 0;
			try {
				quantity = Integer.parseInt(input);
				if (quantity < 1) {
					throw new NumberFormatException();
				}
			} catch (Exception e) {
				System.out.println("Input was not a number or was less than 1.");
				return;
			}
			insertSale(product_name, inventory.get(product_name), quantity);
		} else {
			System.out.println("You entered: " + input);
			System.out.println("Item specified is not in inventory.");
		}
		System.out.println("---");
	}

	/**
	 * Prompts to update the database.
	 */
	public static void update() {
		System.out.println("What would you like to update? Enter the Sales ID to select the update target.");
		listSales();
		System.out.print("> ");
		input = in.nextLine();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			// Input should be the sales_id, which is the primary key.
			SaleTransaction sale = (SaleTransaction) s.load(SaleTransaction.class, Integer.parseInt(input));
			listItems();
			System.out.print("---\nNew product name?\n> ");
			input = in.nextLine();
			if (input.isEmpty()) {
				throw new Exception("The updated product name is empty.");
			}
			sale.setProductName(input);

			System.out.print("New quantity?\n> ");
			input = in.nextLine();
			sale.setQuantity(Integer.parseInt(input));

			System.out.print("New unit cost?\n> ");
			input = in.nextLine();
			sale.setUnitCost(Double.parseDouble(input));
			sale.setTotalCost(sale.getUnitCost() * sale.getQuantity());

			System.out.println("- Attempting to update sale transaction...");
			s.save(sale);
			tx.commit();
		} catch (ObjectNotFoundException e) {
			System.out.println("No sale transaction for Sale ID specified.");
		} catch (Exception e) {
			if (tx != null) {
				System.out.println("-- Rolling back transaction...");
				tx.rollback();
			}
			System.out.println(e.getMessage());
		}
		System.out.println("---");
	}
	
	/**
	 * Prompts to delete a sale from the database.
	 */
	public static void delete() {
		System.out.println("What would you like to delete? Enter the Sales ID to select the deletion target.");
		listSales();
		System.out.print("> ");
		input = in.nextLine();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			// Input should be the sales_id, which is the primary key.
			SaleTransaction sale = (SaleTransaction) s.load(SaleTransaction.class, Integer.parseInt(input));
			System.out.println("Attempting to delete the selected sale transaction...");
			s.delete(sale);
			tx.commit();
		} catch (ObjectNotFoundException e) {
			System.out.println("No sale transaction for Sale ID specified.");
		} catch (Exception e) {
			if (tx != null) {
				System.out.println("-- Rolling back transaction...");
				tx.rollback();
			}
			System.out.println(e.getMessage());
		}
		System.out.println("---");
	}

	/**
	 * Inserting sample sales.
	 */
	public static void insertSale(String product_name, double unit_cost, int quantity) {
		System.out.println("- Attempting to insert a sale transaction...");
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			SaleTransaction sale = new SaleTransaction();
			sale.setProductName(product_name);
			sale.setQuantity(quantity);
			sale.setUnitCost(unit_cost);
			sale.setTotalCost(unit_cost * quantity);
			s.save(sale);
			System.out.println("Inserted a sale transaction:\n" + sale.toString() + "\n---");
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("-- Rolling back transaction...");
			if (tx != null) {
				tx.rollback();
			}
			return;
		}
	}

	/**
	 * Inserting sample sales.
	 */
	public static void insertSales() {
		System.out.println("- Inserting sample sale transactions...\n---");
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			ArrayList<SaleTransaction> sales = generateSales();
			for (SaleTransaction sale : sales) {
				s.save(sale);
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("-- Rolling back transaction...");
			if (tx != null) {
				tx.rollback();
			}
			return;
		}
		System.out.println("Inserted 5 sales transactions.\n---");
	}

	/**
	 * Queries and lists all sale transactions in the database.
	 */
	public static void listSales() {
		System.out.println("- Listing all sale transactions...");
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			List<SaleTransaction> results = s.createQuery("FROM sales").list();
			System.out.println(String.format("%-10s %-30s %-10s %-10s %-10s %-20s", "Sales ID", "Product Name",
					"Quantity", "Unit Cost", "Total Cost", "Timestamp"));
			for (SaleTransaction sale : results) {
				System.out.println(sale.toString());
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("-- Rolling back transaction...");
			if (tx != null) {
				tx.rollback();
			}
			return;
		}
		System.out.println("Listed all sales.\n---");
	}

	/**
	 * Helper method to generate some sale transactions.
	 * 
	 * @return the list of example sales
	 */
	public static ArrayList<SaleTransaction> generateSales() {
		System.out.println("- Generating some sale transactions...");
		ArrayList<SaleTransaction> sales = new ArrayList<>();
		Set<String> keys = inventory.keySet();
		Iterator<String> iter = keys.iterator();
		for (int i = 0; i < 5; i++) {
			String key = iter.next();
			SaleTransaction sale = new SaleTransaction();
			int quantity = rng.nextInt(20);
			double price = inventory.get(key);
			sale.setProductName(key);
			sale.setQuantity(quantity);
			sale.setTotalCost(quantity * price);
			sale.setUnitCost(price);
			sales.add(sale);
			try {
				Thread.sleep(1500);
			} catch (Exception e) {
				System.out.println("-- Could not sleep for 1.5 seconds between sale transactions.");
			}
		}
		System.out.println("Generated some sales.");
		return sales;
	}

	/**
	 * Method to generate a new timestamp for the current moment.
	 * @return a new timestamp representing the instance it was generated
	 */
	public Timestamp now() {
		return new Timestamp(new java.util.Date().getTime());
	}
}