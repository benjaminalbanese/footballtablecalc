package org.jadice.playground;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class Clubs {
  private final ConcurrentHashMap<Long, Club> map = new ConcurrentHashMap<>();

  Club getClub(long id) {
    return map.get(id);
  }

  List<Club> getClubs() {
    return map.entrySet().stream().sorted(Comparator.comparingLong(Entry::getKey)).map(Entry::getValue).collect(Collectors.toList());
  }

  boolean deleteClub(Club toDelete) {
    Club remove = map.remove(toDelete.getTeamId());
    return remove != null;
  }

  boolean updateClub(Club updated) {
    if (updated == null)
      return false;
    long id = updated.getTeamId();
    map.put(id, updated);
    return true;
  }

  void addClubs(List<Club> clubs) {
    for (Club club : clubs) {
      map.put(club.getTeamId(), club);
    }
  }

  void clear() {
    map.clear();
  }
}
