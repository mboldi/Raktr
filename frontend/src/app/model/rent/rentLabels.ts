import { RentType } from './rentType';
import { RentItemStatus } from './rentItem/rentItemStatus';

export const RENT_TYPE_LABELS: Record<RentType, string> = {
  [RentType.SIMPLE]: 'Egyszerű',
  [RentType.COMPLEX]: 'Összetett',
};

export const RENT_ITEM_STATUS_LABELS: Record<RentItemStatus, string> = {
  [RentItemStatus.PENDING]: 'Csomagolásra vár',
  [RentItemStatus.OUT]: 'Kint',
  [RentItemStatus.RETURNED]: 'Visszahozva',
};
