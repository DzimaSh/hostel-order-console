package entity;

import entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Set;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrders")
@Data
@NoArgsConstructor
@Entity
@Table(name = "room")
@SequenceGenerator(
        name = "room_seq",
        sequenceName = "room_seq",
        allocationSize = 1
)
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

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ToString.Exclude
    @ManyToMany(mappedBy = "rooms")
    private Set<HostelOrder> hostelOrders;

    public Room(Long id, Integer roomNumber, Long possibleLivers, Double rentPricePerDay, Status status, Type type, Set<HostelOrder> hostelOrders) {
        super(id);
        this.roomNumber = roomNumber;
        this.possibleLivers = possibleLivers;
        this.rentPricePerDay = rentPricePerDay;
        this.status = status;
        this.type = type;
        this.hostelOrders = hostelOrders;
    }

    public Room addOrders(Collection<HostelOrder> orders) {
        this.getHostelOrders().addAll(orders);

        return this;
    }

    public String details() {
        return "Room Details:\n" +
                "-------------\n" +
                "Room ID: " + id + "\n" +
                "Room Number: " + roomNumber + "\n" +
                "Possible Livers: " + possibleLivers + "\n" +
                "Rent Price Per Day: " + rentPricePerDay + "\n" +
                "Status: " + status + "\n" +
                "Type: " + type + "\n";
    }

}
