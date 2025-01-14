/** * 用户注册页面 */
<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-content">
        <div class="content-wrapper">
          <h2>PICTURE & CLOUD</h2>
          <p>Share your memories, store your moments</p>
          <AntCloudOutlined class="cloud-icon" />
        </div>
      </div>
      <div class="register-form">
        <h1 class="register-title">注册</h1>
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
          <FormItem name="checkPassword" :rules="checkPasswordRules(formState)">
            <InputPassword v-model:value="formState.checkPassword" placeholder="确认密码">
              <template #prefix>
                <LockOutlined />
              </template>
            </InputPassword>
          </FormItem>
          <FormItem>
            <Button type="primary" html-type="submit" :loading="loading" block>注册</Button>
          </FormItem>
          <div class="form-footer">
            <router-link to="/user/login" class="login-link">已有账号？去登录</router-link>
          </div>
        </Form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Form, FormItem, Input, InputPassword, Button, message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, AntCloudOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { userRegisterUsingPost } from '@/api/userController'
import { userAccountRules, passwordRules, checkPasswordRules } from '@/config/formRules'
import type { API } from '@/api/typings'

type FormState = API.UserRegisterRequest

const router = useRouter()
const loading = ref(false)

const formState = reactive<FormState>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const handleFinish = async (values: FormState) => {
  loading.value = true
  try {
    const res = await userRegisterUsingPost(values)
    if (res.data?.code === 0) {
      message.success('注册成功')
      router.push('/user/login')
    } else {
      message.error(res.data?.message || '注册失败')
    }
  } catch (error) {
    if (error instanceof Error) {
      message.error('注册失败，' + error.message)
    } else {
      message.error('注册失败')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-title {
  color: rgb(178, 163, 255);
  font-weight: 600;
  margin-bottom: 30px;
  font-size: 30px;
  text-align: center;
}
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 64px - 70px);
  padding: 20px;
}

.register-box {
  display: flex;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  overflow: hidden;
  width: 900px;
  min-height: 500px;
  position: relative;
}

.register-form {
  flex: 1;
  padding: 40px 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  z-index: 1;
  max-width: 55%;
  margin-left: 45%;
  animation: slideInRight 0.5s ease-out;
}

.form {
  width: 100%;
  max-width: 360px;
}

.register-form h1 {
  color: rgb(178, 163, 255);
  margin-bottom: 30px;
  font-size: 28px;
  text-align: center;
}

.register-content {
  position: absolute;
  left: 0;
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
  clip-path: polygon(0 0, 92% 0, 100% 100%, 0 100%);
  animation: slideInLeft 0.5s ease-out;
}

.content-wrapper {
  margin-right: 40px;
}

.register-content h2 {
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 300;
}

.register-content p {
  font-size: 16px;
  margin-bottom: 30px;
  opacity: 0.9;
}

.cloud-icon {
  font-size: 120px;
  margin-top: 20px;
}

.login-link {
  color: rgb(178, 163, 255);
  text-decoration: none;
}

.login-link:hover {
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
  .register-box {
    flex-direction: column;
    width: 100%;
    max-width: 400px;
  }

  .register-form {
    padding: 30px;
    max-width: 100%;
    margin-left: 0;
  }

  .form {
    max-width: 100%;
  }

  .register-content {
    position: relative;
    width: 100%;
    clip-path: none;
    padding: 20px;
  }

  .content-wrapper {
    margin-right: 0;
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
