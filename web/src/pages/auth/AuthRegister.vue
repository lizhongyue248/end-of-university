<template lang="pug">
#auth-login
  q-form
    q-input(
      v-model="user.username",
      label="用户名",
      lazy-rules,
      :rules="[ val => val && val.length > 0 || '用户名是必填项']"
    )
    q-input(
      v-model="user.username",
      label="用户名",
      lazy-rules,
      :rules="[ val => val && val.length > 0 || '用户名是必填项']"
    )
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
      :rules="[ val => val && val.length > 6 || '密码是必填项且长度大于 6 位']"
    )
    q-btn.full-width.q-mt-lg(color="secondary", label="注册")
    q-btn.full-width.q-my-lg(color="primary", label="登录", to="/auth/login")
</template>

<script lang="ts" setup>

import { reactive } from 'vue'

const user = reactive({ username: '', password: '' })

</script>

<style scoped>

</style>
