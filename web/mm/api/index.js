const { wrapApiData } = require('../util.js')

module.exports = util => {
  const {
    libObj: { mockjs }
  } = util
  return {
    api: {
      // 创建接口并使用 mockjs 生成数据
      'post /api/test': wrapApiData(mockjs.mock({
        'data|3-7': [{
          userId: '@id',
          userName: '@cname'
        }]
      })),
      // 使用 mockjs 生成数据
      '/user' (req, res) {
        const json = mockjs.mock({
          'data|3-7': [{
            userId: '@id',
            userName: '@cname'
          }]
        })
        res.json(json)
      }
    }
  }
}
