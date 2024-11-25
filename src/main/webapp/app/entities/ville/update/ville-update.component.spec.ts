import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IRegion } from 'app/entities/region/region.model';
import { RegionService } from 'app/entities/region/service/region.service';
import { VilleService } from '../service/ville.service';
import { IVille } from '../ville.model';
import { VilleFormService } from './ville-form.service';

import { VilleUpdateComponent } from './ville-update.component';

describe('Ville Management Update Component', () => {
  let comp: VilleUpdateComponent;
  let fixture: ComponentFixture<VilleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let villeFormService: VilleFormService;
  let villeService: VilleService;
  let regionService: RegionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [VilleUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(VilleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(VilleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    villeFormService = TestBed.inject(VilleFormService);
    villeService = TestBed.inject(VilleService);
    regionService = TestBed.inject(RegionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Region query and add missing value', () => {
      const ville: IVille = { id: 456 };
      const region: IRegion = { id: 1692 };
      ville.region = region;

      const regionCollection: IRegion[] = [{ id: 3789 }];
      jest.spyOn(regionService, 'query').mockReturnValue(of(new HttpResponse({ body: regionCollection })));
      const additionalRegions = [region];
      const expectedCollection: IRegion[] = [...additionalRegions, ...regionCollection];
      jest.spyOn(regionService, 'addRegionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      expect(regionService.query).toHaveBeenCalled();
      expect(regionService.addRegionToCollectionIfMissing).toHaveBeenCalledWith(
        regionCollection,
        ...additionalRegions.map(expect.objectContaining),
      );
      expect(comp.regionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ville: IVille = { id: 456 };
      const region: IRegion = { id: 3163 };
      ville.region = region;

      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      expect(comp.regionsSharedCollection).toContain(region);
      expect(comp.ville).toEqual(ville);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVille>>();
      const ville = { id: 123 };
      jest.spyOn(villeFormService, 'getVille').mockReturnValue(ville);
      jest.spyOn(villeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ville }));
      saveSubject.complete();

      // THEN
      expect(villeFormService.getVille).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(villeService.update).toHaveBeenCalledWith(expect.objectContaining(ville));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVille>>();
      const ville = { id: 123 };
      jest.spyOn(villeFormService, 'getVille').mockReturnValue({ id: null });
      jest.spyOn(villeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ville }));
      saveSubject.complete();

      // THEN
      expect(villeFormService.getVille).toHaveBeenCalled();
      expect(villeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IVille>>();
      const ville = { id: 123 };
      jest.spyOn(villeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ville });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(villeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareRegion', () => {
      it('Should forward to regionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(regionService, 'compareRegion');
        comp.compareRegion(entity, entity2);
        expect(regionService.compareRegion).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
