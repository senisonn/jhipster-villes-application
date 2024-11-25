import { IVille, NewVille } from './ville.model';

export const sampleWithRequiredData: IVille = {
  id: 13250,
};

export const sampleWithPartialData: IVille = {
  id: 1397,
  nbHabitants: 1322,
};

export const sampleWithFullData: IVille = {
  id: 18108,
  nom: 'whenever hypothesise',
  codePostal: 'which',
  nbHabitants: 6612,
};

export const sampleWithNewData: NewVille = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
