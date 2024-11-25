import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJoueur } from '../joueur.model';
import { JoueurService } from '../service/joueur.service';

const joueurResolve = (route: ActivatedRouteSnapshot): Observable<null | IJoueur> => {
  const id = route.params.id;
  if (id) {
    return inject(JoueurService)
      .find(id)
      .pipe(
        mergeMap((joueur: HttpResponse<IJoueur>) => {
          if (joueur.body) {
            return of(joueur.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default joueurResolve;
