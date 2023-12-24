package entity.comparator;

import entity.Bill;

import java.util.Comparator;

public class BillComparator implements Comparator<Bill> {
    @Override
    public int compare(Bill bill1, Bill bill2) {
        int priceComparison = bill1.getBillPrice().compareTo(bill2.getBillPrice());
        if (priceComparison != 0) {
            return priceComparison;
        }

        return bill1.getStatus().compareTo(bill2.getStatus());
    }
}
