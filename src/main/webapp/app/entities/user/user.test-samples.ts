import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 26555,
  login: '7A@',
};

export const sampleWithPartialData: IUser = {
  id: 21480,
  login: 'L00G',
};

export const sampleWithFullData: IUser = {
  id: 2722,
  login: '3wJX',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
