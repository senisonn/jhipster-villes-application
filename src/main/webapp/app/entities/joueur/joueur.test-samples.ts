import dayjs from 'dayjs/esm';

import { IJoueur, NewJoueur } from './joueur.model';

export const sampleWithRequiredData: IJoueur = {
  id: 23441,
};

export const sampleWithPartialData: IJoueur = {
  id: 16938,
  pseudo: 'obediently',
  estAdministrateur: false,
};

export const sampleWithFullData: IJoueur = {
  id: 14738,
  pseudo: 'functional inside painfully',
  motDePasse: 'tensely',
  dateInscription: dayjs('2024-11-25T03:03'),
  estAdministrateur: false,
};

export const sampleWithNewData: NewJoueur = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
