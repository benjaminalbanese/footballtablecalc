import {SportOpenLigaDB} from "./SportOpenLigaDB";

export interface Season{
  leagueId : number;
  leagueName : string;
  leagueShortcut : string;
  leagueSeason : string;
  sport : SportOpenLigaDB;
  }
