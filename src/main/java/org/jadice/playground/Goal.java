package org.jadice.playground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Goal {
    private final int goalID;
    private final int scoreTeam1;
    private final int scoreTeam2;
    private final int matchMinute;
    private final int goalGetterID;
    private final String goalGetterName;
    private final boolean isPenalty;
    private final boolean isOwnGoal;
    private final boolean isOvertime;
    private final String comment;
}
