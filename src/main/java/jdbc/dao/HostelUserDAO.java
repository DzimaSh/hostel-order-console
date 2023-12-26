package jdbc.dao;

import entity.HostelUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HostelUserDAO {

    private final EntityManager entityManager;

    public HostelUser createHostelUser(HostelUser hostelUser) {
        log.debug("Creating a new HostelUser...");
        entityManager.getTransaction().begin();
        entityManager.persist(hostelUser);
        entityManager.getTransaction().commit();
        log.info("Created a new HostelUser with id {}", hostelUser.getId());
        return hostelUser;
    }

    public HostelUser getHostelUserById(Long id) {
        log.debug("Retrieving HostelUser with id {}...", id);
        HostelUser user = entityManager.find(HostelUser.class, id);
        if (user != null) {
            log.info("Retrieved HostelUser with id {}", id);
        } else {
            log.warn("No HostelUser found with id {}", id);
        }
        return user;
    }

    public List<HostelUser> getAllHostelUsers() {
        log.debug("Retrieving all HostelUsers...");
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HostelUser> cq = cb.createQuery(HostelUser.class);
        Root<HostelUser> rootEntry = cq.from(HostelUser.class);
        CriteriaQuery<HostelUser> all = cq.select(rootEntry);
        TypedQuery<HostelUser> allQuery = entityManager.createQuery(all);
        List<HostelUser> users = allQuery.getResultList();
        log.info("Retrieved {} HostelUsers", users.size());
        return users;
    }

    public void updateHostelUser(HostelUser hostelUser) {
        log.debug("Updating HostelUser with id {}...", hostelUser.getId());
        entityManager.getTransaction().begin();
        entityManager.merge(hostelUser);
        entityManager.getTransaction().commit();
        log.info("Updated HostelUser with id {}", hostelUser.getId());
    }

    public void deleteHostelUser(Long id) {
        log.debug("Deleting HostelUser with id {}...", id);
        entityManager.getTransaction().begin();
        HostelUser user = entityManager.find(HostelUser.class, id);
        if (user != null) {
            entityManager.remove(user);
            log.info("Deleted HostelUser with id {}", id);
        } else {
            log.warn("No HostelUser found with id {}", id);
        }
        entityManager.getTransaction().commit();
    }

    public HostelUser getHostelUserByEmail(String email) {
        log.debug("Retrieving HostelUser with email {}...", email);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HostelUser> cq = cb.createQuery(HostelUser.class);
        Root<HostelUser> user = cq.from(HostelUser.class);
        cq.where(cb.equal(user.get("email"), email));
        TypedQuery<HostelUser> query = entityManager.createQuery(cq);
        try {
            HostelUser result = query.getSingleResult();
            log.info("Retrieved HostelUser with email {}", email);
            return result;
        } catch (NoResultException e) {
            log.warn("No HostelUser found with email {}", email);
            return null;
        }
    }
}

