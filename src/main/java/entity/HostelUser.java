package entity;

import entity.base.BaseEntity;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrders")
@Data
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

    public HostelUser(Long id, String name, String email, String password, Authority authority, Set<HostelOrder> hostelOrders) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.hostelOrders = hostelOrders;
    }

    public String profile() {
        return "Profile Page:\n" +
                "-------------\n" +
                "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Authority: " + authority + "\n" +
                "Number of Orders: " + (hostelOrders != null ? hostelOrders.size() : 0);
    }
}
