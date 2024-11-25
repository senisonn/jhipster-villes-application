import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '23b72bf2-a02b-4853-b7ca-4e91a86228ba',
};

export const sampleWithPartialData: IAuthority = {
  name: '6546b5f4-2420-4d3d-90f8-0f67471be815',
};

export const sampleWithFullData: IAuthority = {
  name: 'fd6be0ed-664b-4a50-8d82-bdeb7b99f04d',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
