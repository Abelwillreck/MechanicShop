package application;

import java.io.Serializable;
import java.math.BigDecimal;

public class PartPurchase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String partName;
    private String vendor;
    private int quantity;
    private BigDecimal unitPrice;

    public PartPurchase(String partName, String vendor, int quantity, BigDecimal unitPrice) {
        this.partName = partName;
        this.vendor = vendor;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getPartName() { return partName; }
    public String getVendor() { return vendor; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    public void setPartName(String partName) { this.partName = partName; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return partName + " x" + quantity + " @ $" + unitPrice + " (" + vendor + ") = $" + getLineTotal();
    }
}