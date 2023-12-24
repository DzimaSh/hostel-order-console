package entity.base;

import java.time.LocalDate;
import java.util.Objects;

public abstract class DateRangeEntity extends BaseEntity {

    protected LocalDate startDate;

    protected LocalDate endDate;

    public DateRangeEntity(Long id, LocalDate startDate, LocalDate endDate) {
        super(id);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DateRangeEntity that = (DateRangeEntity) o;
        return Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), startDate, endDate);
    }

    @Override
    public String toString() {
        return "DateRangeEntity{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", id=" + id +
                '}';
    }
}
