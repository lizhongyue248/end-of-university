import { api } from 'boot/request'

/**
 * 登录接口
 * @param username 用户名
 * @param password 密码
 */
const login = (username: string, password: string) =>
  api.post('/auth/login', { data: { username, password } })

export {
  login
}
