<template lang="pug">
#auth-login
  q-form(@submit="handleLogin")
    q-input(
      v-model="user.username",
      label="用户名",
      lazy-rules,
      :rules="[ val => val && val.length > 0 || '用户名是必填项']"
    )
    q-input(
      v-model="user.password",
      label="密码",
      lazy-rules,
      type="password"
      :rules="[ val => val && val.length >= 6 || '密码是必填项且长度大于等于 6 位']"
    )
    q-btn.full-width.q-mt-lg(color="primary", label="登录", type="submit")
    q-btn.full-width.q-my-lg(color="secondary", label="注册", to="/auth/register" )
</template>

<script lang="ts" setup>

import { reactive } from 'vue'
import { useAuthStore } from 'stores/auth-store'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const user = reactive({ username: '', password: '' })

const handleLogin = async () => {
  await authStore.login(user.username, user.password)
  await router.push({ name: 'Home' })
}

</script>

<style scoped>

</style>
