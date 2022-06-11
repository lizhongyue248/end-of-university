import { api } from 'boot/request'

/**
 * 登录接口
 * @param username 用户名
 * @param password 密码
 * @param remember 是否记住用户
 */
const login = (username: string, password: string, remember: boolean) =>
  api.post('/auth/login', { data: { username, password, remember } })

export {
  login
}
