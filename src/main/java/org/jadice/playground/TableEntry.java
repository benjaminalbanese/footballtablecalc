package org.jadice.playground;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TableEntry implements Comparable<TableEntry> {
    private final Club club;
    private int scored;
    private int received;
    private int points;

    public TableEntry(Club club) {
        this(club, 0, 0, 0);
    }
    public TableEntry(TableEntry other){
        this(other.club, other.scored, other.received, other.points);
    }

    @Override
    public int compareTo(TableEntry o) {
        if (this.points != o.points) {
            return Integer.compare(points, o.points);
        } else {
            return Integer.compare(scored - received, o.scored - o.received);
        }
    }

    public void resetResults(){
        setPoints(0);
        setScored(0);
        setReceived(0);
    }

    @Override
    public String toString() {
        return String.format("%-28s %d:%d\t%d", club, scored, received, points);
    }
}
