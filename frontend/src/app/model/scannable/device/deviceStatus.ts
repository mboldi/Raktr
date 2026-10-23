export enum DeviceStatus {
  GOOD = 'GOOD',
  NEEDS_REPAIR = 'NEEDS_REPAIR',
  SCRAPPED = 'SCRAPPED',
}

export const DEVICE_STATUS_LABELS: Record<DeviceStatus, string> = {
  [DeviceStatus.GOOD]: 'Jó',
  [DeviceStatus.NEEDS_REPAIR]: 'Javítást igényel',
  [DeviceStatus.SCRAPPED]: 'Selejt',
};
