/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sowmya_zappos;

import java.io.*;
import org.json.simple.parser.*;

/**
 *
 * @author Sowmya
 */
public class ItemSearch {
  public static void main(String[] args) {
		
    boolean isInputAvailable = true;
    int numItems = 0;
    double  amt = 0;
    while(isInputAvailable){
			String numItemsInput = "Enter the number of items:";
			String numItemsOutput = Parsing.prompt(numItemsInput);
			String amtInput = "Enter the amount :";
			String amtOutput = Parsing.prompt(amtInput);
			
			boolean error = false;
			
			
			try {
				numItems = Integer.parseInt(numItemsOutput);
			} catch (NumberFormatException e){
				System.err.println("Integer Values required ");
				error = true;
			}
			
			//check to make sure total price is a double
			try {
				amt = Double.parseDouble(amtOutput);
			} catch (NumberFormatException e){
				System.err.println("Enter valid Amt");
				error = true;
			}
			
			
			if(numItems < 1 && !error) {
				System.out.println("Enter valid Integer");
			} 
			
			else if(amt <= 0 && !error) {
				System.out.println("Enter valid amt");
			} 
			
			else if(!error){
				isInputAvailable = false;
			}
		}
		
		
		try {
			ItemSearcher searcher = new ItemSearcher(numItems,amt);
			System.out.println(searcher.getGiftCombos());
			
		} catch (ParseException e) {
			// occurs if parsed incorrectly
			System.err.println("Parse Exception!");
			e.printStackTrace();
		} catch (IOException e) {
			// occurs if response code wasn't 200
			System.err.println("");
			e.printStackTrace();
		}
	}
}
