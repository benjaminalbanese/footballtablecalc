import {Component, OnInit} from '@angular/core';
import {Club} from '../club/club';
import {ClubService} from "./club.service";
import {TableEntry} from "../tableEntry/tableEntry";
import {ClubPlace} from "../probability/clubPlace";
import {filter, tap, reduce, from, map, distinct} from "rxjs";

@Component({
  selector: 'app-clubs',
  templateUrl: './clubs.component.html',
  styleUrls: ['./clubs.component.scss']
})
export class ClubsComponent implements OnInit {

  clubs: Club[] = [];
  tableEntries: TableEntry[] = [];
  probabilities: ClubPlace[] = [];


  constructor(private clubService: ClubService) {
  }

  doStuff(filteredClub: string, fetchedClubPlaces: ClubPlace[]): void {
    let teams = from (fetchedClubPlaces);
    const ersteLiga = teams.pipe(
      filter((team) => team.club === filteredClub),
      filter((team) => team.place <= 15),
      map((club) => club.count),
      reduce((total, actual) => total + actual, 0)
    );
    const relegation = teams.pipe(
      filter((team) => team.club === filteredClub),
      filter((team) => team.place === 16),
      map((club) => club.count),
      reduce((total, actual) => total + actual, 0)
    );
    const abstieg = teams.pipe(
      filter((team) => team.club === filteredClub),
      filter((team) => team.place >= 17),
      map((club) => club.count),
      reduce((total, actual) => total + actual, 0)
    );
    console.log(filteredClub);
    ersteLiga.subscribe((x) => console.log('erste Liga ' + x / 1000));
    relegation.subscribe((x) => console.log('Relegation ' + x / 1000));
    abstieg.subscribe((x) => console.log('Abstieg ' + x / 1000));
  }

  ngOnInit(): void {
    // populate club list on server and display current table if successful
    this.clubService.populateList().subscribe(fetchedTableEntries => {
      console.log(fetchedTableEntries);
      this.tableEntries = fetchedTableEntries;
      let seasonsObservable = this.clubService.getSeasons().pipe(
        tap(val => console.log("candidate" + val.leagueSeason)),
        filter(val => val.leagueSeason.startsWith("202"))
      );

      const seasonsSubscription = seasonsObservable.subscribe(val => console.log("selected: " + val.leagueSeason));
      this.clubService.getClubs().subscribe(fetchedClubs => {
        this.clubs = fetchedClubs;

        console.log(fetchedClubs)
        this.clubService.getProbabilities().subscribe(fetchedProbabilities => {
          this.probabilities = fetchedProbabilities;
          // fetchedProbabilities.sort((a, b) => {
          //   if (a.place - b.place != 0)
          //     return a.place - b.place;
          //   else
          //     return b.count - a.count;
          // });
          // console.log(fetchedProbabilities);

          const clubList = from(this.probabilities).pipe(
            filter((team) => team.place >= 16),
            map((team) => team.club),
            distinct()
          );
          clubList.subscribe((team) => this.doStuff(team, this.probabilities));

        });
      });
    })
  }
}
