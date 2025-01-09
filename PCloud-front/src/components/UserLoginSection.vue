<template>
  <RouterLink to="/login" class="user-login">
    <span>登录</span>
    <span>或</span>
    <span>注册</span>
  </RouterLink>
</template>

<style scoped lang="scss">
.user-login {
  margin-left: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  color: var(--text-secondary, #666);
  text-decoration: none;

  span {
    &:hover {
      color: var(--primary-color, #1890ff);
    }
  }
}
</style>
