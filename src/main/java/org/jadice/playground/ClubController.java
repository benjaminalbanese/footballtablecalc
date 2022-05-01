package org.jadice.playground;


import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import static io.micrometer.core.instrument.Metrics.timer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.gson.Gson;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

// CORS / Cross-Origin : open up 3000 for react and 4200 for angular
@CrossOrigin(origins = {"http://127.0.0.1:3000", "http://localhost:3000", "http://127.0.0.1:4200", "http://localhost:4200"})
@RestController
public class ClubController {
  private final AtomicLong counter = new AtomicLong();
  private final Clubs clubs;
  private final Table table = new Table(new ArrayList<>());
  private final ArrayList<Match> matches = new ArrayList<>();
  private final Gson gson = new Gson();

  @Autowired
  public ClubController(Clubs clubs) {
    this.clubs = clubs;
  }

  // see https://www.baeldung.com/spring-boot-json
  @PostMapping("/addClub")
  public ResponseEntity<Club> addClub(@RequestBody Club club) {
    if (club == null)
      return ResponseEntity.notFound().build();
    else {
      long newId = counter.incrementAndGet();
      club.setTeamId(newId);
      URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/getClub?={id}")
        .buildAndExpand(newId)
        .toUri();
      clubs.addClubs(Collections.singletonList(club));
      return ResponseEntity.created(uri).body(club);

    }
  }

  @DeleteMapping("/deleteClub")
  public ResponseEntity<Club> deleteClub(@RequestBody Club club) {
    return new ResponseEntity<>(clubs.deleteClub(club) ? OK : NOT_FOUND);
  }

  @RequestMapping("/getClub")
  public ResponseEntity<Club> getClub(@RequestParam(value = "id") long id) {
    Club club = clubs.getClub(id);
    if (club != null)
      return new ResponseEntity<>(club, OK);
    else
      return new ResponseEntity<>(NOT_FOUND);
  }

  @RequestMapping("/getClubList")
  public ResponseEntity<ArrayList<Club>> getClubList() {
    return new ResponseEntity(clubs.getClubs(), OK);
  }

  @PutMapping("/updateClub")
  public ResponseEntity<Club> updateClub(
    @RequestParam(value = "id") long id,
    @RequestParam(value = "name", defaultValue = "VfB Stuttgart") String name,
    @RequestParam(value = "founded", defaultValue = "1893-09-09") String founded,
    @RequestParam(value = "manager", defaultValue = "Tim Walter") String manager) {

    Club club = new Club(id, name, founded, manager);
    return new ResponseEntity<>(club, clubs.updateClub(club) ? OK : BAD_REQUEST);
  }

  @PutMapping("/updateClubJson")
  public ResponseEntity<Club> updateClubJson(@RequestBody Club club) {
    return new ResponseEntity<>(club, clubs.updateClub(club) ? OK : BAD_REQUEST);
  }

  private Object[] getFromUrl(String url, Class<?> clazz) throws IOException {
    URL urlObject = new URL(url);
    InputStream inputStream = urlObject.openStream();
    String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    return (Object[]) gson.fromJson(content, clazz);
  }

  @RequestMapping("/populateList")
  public ResponseEntity<ArrayList<TableEntry>> populateList() {
    counter.set(0);
    clubs.clear();
    table.init();
    matches.clear();

    try {
      matches.addAll(Arrays.asList((Match[]) getFromUrl("https://api.openligadb.de/getmatchdata/bl1/2021", Match[].class)));
      clubs.addClubs(Arrays.asList((Club[]) getFromUrl("https://api.openligadb.de/getavailableteams/bl1/2021", Club[].class)));
      for (Club club : clubs.getClubs()) {
        long id = club.getTeamId();
        if (id >= counter.get()) {
          counter.set(id + 1);
        }
      }
      table.initFromClubs(clubs.getClubs());

      table.calcFromMatches(matches);
      table.sortEntries();
      System.out.println(table);

      return getCurrentTable();
    } catch (IOException e) {
      return new ResponseEntity<>(new ArrayList<>(), INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping("/getCurrentTable")
  public ResponseEntity<ArrayList<TableEntry>> getCurrentTable() {
    table.sortEntries();
    System.out.println(table);

    return new ResponseEntity<>(table.getTableEntries(), OK);
  }

  @SuppressWarnings("rawtypes")
  @RequestMapping("/setMatch")
  public ResponseEntity<ArrayList<ClubPlace>> setMatch(@RequestParam String home, @RequestParam String away) {
    Optional<Match> first = matches.stream().filter(m -> m.getTeam1().getTeamName().equals(home) && m.getTeam2().getTeamName().equals(away)).findFirst();
    if (!first.isPresent())
      return new ResponseEntity<>(NOT_FOUND);
    Match match = first.get();
    MatchResult matchResult = new MatchResult(2, "Endergebnis", 2, 0, 0, 0, "");
    MatchResult[] matchResults = new MatchResult[]{matchResult};
    match.setMatchResults(matchResults);
    table.calcFromMatches(matches);
    table.sortEntries();
    return new ResponseEntity(gson.toJson(table), OK);
  }

  final AtomicInteger currentRandomCount = Metrics.gauge("org.jadice.currentRandomCount", new AtomicInteger());

  @SuppressWarnings("rawtypes")
  @RequestMapping("/finishRemainingRandomly")
  public ResponseEntity finishRemainingRandomly() {

    Sample sample = Timer.start();
    populateList();

    HashMap<String, HashMap<Integer, Integer>> places = new HashMap<>();
    for (Club clubsClub : clubs.getClubs()) {
      places.put(clubsClub.getTeamName(), new HashMap<>());
    }

    ArrayList<Match> remaining = new ArrayList<>();
    for (Match match : matches) {
      MatchResult[] matchResults = match.getMatchResults();
      boolean found = false;
      for (MatchResult matchResult : matchResults) {
        if (matchResult.getResultName().equals("Endergebnis")) {
          found = true;
          break;
        }
      }
      if (!found)
        remaining.add(match);
    }

    Table randomTable;
    for (int i = 0; i < 100000; i++) {
      randomTable = new Table(table);
      randomTable.finishRandom(remaining);
      randomTable.sortEntries();
      for (Club club : clubs.getClubs()) {
        int index = randomTable.getPlace(club.getTeamName());
        HashMap<Integer, Integer> clubPlaces = places.get(club.getTeamName());

        clubPlaces.merge(index, 1, Integer::sum);
        if (i % 1000 == 0) {
          currentRandomCount.set(i);
        }

      }
    }
    Timer timer = timer("org.jadice.randomTable");
    sample.stop(timer);
    ArrayList<ClubPlace> returnValue = new ArrayList<>();
    places.forEach((k, v) -> {
      System.out.println("Club: " + k);

      v.forEach((pk, pv) -> {
        System.out.printf("place %d, count %d\n", pk, pv);
        returnValue.add(new ClubPlace(k, pk, pv));
      });
    });

    return new ResponseEntity<>(gson.toJson(returnValue), OK);
  }
}

