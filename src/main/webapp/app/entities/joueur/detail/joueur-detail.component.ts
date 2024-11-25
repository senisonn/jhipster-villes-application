import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IJoueur } from '../joueur.model';

@Component({
  standalone: true,
  selector: 'jhi-joueur-detail',
  templateUrl: './joueur-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class JoueurDetailComponent {
  joueur = input<IJoueur | null>(null);

  previousState(): void {
    window.history.back();
  }
}
