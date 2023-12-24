package entity;

import entity.base.BaseEntity;
import lombok.*;

import java.util.Collection;
import java.util.Set;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room extends BaseEntity {

    public enum Type {
        BASIC,
        PREMIUM
    }

    public enum Status {
        FREE,
        RESERVED,
        OCCUPIED
    }

    private Integer roomNumber;
    private Long possibleLivers;
    private Double rentPricePerDay;
    private Status status;
    private Type type;
    @ToString.Exclude
    private Set<HostelOrder> hostelOrders;

    public Room addOrders(Collection<HostelOrder> orders) {
        this.getHostelOrders().addAll(orders);

        return this;
    }
}
