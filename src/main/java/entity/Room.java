package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Room extends BaseEntity {

    private Long possibleLivers;

    private Long rentPricePerDay;

    @ManyToMany(mappedBy = "rooms")
    private Set<Order> orders;
}
