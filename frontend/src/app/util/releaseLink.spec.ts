import { releaseLink } from './releaseLink';

describe('releaseLink', () => {
  it('links a tag build to its release', () => {
    expect(releaseLink('raktr-frontend@1.4.0')).toEqual({
      label: 'v1.4.0',
      url: 'https://github.com/mboldi/Raktr/releases/tag/v1.4.0',
    });
  });

  it('links any other image to its commit', () => {
    expect(releaseLink('raktr-frontend@0.0.0+abc1234')).toEqual({
      label: 'abc1234',
      url: 'https://github.com/mboldi/Raktr/commit/abc1234',
    });
  });

  it('links a local build to the repository', () => {
    expect(releaseLink('raktr-frontend@dev')).toEqual({
      label: 'dev',
      url: 'https://github.com/mboldi/Raktr',
    });
  });
});
