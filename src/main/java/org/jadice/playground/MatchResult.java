package org.jadice.playground;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchResult {
    private final int resultID;
    private final String resultName;
    private final int pointsTeam1;
    private final int pointsTeam2;
    private final int resultOrderID;
    private final int resultTypeID;
    private final String resultDescription;
}
