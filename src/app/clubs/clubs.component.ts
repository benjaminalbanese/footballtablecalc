import {Component, OnInit} from '@angular/core';
import {Club} from '../club/club';
import {ClubService} from "./club.service";
import {TableEntry} from "../tableEntry/tableEntry";

@Component({
  selector: 'app-clubs',
  templateUrl: './clubs.component.html',
  styleUrls: ['./clubs.component.scss']
})
export class ClubsComponent implements OnInit {

  clubs: Club[] = [];
  tableEntries: TableEntry[] = [];


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
      });

    })
  }
}
