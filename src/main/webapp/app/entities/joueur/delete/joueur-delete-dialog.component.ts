import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IJoueur } from '../joueur.model';
import { JoueurService } from '../service/joueur.service';

@Component({
  standalone: true,
  templateUrl: './joueur-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class JoueurDeleteDialogComponent {
  joueur?: IJoueur;

  protected joueurService = inject(JoueurService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.joueurService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
