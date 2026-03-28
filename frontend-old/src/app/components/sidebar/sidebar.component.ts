import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';

declare const $: any;

declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
    inMenuBar: boolean;
}

export const ROUTES: RouteInfo[] = [
    {path: '/overview', title: 'Áttekintés', icon: 'dashboard', class: '', inMenuBar: true},
    {path: '/devices', title: 'Eszközök kezelése', icon: 'sd_storage', class: '', inMenuBar: true},
    {path: '/compositeItems', title: 'Összetett eszközök kezelése', icon: 'sd_storage', class: '', inMenuBar: false},
    {path: '/rents', title: 'Kivitelek kezelése', icon: 'local_shipping', class: '', inMenuBar: true},
    {path: '/tickets', title: 'Hibajegyek', icon: 'bug_report', class: '', inMenuBar: true},
    {path: '/settings', title: 'Beállítások', icon: 'person', class: '', inMenuBar: true},
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  private readonly oidcSecurityService = inject(OidcSecurityService);

  menuItems: any[];

  constructor(private router: Router) {}

  ngOnInit() {
      this.menuItems = ROUTES.filter(menuItem => menuItem);
  }

  isMobileMenu() {
    return $(window).width() <= 991;
  };

  logout() {
    this.oidcSecurityService.logoff();
    this.router.navigateByUrl('/login');
  }
}
