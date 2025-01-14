/** * 用户登录页面 */
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-form">
        <h1 class="login-title">登录</h1>
        <Form :model="formState" @finish="handleFinish" class="form">
          <FormItem name="userAccount" :rules="userAccountRules">
            <Input v-model:value="formState.userAccount" placeholder="用户名">
              <template #prefix>
                <UserOutlined />
              </template>
            </Input>
          </FormItem>
          <FormItem name="userPassword" :rules="passwordRules">
            <InputPassword v-model:value="formState.userPassword" placeholder="密码">
              <template #prefix>
                <LockOutlined />
              </template>
            </InputPassword>
          </FormItem>
          <FormItem>
            <Button type="primary" html-type="submit" :loading="loading" block>登录</Button>
          </FormItem>
          <div class="form-footer">
            <router-link to="/user/register" class="register-link">注册账号</router-link>
          </div>
        </Form>
      </div>
      <div class="login-content">
        <div class="content-wrapper">
          <h2>PICTURE & CLOUD</h2>
          <p>Share your memories, store your moments</p>
          <AntCloudOutlined class="book-icon" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Form, FormItem, Input, InputPassword, Button, message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, AntCloudOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { userLoginUsingPost } from '@/api/userController'
import { userAccountRules, passwordRules } from '@/config/formRules'
import type { API } from '@/api/typings'
import { useLoginStore } from '@/stores'

type FormState = API.UserLoginRequest

const router = useRouter()
const loading = ref(false)

const formState = reactive<FormState>({
  userAccount: '',
  userPassword: '',
})

const loginStore = useLoginStore()
const handleFinish = async (values: FormState) => {
  loading.value = true
  try {
    const res = await userLoginUsingPost({
      userAccount: values.userAccount,
      userPassword: values.userPassword,
    })
    if (res.data?.code === 0) {
      await loginStore.setLoginUser()
      message.success('登录成功')
      router.push({
        path: '/',
        replace: true
      })
      console.log(router.getRoutes())
    } else {
      message.error(res.data?.message || '登录失败')
    }
  } catch (error) {
    if (error instanceof Error) {
      message.error('登录失败，' + error.message)
    } else {
      message.error('登录失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-title {
  color: rgb(178, 163, 255);
  font-weight: 600;
  margin-bottom: 30px;
  font-size: 30px;
  text-align: center;
}
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px - 70px);
  padding: 20px;
}

.login-box {
  display: flex;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  overflow: hidden;
  width: 900px;
  min-height: 500px;
  position: relative;
}

.login-form {
  flex: 1;
  padding: 40px 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  z-index: 1;
  max-width: 55%;
  animation: slideInLeft 0.5s ease-out;
}

.form {
  width: 100%;
  max-width: 360px;
}

.login-form h1 {
  color: rgb(178, 163, 255);
  margin-bottom: 30px;
  font-size: 28px;
  text-align: center;
}

.login-content {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 45%;
  background-image: -moz-linear-gradient(90deg, rgb(178, 163, 255), rgb(232, 255, 199));
  background-image: -webkit-linear-gradient(90deg, rgb(178, 163, 255), rgb(232, 255, 199));
  background-image: linear-gradient(90deg, rgb(178, 163, 255), rgb(232, 255, 199));
  padding: 40px;
  color: #666;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  clip-path: polygon(8% 0, 100% 0, 100% 100%, 0 100%);
  animation: slideInRight 0.5s ease-out;
}

.content-wrapper {
  margin-left: 40px;
}

.login-content h2 {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 300;
}

.login-content p {
  font-size: 16px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.book-icon {
  font-size: 120px;
  margin-top: 20px;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
}

.register-link {
  color: rgb(178, 163, 255);
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}

:deep(.ant-input-affix-wrapper) {
  border-radius: 6px;
  height: 40px;
}

:deep(.ant-form-item) {
  margin-bottom: 24px;
}

:deep(.ant-btn) {
  height: 40px;
  border-radius: 6px;
  background: rgb(178, 163, 255);
  border-color: rgb(178, 163, 255);
}

:deep(.ant-btn:hover) {
  background: rgb(158, 143, 235);
  border-color: rgb(158, 143, 235);
}

@media (max-width: 768px) {
  .login-box {
    flex-direction: column;
    width: 100%;
    max-width: 400px;
  }

  .login-form {
    padding: 30px;
    max-width: 100%;
  }

  .form {
    max-width: 100%;
  }

  .login-content {
    position: relative;
    width: 100%;
    clip-path: none;
    padding: 20px;
  }

  .content-wrapper {
    margin-left: 0;
  }
}

@keyframes slideInLeft {
  from {
    transform: translateX(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>
