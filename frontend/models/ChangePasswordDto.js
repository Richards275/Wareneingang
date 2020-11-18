export default class ChangePasswordDto {
  constructor(username, password, newPasswordEins, newPasswordZwei) {
    this.username = username
    this.password = password
    this.newPasswordEins = newPasswordEins
    this.newPasswordZwei = newPasswordZwei
  }
}
