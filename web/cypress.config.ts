import { defineConfig } from 'cypress'

export default defineConfig({
  // fixturesFolder: 'cypress/fixtures',
  // screenshotsFolder: 'cypress/screenshots',
  // videosFolder: 'cypress/videos',
  video: true,
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    // setupNodeEvents (on, config) {
    //   return require('./cypress/plugins/index.ts')(on, config)
    // },
    baseUrl: 'http://localhost:3000/'
  },
  component: {
    devServer: {
      framework: 'vue',
      bundler: 'vite'
    }
    // setupNodeEvents (on, config) {},
    // supportFile: 'cypress/support/unit.ts',
    // specPattern: 'cypress/**/*.spec.ts'
  }
})
