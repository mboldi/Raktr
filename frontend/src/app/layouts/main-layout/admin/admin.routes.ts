import {Routes} from "@angular/router";
import {CategoriesComponent} from "./categories/categories.component";
import {LocationsComponent} from "./locations/locations.component";
import {SignersComponent} from "./signers/signers.component";
import {OwnersComponent} from "./owners/owners.component";
import {ExportImportComponent} from "./export-import/export-import.component";

export const ADMIN_ROUTES: Routes = [
    {path: '', redirectTo: 'categories', pathMatch: 'full'},
    {path: 'categories', component: CategoriesComponent},
    {path: 'locations', component: LocationsComponent},
    {path: 'signers', component: SignersComponent},
    {path: 'owners', component: OwnersComponent},
    {path: 'export-import', component: ExportImportComponent},
];