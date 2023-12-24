package entity;

import entity.base.BaseEntity;

import java.util.Objects;
import java.util.Set;

public class HostelUser extends BaseEntity {

    public enum Authority {
        ADMIN,
        USER
    }

    private String name;

    private String email;

    private String password;

    private Authority authority;

    private Set<HostelOrder> hostelOrders;

    public HostelUser(Long id) {
        super(id);
    }

    public HostelUser(Long id,
                      String name,
                      String email,
                      String password,
                      Authority authority,
                      Set<HostelOrder> hostelOrders
    ) {
        super(id);
        this.name = name;
        this.email = email;
        this.password = password;
        this.authority = authority;
        this.hostelOrders = hostelOrders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Set<HostelOrder> getHostelOrders() {
        return hostelOrders;
    }

    public void setHostelOrders(Set<HostelOrder> hostelOrders) {
        this.hostelOrders = hostelOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HostelUser that = (HostelUser) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(password, that.password) && authority == that.authority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, email, password, authority);
    }

    @Override
    public String toString() {
        return "HostelUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", authority=" + authority +
                ", id=" + id +
                '}';
    }
}
