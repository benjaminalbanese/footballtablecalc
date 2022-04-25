package org.jadice.playground;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Group {
    private final String groupName;
    private final int groupOrderID;
    private final int groupID;
}