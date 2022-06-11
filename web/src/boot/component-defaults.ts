import { boot } from 'quasar/wrappers'
import { LoadingBar, Notify } from 'quasar'

export default boot(async (/* { app, router, ... } */) => {
  Notify.setDefaults({
    position: 'bottom-right',
    timeout: 3000,
    multiLine: true,
    classes: 'min-w-xs',
    actions: [
      { label: 'Dismiss', color: 'white', handler: () => { /* ... */ } }
    ]
  })
  LoadingBar.setDefaults({
    color: 'blue',
    size: '5px',
    position: 'top'
  })
})
