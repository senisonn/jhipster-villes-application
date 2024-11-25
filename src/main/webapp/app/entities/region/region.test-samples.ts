import { IRegion, NewRegion } from './region.model';

export const sampleWithRequiredData: IRegion = {
  id: 10723,
};

export const sampleWithPartialData: IRegion = {
  id: 339,
  nom: 'soggy tightly bid',
};

export const sampleWithFullData: IRegion = {
  id: 1669,
  nom: 'rationale',
};

export const sampleWithNewData: NewRegion = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
