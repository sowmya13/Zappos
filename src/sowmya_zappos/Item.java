/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sowmya_zappos;

import org.json.simple.*;

/**
 *
 * @author Sowmya
 */
public class Item {

    private String id;
    private String name;
    private String styleId;
    private double price;
    private String priceValue;

    public Item(JSONObject item) {
        price = Double.parseDouble(((String) item.get("price")).substring(1));
        id = (String) item.get("productId");
        name = (String) item.get("productName");
        styleId = (String) item.get("styleId");

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(String priceValue) {
        this.priceValue = priceValue;
    }

    public String getStyleId() {
        return styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }
    
    public String toString() {
		return name + ", $" + priceValue + " (id:" + id + ", styleId:" + styleId + ")";
	}
}
