import {Club} from "../club/club";

export interface TableEntry {
  club: Club;
  scored: number;
  received: number;
  points: number;
}
