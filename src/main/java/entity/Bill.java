package entity;

import entity.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrder")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill extends BaseEntity {

    public enum Status {
        NOT_PAYED,
        PAYED,
    }

    private HostelOrder hostelOrder;

    private Double billPrice;

    private Status status = Status.NOT_PAYED;
}
