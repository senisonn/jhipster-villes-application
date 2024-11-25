import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IVille } from '../ville.model';

@Component({
  standalone: true,
  selector: 'jhi-ville-detail',
  templateUrl: './ville-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class VilleDetailComponent {
  ville = input<IVille | null>(null);

  previousState(): void {
    window.history.back();
  }
}
