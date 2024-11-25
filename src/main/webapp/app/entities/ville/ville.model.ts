import { IRegion } from 'app/entities/region/region.model';

export interface IVille {
  id: number;
  nom?: string | null;
  codePostal?: string | null;
  nbHabitants?: number | null;
  region?: IRegion | null;
}

export type NewVille = Omit<IVille, 'id'> & { id: null };
