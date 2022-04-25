package org.jadice.playground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class Season {
    private int startYear;
    private int endYear;
    private ArrayList<Club> clubs;
}
