import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IJoueur, NewJoueur } from '../joueur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IJoueur for edit and NewJoueurFormGroupInput for create.
 */
type JoueurFormGroupInput = IJoueur | PartialWithRequiredKeyOf<NewJoueur>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IJoueur | NewJoueur> = Omit<T, 'dateInscription'> & {
  dateInscription?: string | null;
};

type JoueurFormRawValue = FormValueOf<IJoueur>;

type NewJoueurFormRawValue = FormValueOf<NewJoueur>;

type JoueurFormDefaults = Pick<NewJoueur, 'id' | 'dateInscription' | 'estAdministrateur'>;

type JoueurFormGroupContent = {
  id: FormControl<JoueurFormRawValue['id'] | NewJoueur['id']>;
  pseudo: FormControl<JoueurFormRawValue['pseudo']>;
  motDePasse: FormControl<JoueurFormRawValue['motDePasse']>;
  dateInscription: FormControl<JoueurFormRawValue['dateInscription']>;
  estAdministrateur: FormControl<JoueurFormRawValue['estAdministrateur']>;
  ville: FormControl<JoueurFormRawValue['ville']>;
};

export type JoueurFormGroup = FormGroup<JoueurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class JoueurFormService {
  createJoueurFormGroup(joueur: JoueurFormGroupInput = { id: null }): JoueurFormGroup {
    const joueurRawValue = this.convertJoueurToJoueurRawValue({
      ...this.getFormDefaults(),
      ...joueur,
    });
    return new FormGroup<JoueurFormGroupContent>({
      id: new FormControl(
        { value: joueurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      pseudo: new FormControl(joueurRawValue.pseudo),
      motDePasse: new FormControl(joueurRawValue.motDePasse),
      dateInscription: new FormControl(joueurRawValue.dateInscription),
      estAdministrateur: new FormControl(joueurRawValue.estAdministrateur),
      ville: new FormControl(joueurRawValue.ville),
    });
  }

  getJoueur(form: JoueurFormGroup): IJoueur | NewJoueur {
    return this.convertJoueurRawValueToJoueur(form.getRawValue() as JoueurFormRawValue | NewJoueurFormRawValue);
  }

  resetForm(form: JoueurFormGroup, joueur: JoueurFormGroupInput): void {
    const joueurRawValue = this.convertJoueurToJoueurRawValue({ ...this.getFormDefaults(), ...joueur });
    form.reset(
      {
        ...joueurRawValue,
        id: { value: joueurRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): JoueurFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateInscription: currentTime,
      estAdministrateur: false,
    };
  }

  private convertJoueurRawValueToJoueur(rawJoueur: JoueurFormRawValue | NewJoueurFormRawValue): IJoueur | NewJoueur {
    return {
      ...rawJoueur,
      dateInscription: dayjs(rawJoueur.dateInscription, DATE_TIME_FORMAT),
    };
  }

  private convertJoueurToJoueurRawValue(
    joueur: IJoueur | (Partial<NewJoueur> & JoueurFormDefaults),
  ): JoueurFormRawValue | PartialWithRequiredKeyOf<NewJoueurFormRawValue> {
    return {
      ...joueur,
      dateInscription: joueur.dateInscription ? joueur.dateInscription.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
