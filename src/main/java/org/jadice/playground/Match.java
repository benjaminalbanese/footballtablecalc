package org.jadice.playground;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Match{
    private final int matchID;
    private final String matchDateTime;
    private final String timeZoneID;
    private final int leagueId;
    private final String leagueName;
    private final String leagueSeason;
    private final String leagueShortcut;
    private final String matchDateTimeUTC;
    private final Group group;
    private final Club team1;
    private final Club team2;
    private final String lastUpdateDateTime;
    private final boolean matchIsFinished;
    private MatchResult[] matchResults;
    private final Goal[] goals;
}

