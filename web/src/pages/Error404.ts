import { h, defineComponent } from 'vue'
import { QBtn } from 'quasar'

export default defineComponent({
  name: 'NotFound',
  setup () {
    return () => h('div', {
      class: 'fullscreen bg-blue text-white text-center q-pa-md flex flex-center'
    }, h('div', {}, h('div', [
      h('div', { style: [{ fontSize: '30vh' }] }, '404'),
      h('div', { style: [{ opacity: '.4', class: 'text-h2' }] }, 'Oops. Nothing here...'),
      h(QBtn, {
        class: 'q-mt-xl',
        color: 'white',
        textColor: 'blue',
        unelevated: true,
        to: '/',
        label: 'Go Home',
        noCaps: true
      })
    ])))
  }
})
