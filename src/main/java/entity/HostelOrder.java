package entity;

import entity.base.DateRangeEntity;
import lombok.*;

import java.time.temporal.ChronoUnit;
import java.util.Set;

@EqualsAndHashCode(callSuper = true, exclude = { "client", "rooms", "bill" })
@Data
@AllArgsConstructor
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

    public Long countPeriodOfOrder() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
}
