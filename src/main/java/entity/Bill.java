package entity;

import entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrder")
@Data
@NoArgsConstructor
public class Bill extends BaseEntity {

    public enum Status {
        NOT_PAYED,
        PAYED,
    }

    private HostelOrder hostelOrder;

    private Double billPrice = -1d;

    private Status status = Status.NOT_PAYED;

    public Bill(Long id, HostelOrder hostelOrder, Double billPrice, Status status) {
        super(id);
        this.hostelOrder = hostelOrder;
        this.billPrice = billPrice;
        this.status = status;
    }

    public String details() {
        return "Bill Details:\n" +
                "-------------\n" +
                "Bill ID: " + id + "\n" +
                "Order ID: " + (hostelOrder != null ? hostelOrder.getId() : "Not available") + "\n" +
                "Bill Price: " + billPrice + "\n" +
                "Status: " + status + "\n";
    }

}
