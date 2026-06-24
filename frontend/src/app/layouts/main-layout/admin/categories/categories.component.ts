import {Component} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard} from "@angular/material/card";
import {MatFabButton, MatIconButton} from "@angular/material/button";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {CategoryDetails} from '../../../../model/category/categoryDetails';
import {CategoryService} from '../../../../services/category.service';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatSort} from '@angular/material/sort';
import {DatePipe} from '@angular/common';


const COLUMNS: string[] = ['name', 'assignedScannables', 'createdAt', 'createdBy'];

@Component({
  selector: 'app-categories',
  imports: [
    FormsModule,
    MatCard,
    MatFabButton,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatSuffix,
    ReactiveFormsModule,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatProgressSpinner,
    MatRow,
    MatRowDef,
    MatSort,
    MatTable,
    MatHeaderCellDef,
    DatePipe
  ],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.scss',
})
export class CategoriesComponent {

  protected categorySearchFormControl = new FormControl();

  protected categories: CategoryDetails[] = [];
  protected loading = true;
  protected columns = COLUMNS;

  constructor(
    private categoryService: CategoryService,
  ) {

  }

  ngOnInit(): void {
    this.categoryService.getCategories().subscribe((data) => {
      this.categories = data;
      this.loading = false;
    })
  }


  protected applyFilter($event: KeyboardEvent) {

  }

  protected newCategory() {

  }

  protected openCategory(category: CategoryDetails) {
    console.log(category)
  }
}
