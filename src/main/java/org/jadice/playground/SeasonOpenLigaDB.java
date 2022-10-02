package org.jadice.playground;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SeasonOpenLigaDB {
  private int leagueId;
  private String leagueName;
  private String leagueShortcut;
  private String leagueSeason;
  private SportOpenLigaDB sport;
}
