package application;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private String workPerformed;
    private final List<PartPurchase> parts = new ArrayList<>();
    private BigDecimal totalCharged;

    public ServiceRecord(LocalDate date, String workPerformed, BigDecimal totalCharged) {
        this.date = date;
        this.workPerformed = workPerformed;
        this.totalCharged = totalCharged;
    }

    public LocalDate getDate() { return date; }
    public String getWorkPerformed() { return workPerformed; }
    public List<PartPurchase> getParts() { return parts; }
    public BigDecimal getTotalCharged() { return totalCharged; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setWorkPerformed(String workPerformed) { this.workPerformed = workPerformed; }
    public void setTotalCharged(BigDecimal totalCharged) { this.totalCharged = totalCharged; }

    @Override
    public String toString() {
        return "Date: " + date + "\nWork: " + workPerformed + "\nTotal Charged: $" + totalCharged;
    }
}