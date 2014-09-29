/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sowmya_zappos;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
import org.json.simple.parser.*;
/**
 *
 * @author Sowmya
 */
public class ItemSearcher {
    private int numItems;			
	private double totalPrice;		
	private double maxPrice;		
	private int page;				
	private JSONArray products;		
	private ArrayList<Item> items; 
	private ArrayList<ItemLists> productCombos; 
	private final double TOL = Math.pow(10, -7);  
	private final int MAXCOMBOS = 30;
        public ItemSearcher(int num, double total) {
		numItems = num;
		totalPrice = total;
		maxPrice = Integer.MAX_VALUE; 	//will set later
		page = 1;					//will pull at least one page of results
		products = new JSONArray();
		items = new ArrayList<Item>();
		productCombos = new ArrayList<ItemLists>();
	}
        
        private Double getPrice(Object item){
		return Double.parseDouble(((String) ((JSONObject) item).get("price")).substring(1));
	}
	private void setProductsInRange() throws IOException, ParseException {
        try {
            String reply = Parsing.httpGet(Parsing.BASEURL + "&term=&limit=100&sort={\"price\":\"asc\"}");
             JSONObject replyObject;
    
        replyObject = Parsing.parseReply(reply);
    
            JSONArray resultArray = Parsing.getResults(replyObject);
            
            
            double firstPrice = getPrice(resultArray.get(0));
            
            
            if( (firstPrice * numItems) > totalPrice) {
                    products = null;
            }
             maxPrice = totalPrice - (numItems - 1)*(firstPrice);
                    page++;
                    Double lastPrice = getPrice(resultArray.get(resultArray.size() - 1));
                    while(lastPrice < maxPrice) { 
                    //System.out.println("Last price: " + lastPrice);
                    String nextPage = Parsing.httpGet(Parsing.BASEURL + "&term=&limit=100&sort={\"price\":\"asc\"}&page=" + page);
                    //System.out.println("Pulling page " + page + "...");
                    JSONObject nextObject = null;
        try {
            nextObject = Parsing.parseReply(nextPage);
        } catch (java.text.ParseException ex) {
            Logger.getLogger(ItemSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
                    JSONArray nextArray = Parsing.getResults(nextObject);
                    
                    //append new page of results to original array
                    resultArray.addAll(nextArray);
                    
                    //get new last product and price
                    lastPrice = getPrice(nextArray.get(nextArray.size() - 1));
                    
                    page++;
            }

            //return resultArray.toString();
            products = resultArray;
        } catch (java.text.ParseException ex) {
            Logger.getLogger(ItemSearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	/**
	 * Converts JSONObjects into Products, puts products in price range in
	 * ArrayList to be sorted and searched
	 */
	private void setSearchableProducts() {
		//add the first (smallest price) object
		items.add(new Item((JSONObject)products.get(0)));
		
		//count how many times a price has already shown up
		int already = 1;
		int numPrices = 1;
		//go through the whole 
		for(int i = 1; i < products.size() && getPrice(products.get(i)) < maxPrice; i++) {
			double currentPrice = getPrice(products.get(i));
			if( currentPrice > items.get(numPrices-1).getPrice()) {
				items.add(new Item((JSONObject)products.get(i)));
				numPrices++;
				already = 1;
			} else if(Math.abs(currentPrice - items.get(numPrices-1).getPrice()) < TOL && already < numItems){
				items.add(new Item((JSONObject)products.get(i)));
				numPrices++;
				already++;
			} else {
				while(i < products.size() && Math.abs(currentPrice - items.get(numPrices-1).getPrice()) < TOL) {
					i++;
					currentPrice = getPrice(products.get(i));
				}
				i++;
				already = 0;
			}
		}
	}

	/**
	 * Recursively finds the product combinations of numItems items within $1 of the totalPrice
	 */
	private void setProductCombos() {
		setProductCombosRecursive(items, totalPrice, new ArrayList<Item>());
	}
	
	
	private void setProductCombosRecursive(ArrayList<Item> productList, double target, ArrayList<Item> partial) {
		int priceWithinAmount = 1;
		
		//if partial size > numItems, you already have too many items, so stop
		if(partial.size() > numItems) { return; }
		
		double sum = 0;
		for(Item x : partial) sum += x.getPrice();
		
		//if sum is within $1 of target, and partial size is numItems, and you don't already have too many product 
		//combos, then add another product combo
		if(Math.abs(sum - target) < priceWithinAmount && partial.size() == numItems && productCombos.size() < MAXCOMBOS) {
			//if no price combos yet, just add it on
			if(productCombos.size() == 0) {	productCombos.add(new ItemLists(partial, totalPrice)); }
			//otherwise, check it against the most recent product combo to make sure you're not repeating
			//TODO: check all product combos
			else{
				ItemLists testerCombo = productCombos.get(productCombos.size() -1);
				ItemLists partialCombo = new ItemLists(partial, totalPrice);
				if(!partialCombo.equals(testerCombo)) {
					productCombos.add(partialCombo);
				}
			}
		}
		//if sum is at or within $1 of target, then stop - done!
		if(sum >= target + priceWithinAmount) {
			return;
		}
		
		//otherwise, recursively continue adding another product to combo and test it
		for(int i = 0; i < productList.size() && !(partial.size() == numItems && sum < target); i++){
			ArrayList<Item> remaining = new ArrayList<Item>();
			Item n = productList.get(i);
			for(int j=i+1; j < productList.size(); j++) {remaining.add(productList.get(j)); }
			ArrayList<Item> partial_rec = new ArrayList<Item>(partial);
			partial_rec.add(n);
			setProductCombosRecursive(remaining, target, partial_rec);
		}
	}
	
	/**
	 * Sorts product combinations from closest to totalPrice to furthest away
	 */
	@SuppressWarnings("unchecked")
	private void sortProductCombos() {
		Collections.sort(productCombos);
	}
	
	/**
	 * Returns the gift combinations that are closest to the total dollar amount
	 * @throws IOException
	 * @throws ParseException
	 */
	public String getGiftCombos() throws IOException, ParseException {
		//get products from API
		System.out.println("Searching Zappos...");
		this.setProductsInRange();
		
		System.out.println("Finding combinations that work for you...");
		//convert to Products
		this.setSearchableProducts();
		//find combinations that work
		this.setProductCombos();
		//sort combos by how close they are to given total
		this.sortProductCombos();
		
		//see if you have any combos
		if(productCombos.size() != 0) {
			String toPrint = "\nDone!\n";
			for(ItemLists x:productCombos) {
				toPrint += x.toString() + "\n";
			}
			return toPrint;
		}
		else {
			return "We couldn't find a set of items matching your criteria. " +
					"Please try again with fewer items or a larger dollar amount.";
		}
	}
	
}
