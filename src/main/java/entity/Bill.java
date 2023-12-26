package entity;

import entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrder")
@Data
@NoArgsConstructor
@Table(name = "bill")
@Entity
@SequenceGenerator(
        name = "bill_seq",
        sequenceName = "bill_seq",
        allocationSize = 1
)
public class Bill extends BaseEntity {

    public enum Status {
        NOT_PAYED,
        PAYED,
    }

    @OneToOne(mappedBy = "bill")
    @ToString.Exclude
    private HostelOrder hostelOrder;

    private Double billPrice = -1d;

    @Enumerated(EnumType.STRING)
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
