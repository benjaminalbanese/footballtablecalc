import {Component, OnInit} from '@angular/core';
import {Club} from '../club/club';
import {ClubService} from "./club.service";
import {TableEntry} from "../tableEntry/tableEntry";
import {Probability} from "../probability/probability";

@Component({
  selector: 'app-clubs',
  templateUrl: './clubs.component.html',
  styleUrls: ['./clubs.component.scss']
})
export class ClubsComponent implements OnInit {

  clubs: Club[] = [];
  tableEntries: TableEntry[] = [];
  probabilities: Probability[] = [];


  constructor(private clubService: ClubService) {
  }

  ngOnInit(): void {
    // populate club list on server and display current table if successful
    this.clubService.populateList().subscribe(fetchedTableEntries => {
      console.log(fetchedTableEntries);
      this.tableEntries = fetchedTableEntries;
      this.clubService.getClubs().subscribe(fetchedClubs => {
        this.clubs = fetchedClubs;

        console.log(fetchedClubs)
        this.clubService.getProbabilities('FC Schalke 04').subscribe(fetchedProbabilities => {
          this.probabilities = fetchedProbabilities
          console.log(fetchedProbabilities);
        });
      });
    })
  }
}
