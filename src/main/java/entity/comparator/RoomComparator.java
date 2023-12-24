package entity.comparator;

import entity.Room;

import java.util.Comparator;

public class RoomComparator implements Comparator<Room> {

    @Override
    public int compare(Room room1, Room room2) {
        int roomNumberComparison = room1.getRoomNumber().compareTo(room2.getRoomNumber());
        if (roomNumberComparison != 0) {
            return roomNumberComparison;
        }

        int possibleLiversComparison = room1.getPossibleLivers().compareTo(room2.getPossibleLivers());
        if (possibleLiversComparison != 0) {
            return possibleLiversComparison;
        }

        int rentPriceComparison = room1.getRentPricePerDay().compareTo(room2.getRentPricePerDay());
        if (rentPriceComparison != 0) {
            return rentPriceComparison;
        }

        int statusComparison = room1.getStatus().compareTo(room2.getStatus());
        if (statusComparison != 0) {
            return statusComparison;
        }

        return room1.getType().compareTo(room2.getType());
    }
}

