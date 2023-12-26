package jdbc.dao;

import entity.HostelOrder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HostelOrderDAO {

    private final EntityManager entityManager;

    public HostelOrder createHostelOrder(HostelOrder order) {
        log.debug("Creating a new HostelOrder...");
        entityManager.getTransaction().begin();
        entityManager.persist(order);
        entityManager.getTransaction().commit();
        log.info("Created a new HostelOrder with id {}", order.getId());
        return order;
    }

    public HostelOrder getHostelOrderById(Long id) {
        log.debug("Retrieving HostelOrder with id {}...", id);
        HostelOrder order = entityManager.find(HostelOrder.class, id);
        if (order != null) {
            log.info("Retrieved HostelOrder with id {}", id);
        } else {
            log.warn("No HostelOrder found with id {}", id);
        }
        return order;
    }

    public List<HostelOrder> getAllOpenedHostelOrders() {
        log.debug("Retrieving all opened HostelOrders...");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HostelOrder> cq = cb.createQuery(HostelOrder.class);
        Root<HostelOrder> orderRoot = cq.from(HostelOrder.class);
        cq.where(cb.equal(orderRoot.get("status"), HostelOrder.Status.OPEN));

        TypedQuery<HostelOrder> query = entityManager.createQuery(cq);
        List<HostelOrder> orders = query.getResultList();

        log.info("Retrieved {} opened HostelOrders", orders.size());
        return orders;
    }


    public List<HostelOrder> getAllHostelOrdersByUser(Long userId) {
        log.debug("Retrieving all HostelOrders for user with id {}...", userId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<HostelOrder> cq = cb.createQuery(HostelOrder.class);
        Root<HostelOrder> order = cq.from(HostelOrder.class);
        cq.where(cb.equal(order.get("client").get("id"), userId));
        List<HostelOrder> orders = entityManager.createQuery(cq).getResultList();

        log.info("Retrieved {} HostelOrders for user with id {}", orders.size(), userId);
        return orders;
    }


    public void updateHostelOrder(HostelOrder order) {
        log.debug("Updating HostelOrder with id {}...", order.getId());
        entityManager.getTransaction().begin();
        entityManager.merge(order);
        entityManager.getTransaction().commit();
        log.info("Updated HostelOrder with id {}", order.getId());
    }

    public void deleteHostelOrder(Long id) {
        log.debug("Deleting HostelOrder with id {}...", id);
        entityManager.getTransaction().begin();
        HostelOrder order = entityManager.find(HostelOrder.class, id);
        if (order != null) {
            entityManager.remove(order);
            log.info("Deleted HostelOrder with id {}", id);
        } else {
            log.warn("No HostelOrder found with id {}", id);
        }
        entityManager.getTransaction().commit();
    }
}
