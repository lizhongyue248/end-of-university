/**
 * 包裹 api 的返回值
 * @param {*} param0
 * @param {object} param0.data - 原始数据
 * @param {number|string} [param0.code=200] - http状态码
 * @returns
 */
function wrapApiData ({ data, code = 200 }) {
  code = String(code)
  return {
    code,
    success: Boolean(code.match(/^[2]/)), // 如果状态码以2开头则为 true
    data
  }
}

const wrapErrorMessage = (message) => ({
  error: message,
  description: message
})

const wrapError = (message, description) => ({
  error: message,
  description
})

module.exports = {
  wrapApiData,
  wrapErrorMessage,
  wrapError
}
