import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LOCALE_ID, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {RouterModule} from '@angular/router';

import {AppRoutingModule} from './app.routing';
import {ComponentsModule} from './components/components.module';

import {AppComponent} from './app.component';
import {AdminLayoutComponent} from './layouts/admin-layout/admin-layout.component';
import {RentsComponent} from './rents/rents.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MAT_DATE_LOCALE, MatNativeDateModule} from '@angular/material/core';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {EditDeviceModalComponent} from './edit-device-modal/edit-device-modal.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {EditRentComponent} from './edit-rent/edit-rent.component';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {LoginComponent} from './login/login.component';
import {DeviceToRentModalComponent} from './device-to-rent-modal/device-to-rent-modal.component';
import {EditCompositeModalComponent} from './edit-composite-modal/edit-composite-modal.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthInterceptor} from './helpers/auth.interceptor';
import {ConfirmDialogComponent} from './confirm-dialog/confirm-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {PdfGenerationModalComponent} from './pdf-generation-modal/pdf-generation-modal.component';
import {MatRadioModule} from '@angular/material/radio';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatDividerModule} from '@angular/material/divider';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatPaginatorModule} from '@angular/material/paginator';
import { TicketsComponent } from './tickets/tickets.component';
import { EditProjectComponent } from './edit-project/edit-project.component';

@NgModule({
    imports: [
        BrowserAnimationsModule,
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        ComponentsModule,
        RouterModule,
        AppRoutingModule,
        MatFormFieldModule,
        MatInputModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatCheckboxModule,
        MatAutocompleteModule,
        NgbModule,
        MatButtonModule,
        MatIconModule,
        MatToolbarModule,
        HttpClientModule,
        MatDialogModule,
        MatRadioModule,
        MatSlideToggleModule,
        MatProgressSpinnerModule,
        MatSelectModule,
        MatTooltipModule,
        MatDividerModule,
        MatExpansionModule,
        MatPaginatorModule,
    ],
    declarations: [
        AppComponent,
        AdminLayoutComponent,
        RentsComponent,
        EditDeviceModalComponent,
        EditRentComponent,
        LoginComponent,
        DeviceToRentModalComponent,
        EditCompositeModalComponent,
        ConfirmDialogComponent,
        PdfGenerationModalComponent,
        TicketsComponent,
        EditProjectComponent,
    ],
    providers: [
        {provide: MAT_DATE_LOCALE, useValue: 'hu-HU'},
        {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
