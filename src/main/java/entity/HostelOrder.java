package entity;

import entity.base.DateRangeEntity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;

public class HostelOrder extends DateRangeEntity {

    public enum Status {
        OPEN,
        APPROVED,
        PAYED,
        CLOSED,
    }

    private HostelUser client;

    private Set<Room> rooms;

    private Room.Type desiredRoomType;

    private Integer desiredBeds;

    private Bill bill;

    private Status status;

    public HostelOrder(Long id, LocalDate startDate, LocalDate endDate) {
        super(id, startDate, endDate);
    }

    public HostelOrder(Long id,
                       LocalDate startDate,
                       LocalDate endDate,
                       HostelUser client,
                       Set<Room> rooms,
                       Room.Type desiredRoomType,
                       Integer desiredBeds,
                       Bill bill,
                       Status status
    ) {
        super(id, startDate, endDate);
        this.client = client;
        this.rooms = rooms;
        this.desiredRoomType = desiredRoomType;
        this.desiredBeds = desiredBeds;
        this.bill = bill;
        this.status = status;
    }

    public HostelUser getClient() {
        return client;
    }

    public void setClient(HostelUser client) {
        this.client = client;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public Room.Type getDesiredRoomType() {
        return desiredRoomType;
    }

    public void setDesiredRoomType(Room.Type desiredRoomType) {
        this.desiredRoomType = desiredRoomType;
    }

    public Integer getDesiredBeds() {
        return desiredBeds;
    }

    public void setDesiredBeds(Integer desiredBeds) {
        this.desiredBeds = desiredBeds;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long countPeriodOfOrder() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HostelOrder that = (HostelOrder) o;
        return desiredRoomType == that.desiredRoomType && Objects.equals(desiredBeds, that.desiredBeds) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), desiredRoomType, desiredBeds, status);
    }

    @Override
    public String toString() {
        return "HostelOrder{" +
                "desiredRoomType=" + desiredRoomType +
                ", desiredBeds=" + desiredBeds +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", id=" + id +
                '}';
    }
}
