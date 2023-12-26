package jdbc.dao;

import entity.Bill;
import entity.HostelOrder;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class BillDAO {
    private final EntityManager entityManager;

    public Bill createBill(Bill bill) {
        entityManager.getTransaction().begin();
        entityManager.persist(bill);
        entityManager.getTransaction().commit();
        return bill;
    }

    public Bill getBillById(Long id) {
        return entityManager.find(Bill.class, id);
    }

    public List<Bill> getAllBills() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bill> cq = cb.createQuery(Bill.class);
        Root<Bill> rootEntry = cq.from(Bill.class);
        CriteriaQuery<Bill> all = cq.select(rootEntry);
        TypedQuery<Bill> allQuery = entityManager.createQuery(all);
        return allQuery.getResultList();
    }

    public void updateBill(Bill bill) {
        entityManager.getTransaction().begin();
        entityManager.merge(bill);
        entityManager.getTransaction().commit();
    }

    public void deleteBill(Long id) {
        entityManager.getTransaction().begin();
        Bill bill = entityManager.find(Bill.class, id);
        if (bill != null) {
            entityManager.remove(bill);
        }
        entityManager.getTransaction().commit();
    }

    public Set<Bill> getAllUnpaidBills() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bill> cq = cb.createQuery(Bill.class);
        Root<Bill> bill = cq.from(Bill.class);
        Join<Bill, HostelOrder> order = bill.join("hostelOrder");
        cq.where(cb.and(
                cb.equal(order.get("status"), HostelOrder.Status.APPROVED),
                cb.equal(bill.get("status"), Bill.Status.NOT_PAYED)
        ));
        return entityManager.createQuery(cq).getResultStream().collect(Collectors.toSet());
    }
}
