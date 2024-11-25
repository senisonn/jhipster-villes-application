import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IVille } from 'app/entities/ville/ville.model';
import { VilleService } from 'app/entities/ville/service/ville.service';
import { IJoueur } from '../joueur.model';
import { JoueurService } from '../service/joueur.service';
import { JoueurFormGroup, JoueurFormService } from './joueur-form.service';

@Component({
  standalone: true,
  selector: 'jhi-joueur-update',
  templateUrl: './joueur-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class JoueurUpdateComponent implements OnInit {
  isSaving = false;
  joueur: IJoueur | null = null;

  villesSharedCollection: IVille[] = [];

  protected joueurService = inject(JoueurService);
  protected joueurFormService = inject(JoueurFormService);
  protected villeService = inject(VilleService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: JoueurFormGroup = this.joueurFormService.createJoueurFormGroup();

  compareVille = (o1: IVille | null, o2: IVille | null): boolean => this.villeService.compareVille(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ joueur }) => {
      this.joueur = joueur;
      if (joueur) {
        this.updateForm(joueur);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const joueur = this.joueurFormService.getJoueur(this.editForm);
    if (joueur.id !== null) {
      this.subscribeToSaveResponse(this.joueurService.update(joueur));
    } else {
      this.subscribeToSaveResponse(this.joueurService.create(joueur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJoueur>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(joueur: IJoueur): void {
    this.joueur = joueur;
    this.joueurFormService.resetForm(this.editForm, joueur);

    this.villesSharedCollection = this.villeService.addVilleToCollectionIfMissing<IVille>(this.villesSharedCollection, joueur.ville);
  }

  protected loadRelationshipsOptions(): void {
    this.villeService
      .query()
      .pipe(map((res: HttpResponse<IVille[]>) => res.body ?? []))
      .pipe(map((villes: IVille[]) => this.villeService.addVilleToCollectionIfMissing<IVille>(villes, this.joueur?.ville)))
      .subscribe((villes: IVille[]) => (this.villesSharedCollection = villes));
  }
}
