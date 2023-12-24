package entity;

import entity.base.BaseEntity;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostelUser extends BaseEntity {

    public enum Authority {
        ADMIN,
        USER
    }

    private String name;
    private String email;
    private String password;
    private Authority authority;
    @ToString.Exclude
    private Set<HostelOrder> hostelOrders;
}
