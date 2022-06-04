<template lang="pug">
q-card.auth-role-info.cursor-pointer(v-ripple, @click="handleToLink")
  q-card-section.q-pa-md.q-ml-md.text-center.q-gutter-sm.flex-row.flex.items-center
    q-avatar(:color="props.color", :icon="props.icon", :text-color="props.textColor")
    .text-h5.q-pl-sm {{props.text}}
</template>

<script lang="ts" setup>
import { defineProps } from 'vue'
import { useQuasar } from 'quasar'
import { useRouter } from 'vue-router'
const $q = useQuasar()
defineExpose({
  $q
})
const props = defineProps({
  icon: String,
  color: String,
  to: String,
  textColor: {
    type: String,
    default: 'white'
  },
  text: {
    type: String,
    default: ''
  }
})
const router = useRouter()
const handleToLink = () => {
  router.push({ name: props.to })
}
</script>

<style scoped>

</style>
