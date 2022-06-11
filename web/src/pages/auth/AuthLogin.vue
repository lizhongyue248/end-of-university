<template lang="pug">
#auth-login
  #auth-login-head.text-h5.font-bold.q-mb-xs {{$t('title.login')}}
  q-form.q-mt-sm(@submit="handleLogin")
    q-input.q-mt-md(
      v-model="user.username",
      :label="$t('form.username')",
      lazy-rules,
      :rules="[ val => val && val.length >= 6 || $t('rule.requiredMinLength', [ 6 ])]"
    )
    q-input.q-mt-xs(
      v-model="user.password",
      :label="$t('form.password')",
      lazy-rules,
      type="password"
      :rules="[ val => val && val.length >= 6 || $t('rule.requiredMinLength', [ 6 ])]"
    )
    .flex.items-center.q-mt-md
      q-checkbox(v-model="user.remember", :label="$t('form.rememberMe')" )
      q-space
      span.text-primary.cursor-pointer {{$t('form.forgetPassword')}}
    #auth-login-action.q-my-lg
      q-btn.full-width(color="primary", :label="$t('title.login')", type="submit", :loading="loginLoading", :disable="loginLoading")
      .q-mt-md.text-center {{$t('tip.register')}}&nbsp;&nbsp;
        span.text-primary.cursor-pointer(@click="handleToRegister") {{$t('title.register')}}
    //q-btn.full-width.q-my-lg(color="secondary", label="注册", :to="{ name: Auth.REGISTER }" )
</template>

<script lang="ts" setup>

import { reactive } from 'vue'
import { useAuthStore } from 'stores/auth-store'
import { useRouter } from 'vue-router'
import { Auth } from '@/constant/Routes'
import { useBoolean } from 'v3hooks'
import { toRoute } from '@/hooks/toRoute'

const authStore = useAuthStore()
const router = useRouter()
const [loginLoading, { toggle: loginToggle }] = useBoolean(false)

const user = reactive({ username: '', password: '', remember: false })
const toLink = toRoute()

const handleLogin = async () => {
  loginToggle()
  try {
    await authStore.login(user.username, user.password, user.remember)
    if (authStore.hasMultipleRole) {
      await router.push({ name: Auth.ROLE })
      return
    }
    toLink.singleToHome()
  } finally {
    loginToggle()
  }
}

const handleToRegister = () => {
  router.push({ name: Auth.REGISTER })
}

</script>

<style scoped>

</style>
