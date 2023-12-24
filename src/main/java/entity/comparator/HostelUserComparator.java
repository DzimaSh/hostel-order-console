package entity.comparator;

import entity.HostelUser;

import java.util.Comparator;

public class HostelUserComparator implements Comparator<HostelUser> {

    @Override
    public int compare(HostelUser user1, HostelUser user2) {
        int nameComparison = user1.getName().compareTo(user2.getName());
        if (nameComparison != 0) {
            return nameComparison;
        }

        int emailComparison = user1.getEmail().compareTo(user2.getEmail());
        if (emailComparison != 0) {
            return emailComparison;
        }

        return user1.getAuthority().compareTo(user2.getAuthority());
    }
}

