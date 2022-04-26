package org.jadice.playground;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Table {
    private final ArrayList<TableEntry> tableEntries;

    public Table(ArrayList<TableEntry> tableEntries) {
        this.tableEntries = tableEntries;
    }

    public Table(Table other) {
        tableEntries = new ArrayList<>();
        for (TableEntry tableEntry : other.tableEntries) {
            tableEntries.add(new TableEntry(tableEntry));
        }
    }

    public void init() {
        tableEntries.clear();
    }

    public void initFromClubs(List<Club> clubs) {
        init();
        for (Club club : clubs) {
            tableEntries.add(new TableEntry(club));
        }
    }
    public ArrayList<TableEntry> getTableEntries(){
      return tableEntries;
    }


    public void calcFromMatches(List<Match> matches) {
        tableEntries.forEach(TableEntry::resetResults);
        for (Match match : matches) {
            TableEntry home = getTeam(tableEntries, match.getTeam1());
            TableEntry away = getTeam(tableEntries, match.getTeam2());

            Optional<MatchResult> finalResult = Arrays.stream(match.getMatchResults()).filter(m -> m.getResultName().equals("Endergebnis")).findFirst();
            finalResult.ifPresent(matchResult -> applyResult(home, away, matchResult));

        }
    }

    private void applyResult(TableEntry home, TableEntry away, MatchResult finalResult) {
        int goalsHome = finalResult.getPointsTeam1();
        int goalsAway = finalResult.getPointsTeam2();
        home.setScored(home.getScored() + goalsHome);
        home.setReceived(home.getReceived() + goalsAway);
        away.setScored(away.getScored() + goalsAway);
        away.setReceived(away.getReceived() + goalsHome);

        if (goalsAway == goalsHome) {
            home.setPoints(home.getPoints() + 1);
            away.setPoints(away.getPoints() + 1);
        } else if (goalsHome > goalsAway) {
            home.setPoints(home.getPoints() + 3);
        } else {
            away.setPoints(away.getPoints() + 3);
        }
    }

    public void finishRandom(List<Match> matches) {
        for (Match match : matches) {
            TableEntry home = getTeam(tableEntries, match.getTeam1());
            TableEntry away = getTeam(tableEntries, match.getTeam2());
            int[] ints = RandomResult.drawResult();
            applyResult(home, away, new MatchResult(0, "", ints[0], ints[1], 0, 0, ""));
        }
    }

    private TableEntry getTeam(ArrayList<TableEntry> entries, Club team) {
        Optional<TableEntry> tableEntry = entries.stream().filter(t -> t.getClub().getTeamName().equals(team.getTeamName())).findFirst();
        if (!tableEntry.isPresent()) {
            throw new IllegalArgumentException("tableEntry must be present");
        }
        return tableEntry.get();
    }

    public void sortEntries() {
        tableEntries.sort(Collections.reverseOrder());
    }

    public int getPlace(String teamName) {
        for (int i = 0; i < tableEntries.size(); i++) {
            if (tableEntries.get(i).getClub().getTeamName().equals(teamName)) {
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return tableEntries.stream().map(TableEntry::toString).collect(Collectors.joining("\n"));
    }
}
