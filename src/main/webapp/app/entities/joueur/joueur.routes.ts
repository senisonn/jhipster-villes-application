import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import JoueurResolve from './route/joueur-routing-resolve.service';

const joueurRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/joueur.component').then(m => m.JoueurComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/joueur-detail.component').then(m => m.JoueurDetailComponent),
    resolve: {
      joueur: JoueurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/joueur-update.component').then(m => m.JoueurUpdateComponent),
    resolve: {
      joueur: JoueurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/joueur-update.component').then(m => m.JoueurUpdateComponent),
    resolve: {
      joueur: JoueurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default joueurRoute;
