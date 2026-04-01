import {Component} from '@angular/core';
import {SidebarComponent} from '../../components/sidebar/sidebar.component';
import {MatButton} from '@angular/material/button';
import {CategoryService} from '../../services/category.service';
import {RouterLink, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-main-layout',
  imports: [
    SidebarComponent,
    MatButton,
    RouterOutlet
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {

  constructor(private categoryService: CategoryService) {
  }

  protected getCategories() {

    this.categoryService.getCategories().subscribe(categories => {
      console.log(categories);
    })
  }


}
