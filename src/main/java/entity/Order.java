package entity;

import entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Order extends BaseEntity {

    @ManyToOne
    private User client;

    @ManyToMany
    private Set<Room> rooms;

    @OneToOne
    private Bill bill;

    private Date startDate;

    private Date endDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
