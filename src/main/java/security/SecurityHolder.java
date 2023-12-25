package security;

import at.favre.lib.crypto.bcrypt.BCrypt;
import entity.HostelUser;
import jdbc.dao.HostelUserDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class SecurityHolder {
    private static final String EMAIL_NOT_FOUND = "Email not found or invalid email provided";
    private static final String INVALID_CREDENTIALS = "Invalid email or password";
    private final ThreadLocal<HostelUser> authorizedUser = new ThreadLocal<>();
    private final HostelUserDAO hostelUserDAO;


    public boolean login(String email, String password) throws SQLException {
        log.debug("Attempt to auth user with email: " + email);
        HostelUser user = hostelUserDAO.getHostelUserByEmail(email);
        if (Objects.isNull(user)) {
            System.out.println(EMAIL_NOT_FOUND);
            return false;
        }
        if (BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray()).verified) {
            authorizedUser.set(user);
            log.debug("Logged in user with authority: " + user.getAuthority());
            System.out.println("Hello, " + user.getName());
            return true;
        } else {
            System.out.println(INVALID_CREDENTIALS);
            log.debug("Login attempt failed");
            return false;
        }
    }

    public void logout() {
        log.debug("Bye, bye!!");
        authorizedUser.remove();
    }

    public boolean isAdminSession() {
        return getCurrentUser()
                .getAuthority()
                .equals(HostelUser.Authority.ADMIN);
    }

    public HostelUser getCurrentUser() {
        return authorizedUser.get();
    }
}
