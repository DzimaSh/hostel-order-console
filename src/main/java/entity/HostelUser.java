package entity;

import entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@EqualsAndHashCode(callSuper = true, exclude = "hostelOrders")
@Data
@NoArgsConstructor
@Entity
@Table(name = "hostel_user")
@SequenceGenerator(
        name = "hostel_user_seq",
        sequenceName = "hostel_user_seq",
        allocationSize = 1
)
public class HostelUser extends BaseEntity {

    public enum Authority {
        ADMIN,
        USER
    }

    @Column(name = "name")
    private String name;

    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;

    @ToString.Exclude
    @OneToMany(mappedBy = "client")
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
                "Number of Orders: " + (hostelOrders != null ? hostelOrders.size() : 0) + "\n";
    }
}
