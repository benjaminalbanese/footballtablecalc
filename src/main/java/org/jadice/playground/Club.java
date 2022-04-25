package org.jadice.playground;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Club {
  private long teamId;
  private String teamName;
  private String teamIconUrl;
  private String shortName;


  @Override
  public String toString() {
    return teamName;
  }
}
