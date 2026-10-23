import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  inject,
  signal,
} from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { BrowserMultiFormatReader, IScannerControls } from '@zxing/browser';
import { BarcodeFormat, DecodeHintType } from '@zxing/library';

const SCANNED_FORMATS = [
  BarcodeFormat.EAN_8,
  BarcodeFormat.EAN_13,
  BarcodeFormat.CODE_128,
  BarcodeFormat.CODE_39,
  BarcodeFormat.QR_CODE,
];

/** Opens the phone camera and closes with the first barcode it reads (the dialog result is its text). */
@Component({
  selector: 'app-barcode-scanner-modal',
  imports: [MatDialogTitle, MatDialogContent, MatDialogActions, MatDialogClose, MatButton],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './barcode-scanner-modal.component.html',
  styleUrl: './barcode-scanner-modal.component.scss',
})
export class BarcodeScannerModalComponent implements AfterViewInit, OnDestroy {
  private dialogRef = inject<MatDialogRef<BarcodeScannerModalComponent, string>>(MatDialogRef);

  @ViewChild('video') video!: ElementRef<HTMLVideoElement>;

  protected error = signal<string | null>(null);

  private controls?: IScannerControls;
  private destroyed = false;

  async ngAfterViewInit() {
    const hints = new Map<DecodeHintType, unknown>([
      [DecodeHintType.POSSIBLE_FORMATS, SCANNED_FORMATS],
    ]);
    const reader = new BrowserMultiFormatReader(hints);

    try {
      const controls = await reader.decodeFromConstraints(
        { video: { facingMode: 'environment' } },
        this.video.nativeElement,
        (result) => {
          if (result && !this.destroyed) {
            this.dialogRef.close(result.getText());
          }
        },
      );

      if (this.destroyed) {
        controls.stop();
      } else {
        this.controls = controls;
      }
    } catch {
      this.error.set(
        'Nem sikerült elindítani a kamerát. Engedélyezd a kamera használatát a böngészőben!',
      );
    }
  }

  ngOnDestroy() {
    this.destroyed = true;
    this.controls?.stop();
  }
}
