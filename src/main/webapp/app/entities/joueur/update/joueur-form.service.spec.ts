import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../joueur.test-samples';

import { JoueurFormService } from './joueur-form.service';

describe('Joueur Form Service', () => {
  let service: JoueurFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JoueurFormService);
  });

  describe('Service methods', () => {
    describe('createJoueurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJoueurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            pseudo: expect.any(Object),
            motDePasse: expect.any(Object),
            dateInscription: expect.any(Object),
            estAdministrateur: expect.any(Object),
            ville: expect.any(Object),
          }),
        );
      });

      it('passing IJoueur should create a new form with FormGroup', () => {
        const formGroup = service.createJoueurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            pseudo: expect.any(Object),
            motDePasse: expect.any(Object),
            dateInscription: expect.any(Object),
            estAdministrateur: expect.any(Object),
            ville: expect.any(Object),
          }),
        );
      });
    });

    describe('getJoueur', () => {
      it('should return NewJoueur for default Joueur initial value', () => {
        const formGroup = service.createJoueurFormGroup(sampleWithNewData);

        const joueur = service.getJoueur(formGroup) as any;

        expect(joueur).toMatchObject(sampleWithNewData);
      });

      it('should return NewJoueur for empty Joueur initial value', () => {
        const formGroup = service.createJoueurFormGroup();

        const joueur = service.getJoueur(formGroup) as any;

        expect(joueur).toMatchObject({});
      });

      it('should return IJoueur', () => {
        const formGroup = service.createJoueurFormGroup(sampleWithRequiredData);

        const joueur = service.getJoueur(formGroup) as any;

        expect(joueur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJoueur should not enable id FormControl', () => {
        const formGroup = service.createJoueurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJoueur should disable id FormControl', () => {
        const formGroup = service.createJoueurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
