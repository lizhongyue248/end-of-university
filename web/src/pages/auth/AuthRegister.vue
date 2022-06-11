<template lang="pug">
#auth-register
  #auth-register-head.text-h5.font-bold.q-mb-xs {{$t('title.register')}}
  q-form.q-mt-sm
    q-input.q-mt-md(
      v-model="user.username",
      :label="$t('form.username')",
      lazy-rules,
      minLength="6",
      :rules="[ val => val && val.length >= 6 || $t('rule.requiredMinLength', [ 6 ])]"
    )
    q-input.q-mt-xs(
      v-model="user.phone",
      :label="$t('form.phone')",
      lazy-rules,
      :rules="[ val => val && val.length >= 11 || $t('rule.requiredEqualLength', [ 11 ])]",
    )
    q-input(
      v-model="user.password",
      :label="$t('form.password')",
      lazy-rules,
      type="password",
      counter,
      :rules="[ val => val && val.length >= 6 || $t('rule.requiredMinLength', [ 6 ])]"
    )
    q-input(
      v-model="user.rePassword",
      :label="$t('form.rePassword')",
      lazy-rules,
      type="rePassword",
      counter,
      :rules="[ val => val && val.length >= 6 || $t('rule.requiredMinLength', [ 6 ])]"
    )
    .flex.items-center.q-mt-md
      q-checkbox(v-model="user.agree", :label="$t('tip.registerAgree')" )
    #auth-login-action.q-my-lg
      q-btn.full-width(color="primary", :label="$t('title.register')", type="submit")
      .q-mt-md.text-center {{$t('tip.login')}}&nbsp;&nbsp;
        span.text-primary.cursor-pointer(@click="handleToLogin") {{$t('title.login')}}
</template>

<script lang="ts" setup>

import { reactive } from 'vue'
import { Auth } from '@/constant/Routes'
import { useRouter } from 'vue-router'

const router = useRouter()

const user = reactive({
  username: '',
  password: '',
  rePassword: '',
  phone: '',
  agree: false
})

const handleToLogin = () => {
  router.push({ name: Auth.LOGIN })
}
</script>

<style scoped>

</style>
