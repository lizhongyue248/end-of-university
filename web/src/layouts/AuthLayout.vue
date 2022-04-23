<template lang="pug">
#auth.flex.justify-center.items-center
  q-card.max-w-md.full-width.shadow-18.animated.fadeIn.transition-all(class="<sm:(h-screen w-full max-w-none)")
    q-card#auth-title.max-w-sm.bg-indigo-500.text-white.mx-auto.relative.-top-8.text-center.q-pt-lg(class="<sm:hidden", v-ripple)
      p.text-weight-bold.text-h5 欢迎登录毕业设计指导系统
      q-card-section#auth-sub-title
        span.typed-element.text-white.text-subtitle1.typing(ref="typedElement")
    .text-h4.q-mx-lg.q-mt-xl.font-bold(class="sm:hidden") 欢迎登录
    q-card-section.q-mx-md
      router-view(v-slot="{ Component }")
        transition(enter-active-class="animated fadeIn", leave-active-class="animated fadeOut", appear, mode="out-in" )
          component(:is="Component")
</template>

<script lang="ts" setup>
import Typed from 'typed.js'
import { onMounted, ref, watchEffect } from 'vue'
import { useRoute } from 'vue-router'

const typedElement = ref<Element>()
const route = useRoute()
let tip = null

const typedJs = () => {
  if (typedElement.value === undefined) return
  if (tip !== null) tip.destroy()
  tip = new Typed(
    typedElement.value,
    {
      strings: [route.meta.tip],
      loop: false,
      typeSpeed: 150
    }
  )
}

watchEffect(() => {
  typedJs()
})

const initTypedJS = () => {
  return typedJs()
}

onMounted(initTypedJS)

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
