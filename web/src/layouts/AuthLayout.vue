<template lang="pug">
#auth.flex.justify-center.items-center
  q-card.max-w-md.full-width.shadow-18.animated.fadeIn.transition-all(class="<sm:(h-screen w-full max-w-none)")
    #auth-title.max-w-sm.bg-indigo-500.text-white.mx-auto.relative.-top-8.text-center.q-pt-lg(class="<sm:hidden")
      p.text-weight-bold.text-h5 欢迎使用毕业设计指导系统
      q-card-section#auth-sub-title
        span.typed-element.text-white.text-subtitle1.typing {{tip}}
    .text-h4.q-mx-lg.q-mt-xl.font-bold(class="sm:hidden") 欢迎使用
    q-card-section.q-mx-md
      router-view(v-slot="{ Component }")
        transition(enter-active-class="animated fadeIn", leave-active-class="animated fadeOut", appear, mode="out-in" )
          component(:is="Component")
</template>

<script lang="ts" setup>

import { useRoute } from 'vue-router'

const route = useRoute()
const tip = route.meta.tip
</script>

<style lang="scss" scoped>
#auth {
  height: 100vh;
  width: 100%;
  background: url("src/assets/bg.jpg") no-repeat center center;
  background-size: cover;

  #auth-title {
    box-shadow: 0 8px 10px -5px rgba(0, 0, 0, 0.2), 0 16px 24px 2px rgba(0, 0, 0, 0.14), 0 6px 30px 5px rgba(0, 0, 0, 0.12);
  }
}
</style>
