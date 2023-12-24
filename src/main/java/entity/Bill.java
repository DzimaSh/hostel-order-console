package entity;

import entity.base.BaseEntity;

import java.util.Objects;

public class Bill extends BaseEntity {

    public enum Status {
        NOT_PAYED,
        PAYED,
    }

    private HostelOrder hostelOrder;

    private Double billPrice;

    private Status status = Status.NOT_PAYED;

    public Bill(Long id, HostelOrder hostelOrder, Double billPrice, Status status) {
        super(id);
        this.hostelOrder = hostelOrder;
        this.billPrice = billPrice;
        this.status = status;
    }

    public HostelOrder getHostelOrder() {
        return hostelOrder;
    }

    public void setHostelOrder(HostelOrder hostelOrder) {
        this.hostelOrder = hostelOrder;
    }

    public Double getBillPrice() {
        return billPrice;
    }

    public void setBillPrice(Double billPrice) {
        this.billPrice = billPrice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Bill bill = (Bill) o;
        return Objects.equals(billPrice, bill.billPrice) && status == bill.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), billPrice, status);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billPrice=" + billPrice +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
