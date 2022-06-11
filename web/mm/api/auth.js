const { wrapErrorMessage } = require('../util.js')

module.exports = util => {
  return {
    api: {
      'POST /auth/login' (req, res) {
        const { username, password } = req.body
        if (username === 'adminAndTeacher' && password === '123456') {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: ['ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT'] })
          return
        }
        if (username === 'admin1' && password === '123456') {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: ['ROLE_ADMIN'] })
          return
        }
        if (username === 'teacher' && password === '123456') {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: ['ROLE_TEACHER'] })
          return
        }
        if (username === 'student' && password === '123456') {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: ['ROLE_STUDENT'] })
          return
        }
        if (username === 'noAuth' && password === '123456') {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)'), roles: [] })
          return
        }
        res.status(401)
          .json(wrapErrorMessage('无效的用户信息'))
      }
    }
  }
}
