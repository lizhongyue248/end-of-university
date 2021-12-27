import { h, defineComponent } from 'vue'
import { QBtn, QPage } from 'quasar'

export default defineComponent({
  name: 'Home',
  setup () {
    return () => h(QPage, {
      class: 'row items-center justify-evenly'
    }, () => h(QBtn, {}, () => 'test'))
  }
})
