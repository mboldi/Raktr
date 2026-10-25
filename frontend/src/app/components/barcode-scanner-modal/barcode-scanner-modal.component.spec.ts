import { NgZone, provideZoneChangeDetection } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';
import { BrowserMultiFormatReader, IScannerControls } from '@zxing/browser';

import { BarcodeScannerModalComponent } from './barcode-scanner-modal.component';

const flushAsync = () => new Promise((resolve) => setTimeout(resolve));

describe('BarcodeScannerModalComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BarcodeScannerModalComponent],
      providers: [
        // as in the app, zone.js reports unhandled rejections through NgZone.onError
        provideZoneChangeDetection(),
        { provide: MatDialogRef, useValue: { close: vi.fn() } },
      ],
    }).compileComponents();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('does not leave a rejected stop() unhandled when it closes', async () => {
    let stopCalls = 0;
    // not a vi.fn(): that attaches its own handler to the promise it returns
    const controls = {
      stop: () => {
        stopCalls++;
        return Promise.reject(new Error('torch off failed'));
      },
    } as unknown as IScannerControls;
    vi.spyOn(BrowserMultiFormatReader.prototype, 'decodeFromConstraints').mockResolvedValue(
      controls,
    );

    const zone = TestBed.inject(NgZone);
    const zoneErrors: unknown[] = [];
    zone.onError.subscribe((error) => zoneErrors.push(error));

    const fixture = TestBed.createComponent(BarcodeScannerModalComponent);
    fixture.detectChanges();
    await flushAsync();

    zone.run(() => fixture.destroy());
    await flushAsync();

    expect(stopCalls).toBe(1);
    expect(zoneErrors).toEqual([]);
  });
});
