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


    public HostelUser login(String email, String password) throws SQLException {
        log.debug("Attempt to auth user with email: " + email);
        HostelUser user = hostelUserDAO.getHostelUserByEmail(email);
        if (Objects.isNull(user)) {
            System.out.println(EMAIL_NOT_FOUND);
            throw new IllegalStateException(EMAIL_NOT_FOUND);
        }
        if (BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray()).verified) {
            authorizedUser.set(user);
            log.debug("Logged in user with authority: " + user.getAuthority());
            System.out.println("Hello, " + user.getName());
            return user;
        } else {
            System.out.println(INVALID_CREDENTIALS);
            throw new IllegalStateException(INVALID_CREDENTIALS);
        }
    }

    public void logout() {
        log.debug("Bye, bye!!");
        authorizedUser.remove();
    }

    public HostelUser getCurrentUser() {
        return authorizedUser.get();
    }
}
