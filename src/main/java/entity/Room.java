package entity;

import entity.base.BaseEntity;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

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
    private Set<HostelOrder> hostelOrders;

    public Room(Long id) {
        super(id);
    }

    public Room(Long id,
                Integer roomNumber,
                Long possibleLivers,
                Double rentPricePerDay,
                Status status,
                Type type,
                Set<HostelOrder> hostelOrders
    ) {
        super(id);
        this.roomNumber = roomNumber;
        this.possibleLivers = possibleLivers;
        this.rentPricePerDay = rentPricePerDay;
        this.status = status;
        this.type = type;
        this.hostelOrders = hostelOrders;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Long getPossibleLivers() {
        return possibleLivers;
    }

    public void setPossibleLivers(Long possibleLivers) {
        this.possibleLivers = possibleLivers;
    }

    public Double getRentPricePerDay() {
        return rentPricePerDay;
    }

    public void setRentPricePerDay(Double rentPricePerDay) {
        this.rentPricePerDay = rentPricePerDay;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Set<HostelOrder> getHostelOrders() {
        return hostelOrders;
    }

    public void setHostelOrders(Set<HostelOrder> hostelOrders) {
        this.hostelOrders = hostelOrders;
    }

    public Room addOrders(Collection<HostelOrder> orders) {
        this.getHostelOrders().addAll(orders);

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Room room = (Room) o;
        return Objects.equals(roomNumber, room.roomNumber) && Objects.equals(possibleLivers, room.possibleLivers) && Objects.equals(rentPricePerDay, room.rentPricePerDay) && status == room.status && type == room.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roomNumber, possibleLivers, rentPricePerDay, status, type);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", possibleLivers=" + possibleLivers +
                ", rentPricePerDay=" + rentPricePerDay +
                ", status=" + status +
                ", type=" + type +
                ", id=" + id +
                '}';
    }
}
