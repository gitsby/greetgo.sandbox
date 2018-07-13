
import { fakeAsync, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientRecordsComponent } from './client-records.component';

describe('ClientRecordsComponent', () => {
  let component: ClientRecordsComponent;
  let fixture: ComponentFixture<ClientRecordsComponent>;

  beforeEach(fakeAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ClientRecordsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClientRecordsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should compile', () => {
    expect(component).toBeTruthy();
  });
});
