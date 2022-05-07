const { wrapErrorMessage } = require('../util.js')

module.exports = util => {
  return {
    api: {
      'POST /auth/login' (req, res) {
        const { username, password } = req.body
        if (username === 'admin' && password === '123456') {
          res.json({ accessToken: util.libObj.mockjs.mock('@word(64)') })
          return
        }
        res.status(401)
          .json(wrapErrorMessage('无效的用户信息'))
      }
    }
  }
}
