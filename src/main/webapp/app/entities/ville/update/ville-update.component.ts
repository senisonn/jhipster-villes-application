import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IRegion } from 'app/entities/region/region.model';
import { RegionService } from 'app/entities/region/service/region.service';
import { IVille } from '../ville.model';
import { VilleService } from '../service/ville.service';
import { VilleFormGroup, VilleFormService } from './ville-form.service';

@Component({
  standalone: true,
  selector: 'jhi-ville-update',
  templateUrl: './ville-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class VilleUpdateComponent implements OnInit {
  isSaving = false;
  ville: IVille | null = null;

  regionsSharedCollection: IRegion[] = [];

  protected villeService = inject(VilleService);
  protected villeFormService = inject(VilleFormService);
  protected regionService = inject(RegionService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VilleFormGroup = this.villeFormService.createVilleFormGroup();

  compareRegion = (o1: IRegion | null, o2: IRegion | null): boolean => this.regionService.compareRegion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ville }) => {
      this.ville = ville;
      if (ville) {
        this.updateForm(ville);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ville = this.villeFormService.getVille(this.editForm);
    if (ville.id !== null) {
      this.subscribeToSaveResponse(this.villeService.update(ville));
    } else {
      this.subscribeToSaveResponse(this.villeService.create(ville));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVille>>): void {
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

  protected updateForm(ville: IVille): void {
    this.ville = ville;
    this.villeFormService.resetForm(this.editForm, ville);

    this.regionsSharedCollection = this.regionService.addRegionToCollectionIfMissing<IRegion>(this.regionsSharedCollection, ville.region);
  }

  protected loadRelationshipsOptions(): void {
    this.regionService
      .query()
      .pipe(map((res: HttpResponse<IRegion[]>) => res.body ?? []))
      .pipe(map((regions: IRegion[]) => this.regionService.addRegionToCollectionIfMissing<IRegion>(regions, this.ville?.region)))
      .subscribe((regions: IRegion[]) => (this.regionsSharedCollection = regions));
  }
}
