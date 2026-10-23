export const environment = {
  apiUrl: '/api',
  defaultPageSize: 50,
  pageSizeKeys: {
    devices: 'PAGE_SIZE_DEVICES',
    containers: 'PAGE_SIZE_CONTAINERS',
    rents: 'PAGE_SIZE_RENTS',
    tickets: 'PAGE_SIZE_TICKETS',
    rentItems: 'PAGE_SIZE_RENT_ITEMS',
  },

  rentTeamNameKey: 'RENT_TEAM_NAME',
  rentTeamLeaderKey: 'RENT_TEAM_LEADER_NAME',

  rentFirstSignerNameKey: 'RENT_FIRST_SIGNER_NAME',
  rentFirstSignerTitleKey: 'RENT_FIRST_SIGNER_TITLE',
  rentSecondSignerNameKey: 'RENT_SECOND_SIGNER_NAME',
  rentSecondSignerTitleKey: 'RENT_SECOND_SIGNER_TITLE',

  forceEan8Key: 'FORCE_EAN8',

  adminGroupNames: ['Admin', 'Vezetőség', 'Főszerkesztő'],
  fullAccessGroupNames: ['Stúdiós', 'Vezetőség'],

  defaultOwnerName: 'SVIE',
  defaultDeviceStatus: 'GOOD',
  defaultDeviceQuantity: 1,
};
