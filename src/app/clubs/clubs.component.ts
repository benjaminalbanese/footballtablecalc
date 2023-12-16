import {Component, OnInit} from '@angular/core';
import {Club} from '../club/club';
import {ClubService} from "./club.service";
import {TableEntry} from "../tableEntry/tableEntry";
import {ClubPlace} from "../probability/clubPlace";
import {Competition} from "../probability/Competitions";

@Component({
  selector: 'app-clubs',
  templateUrl: './clubs.component.html',
  styleUrls: ['./clubs.component.scss']
})
export class ClubsComponent implements OnInit {

  clubs: Club[] = [];
  tableEntries: TableEntry[] = [];
  probabilities: ClubPlace[] = [];
  competitions: Competition[] = [];

  constructor(private clubService: ClubService) {
  }

  ngOnInit(): void {

    // populate club list on server and display current table if successful
    this.clubService.populateList().subscribe(fetchedTableEntries => {
      this.tableEntries = fetchedTableEntries;
      this.clubService.getSeasons().subscribe(fetchedSeasons => {
        console.log(fetchedSeasons)
      });
      this.clubService.getClubs().subscribe(fetchedClubs => {
        this.clubs = fetchedClubs;
        this.clubService.getProbabilities().subscribe(fetchedProbabilities => {
          this.probabilities = fetchedProbabilities;
          this.sortTable(fetchedProbabilities);

          for (let fetchedClub of fetchedClubs) {
            this.competitions.push(new Competition(fetchedClub));

            fetchedProbabilities.filter(prob => prob.club === fetchedClub.teamName).map(prob => {
              this.categorizeIntoCompetitions(fetchedClub, prob);
            });
          }
          this.sortCategorizedTables();
        });
      });
    })
  }

  private sortCategorizedTables() {
    this.competitions.sort((compA, compB) =>{
      let {teamName: teamA} = compA.club;
      let teamB = compB.club.teamName;

      let indexA = this.tableEntries.findIndex(clubPlace => clubPlace.club.teamName === teamA);
      let indexB = this.tableEntries.findIndex(clubPlace => clubPlace.club.teamName === teamB);

      return indexA-indexB;
    })
  }

  private sortTable(fetchedProbabilities: ClubPlace[]) {
    fetchedProbabilities.sort((a, b) => {
      if (a.place - b.place != 0)
        return a.place - b.place;
      else
        return b.count - a.count;
    });
  }

  private categorizeIntoCompetitions(fetchedClub: Club, prob: ClubPlace) {
    let club = this.competitions.find(competition => competition.club === fetchedClub);
    if (club === undefined) {
      return;
    }
    if (prob.place === 1) {
      club.champion = prob.count;
    } else if (prob.place <= 4) {
      club.championsLeague += prob.count;
    } else if (prob.place <= 6) {
      club.european += prob.count;
    } else if (prob.place === 7) {
      club.optionalEuropean = prob.count;
    } else if (prob.place < 16) {
      club.midfield += prob.count;
    } else if (prob.place === 16) {
      club.relegation = prob.count;
    } else {
      club.secondLeague += prob.count;
    }
  }
}
