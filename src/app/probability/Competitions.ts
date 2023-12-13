import {Club} from "../club/club";

export class Competition {
  constructor(public club: Club,
              public champion = 0,
              public championsLeague = 0,
              public european = 0,
              public optionalEuropean = 0,
              public midfield = 0,
              public relegation = 0,
              public secondLeague = 0) {
  }
}

