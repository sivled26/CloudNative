export default {
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.jsx?$': 'babel-jest'
  },
  moduleFileExtensions: ['js', 'jsx'],
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  // import.meta.env no existe en Jest: se reemplaza el módulo de configuración.
  moduleNameMapper: {
    '^(.*)/config/env(\\.js)?$': '<rootDir>/test/envMock.js'
  }
};
