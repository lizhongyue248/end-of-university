<template lang="pug">
#auth-role(class="<sm:mt-2")
  #auth-role-content(v-if="!tip")
    #auth-role-choose.flex.flex-col.q-gutter-lg
      RoleInfo(v-if="show.admin", color="red", icon="school", text="Administrator", :to="Admin.HOME")
      RoleInfo(v-if="show.teacher", color="orange", icon="person", text="Teacher", :to="Teacher.HOME")
      RoleInfo(v-if="show.student", color="green", icon="face", text="Student", :to="Student.HOME")
    #auth-role-action.flex.items-center.q-mt-md
      q-checkbox(v-model="remember", :label="$t('tip.rememberRole')" )
      q-space
      span.text-primary.cursor-pointer(@click="logout") {{$t('tip.changeAccount')}}
  #auth-role-tip.text-center.q-pb-md(v-else)
    #auth-role-tip-content.text-h6 {{ tipMessage }}
    q-btn.q-mt-lg(outline, color="primary", :label="$t('tip.changeAccount')", size="lg", @click="handleBack")
</template>

<script lang="ts" setup>
import { useAuthStore } from 'stores/auth-store'
import { onMounted, reactive, ref } from 'vue'
import RoleInfo from 'components/auth/RoleInfo.vue'
import { toRoute } from '@/hooks/toRoute'
import { Admin, Teacher, Student } from '@/constant/Routes'
import { useBoolean } from 'v3hooks'

const authStore = useAuthStore()
const show = reactive({
  admin: authStore.hasAdmin,
  teacher: authStore.hasTeacher,
  student: authStore.hasStudent
})
const [remember] = useBoolean(false)
const [tip, { setTrue: tipShow, setFalse: tipHidden }] = useBoolean(false)
const tipMessage = ref('')

const { logout } = toRoute()

onMounted(() => {
  if (!authStore.hasRole) {
    tipShow()
    tipMessage.value = '当前用户没有任何角色角色信息，请联系管理员进行处理'
  }
})

const handleBack = () => {
  tipHidden()
  logout()
}

</script>

<style scoped>

</style>
