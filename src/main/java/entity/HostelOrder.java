package entity;

import entity.base.DateRangeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true, exclude = { "client", "rooms", "bill" })
@Data
@NoArgsConstructor
@Entity
@Table(name = "hostel_order")
@SequenceGenerator(
        name = "hostel_order_seq",
        sequenceName = "hostel_order_seq",
        allocationSize = 1
)
public class HostelOrder extends DateRangeEntity {

    public enum Status {
        OPEN,
        APPROVED,
        PAYED,
        CLOSED,
    }

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "client_id")
    private HostelUser client;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_room",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "room_id") }
    )
    private Set<Room> rooms;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @Enumerated(EnumType.STRING)
    @Column(name = "desired_room_type")
    private Room.Type desiredRoomType;

    @Column(name = "desired_beds")
    private Integer desiredBeds;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    public HostelOrder(Long id,
                       LocalDate startDate,
                       LocalDate endDate,
                       HostelUser client,
                       Set<Room> rooms,
                       Bill bill,
                       Room.Type desiredRoomType,
                       Integer desiredBeds,
                       Status status
    ) {
        super(id, startDate, endDate);
        this.client = client;
        this.rooms = rooms;
        this.bill = bill;
        this.desiredRoomType = desiredRoomType;
        this.desiredBeds = desiredBeds;
        this.status = status;
    }

    public Long countPeriodOfOrder() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public String details() {
        String roomDetails = "\tNot available till approve";
        String billDetails = "\tNot available till approve";

        if (status.ordinal() >= Status.APPROVED.ordinal()) {
            roomDetails = rooms.isEmpty()
                    ? "\tNo rooms assigned"
                    : rooms.stream()
                    .map(room -> "\t" + room.details().replace("\n", "\n\t"))
                    .collect(Collectors.joining("\n"));

            billDetails = bill != null
                    ? "\t" + bill.details().replace("\n", "\n\t")
                    : "\tNo bill available";
        }

        return "Order Details:\n" +
                "-------------\n" +
                "Order ID: " + id + "\n" +
                "Rooms:\n" + roomDetails + "\n" +
                "Client Name: " + client.getName() + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate + "\n" +
                "Status: " + status + "\n" +
                "Total Days: " + countPeriodOfOrder() + "\n" +
                "Bill:\n" + billDetails + "\n";
    }
}
