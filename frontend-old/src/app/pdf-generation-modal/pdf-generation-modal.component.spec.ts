import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PdfGenerationModalComponent } from './pdf-generation-modal.component';

describe('PdfGenerationModalComponent', () => {
  let component: PdfGenerationModalComponent;
  let fixture: ComponentFixture<PdfGenerationModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdfGenerationModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdfGenerationModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
