package entity.comparator;

import entity.HostelOrder;

import java.util.Comparator;

public class HostelOrderComparator implements Comparator<HostelOrder> {

    @Override
    public int compare(HostelOrder order1, HostelOrder order2) {
        int roomTypeComparison = order1.getDesiredRoomType().compareTo(order2.getDesiredRoomType());
        if (roomTypeComparison != 0) {
            return roomTypeComparison;
        }

        int bedsComparison = order1.getDesiredBeds().compareTo(order2.getDesiredBeds());
        if (bedsComparison != 0) {
            return bedsComparison;
        }

        return order1.getStatus().compareTo(order2.getStatus());
    }
}

