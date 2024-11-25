import dayjs from 'dayjs/esm';
import { IVille } from 'app/entities/ville/ville.model';

export interface IJoueur {
  id: number;
  pseudo?: string | null;
  motDePasse?: string | null;
  dateInscription?: dayjs.Dayjs | null;
  estAdministrateur?: boolean | null;
  ville?: IVille | null;
}

export type NewJoueur = Omit<IJoueur, 'id'> & { id: null };
