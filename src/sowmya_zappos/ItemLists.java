/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sowmya_zappos;

/**
 *
 * @author Sowmya
 */
import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class ItemLists implements Comparable{
	private ArrayList<Item> combItems;		
	private double sum;							
	private double idealTotal; 					
	private double closeness;					
	private final double TOL = Math.pow(10, -7);
	
	public ItemLists(ArrayList<Item> productsForCombo, double total) {
		combItems = productsForCombo;
		sum = 0;
		idealTotal = total;
		for(Item x:combItems) sum += x.getPrice(); 
		closeness = Math.abs(idealTotal - sum);
	}
	
	
	public double getPrice(int index) {
		return combItems.get(index).getPrice();
	}
	
	
	public double getSum() {
		return sum;
	}
	
	
	public int getProductComboLength() {
		return combItems.size();
	}
	
	
	public double getCloseness() {
		return closeness;
	}
	
	
	public double getTotal() {
		return idealTotal;
	}

	@Override
	public int compareTo(Object o) {
		ItemLists other = (ItemLists) o;
		if(this.equals(other)) return 0;
		else if(this.closeness < other.getCloseness()) return -1;
		else return 1;
	}
	
	
	public boolean equals(ItemLists other) {
		if(this.combItems.size() != other.getProductComboLength()) {
			return false;
		}
		if(this.idealTotal != other.getTotal()) {
			return false;
		}
		for(int i = 0; i < combItems.size(); i++){
			if(Math.abs(this.combItems.get(i).getPrice() - other.getPrice(i)) > TOL) {
				return false;
			}
		}
		return true;
	}
	
	
	public String toString() {
		String toReturn = "Products with sum $" + sum + "\n";
		for(int i = 0; i < combItems.size(); i ++) {
			toReturn += (i+1) + ": " + combItems.get(i).toString() + "\n";
		}
		return toReturn;
	}
	
}
