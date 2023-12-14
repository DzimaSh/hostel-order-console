package entity;

import entity.enums.BillStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Bill extends BaseEntity {
    @OneToOne(mappedBy = "bill")
    private Order order;

    private Long billPrice;

    private BillStatus status = BillStatus.NOT_PAYED;
}
