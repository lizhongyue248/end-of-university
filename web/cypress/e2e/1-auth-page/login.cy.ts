const { username, password, roles } = require('../../../mm/mock/auth')

const usernameInput = 'input[aria-label="用户名"]'
const passwordInput = 'input[aria-label="密码"]'

const errorInput = (text: string) => {
  cy.get(usernameInput).type(text).blur()
  cy.get(passwordInput).type(text).blur()
  cy.get('.q-btn').first().click()
  cy.get('div[role="alert"]').should('have.length', 2)
    .should('contain.text', '这个字段是必填项并且最短为 6.')
  cy.get(usernameInput).clear()
  cy.get(passwordInput).clear()
}

const loginSuccessLocalStorageCheck = (username: string, password: string, roles: string[]) => {
  cy.get(usernameInput).type(username)
  cy.get(passwordInput).type(password)
  cy.get('.q-btn').first().click()
    .should(() => {
      const auth = localStorage.getItem('auth')
      void expect(auth).to.not.be.null
      expect(auth).to.contains('accessToken')
      const parse = JSON.parse(auth as string)
      void expect(parse.accessToken).to.not.be.null
      void expect(parse.roles).to.not.be.null
      expect(parse.roles).have.length(roles.length)
      for (const role of roles) {
        expect(parse.roles).to.contains(role)
      }
    })
}

describe('User login page test', () => {
  beforeEach(() => {
    cy.visit('/#/auth/login')
  })

  it('should have two field inputs, one checkbox, two links and one button.', () => {
    // two field inputs
    cy.get('.q-field').should('have.length', 2)
    cy.get('.q-field').first().should('have.text', '用户名')
    cy.get('.q-field').last().should('have.text', '密码')

    // one checkbox
    cy.get('.q-checkbox').should('have.length', 1)
      .first().should('have.text', '记住我')

    // two links
    cy.get('span.cursor-pointer').should('have.length', 2)
    cy.get('span.cursor-pointer').first().should('have.text', '忘记密码？')
    cy.get('span.cursor-pointer').last().should('have.text', '注册')

    // one button
    cy.get('.q-btn').should('have.length', 1)
      .first().should('have.text', '登录')
  })

  it('should field get error when input invalid.', () => {
    errorInput('1')
    errorInput('12')
    errorInput('123')
    errorInput('1234')
    errorInput('12345')

    // Focus and no input error message.
    cy.get(usernameInput).focus().blur()
    cy.get(passwordInput).focus().blur()
    cy.get('.q-btn').first().click()
    cy.get('div[role="alert"]').should('have.length', 2)
      .should('contain.text', '这个字段是必填项并且最短为 6.')
  })

  it('should student login success.', () => {
    loginSuccessLocalStorageCheck(username.student, password, [roles.student])
    cy.url().should('include', '/student/home')
  })

  it('should teacher login success.', () => {
    loginSuccessLocalStorageCheck(username.teacher, password, [roles.teacher])
    cy.url().should('include', '/teacher/home')
  })

  it('should admin and teacher login success.', () => {
    loginSuccessLocalStorageCheck(username.adminAntdTeacher, password, [roles.teacher, roles.admin])
    cy.url().should('include', '/auth/role')
  })

  it.only('should no role login success.', () => {
    loginSuccessLocalStorageCheck(username.noRole, password, [])
    cy.url().should('include', '/#')
  })
})
