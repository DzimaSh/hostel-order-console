package entity;

import entity.base.DateRangeEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true, exclude = { "client", "rooms", "bill" })
@Data
@NoArgsConstructor
public class HostelOrder extends DateRangeEntity {

    public enum Status {
        OPEN,
        APPROVED,
        PAYED,
        CLOSED,
    }

    @ToString.Exclude
    private HostelUser client;
    @ToString.Exclude
    private Set<Room> rooms;
    @ToString.Exclude
    private Bill bill;
    private Room.Type desiredRoomType;
    private Integer desiredBeds;
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

        if (status == Status.APPROVED) {
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
