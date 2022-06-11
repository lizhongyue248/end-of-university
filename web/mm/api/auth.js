const { wrapErrorMessage } = require('../util.js')
const data = require('../mock/auth')
const { roles } = data

module.exports = util => {
  return {
    api: {
      'POST /auth/login' (req, res) {
        const { username, password } = req.body
        if (username === data.username.adminAndTeacher && password === data.password) {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: [roles.teacher, roles.admin] })
          return
        }
        if (username === data.username.administrator && password === data.password) {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: [roles.admin] })
          return
        }
        if (username === data.username.teacher && password === data.password) {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: [roles.teacher] })
          return
        }
        if (username === data.username.student && password === data.password) {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: [roles.student] })
          return
        }
        if (username === data.username.noRole && password === data.password) {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: [] })
          return
        }
        res.status(401)
          .json(wrapErrorMessage('用户不存在或密码错误'))
      }
    }
  }
}
