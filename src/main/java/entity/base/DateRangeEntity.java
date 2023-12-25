package entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public abstract class DateRangeEntity extends BaseEntity {

    protected LocalDate startDate;

    protected LocalDate endDate;

    public DateRangeEntity(Long id, LocalDate startDate, LocalDate endDate) {
        super(id);
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
