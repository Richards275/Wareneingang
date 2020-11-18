export const getters = {
  isAuthenticated(state) {
    return state.auth.status.loggedIn
  },

  loggedInUser(state) {
    return state.auth.user
  },

  token(state) {
    return state.auth.token
  },

  userRoles(state) {
    return state.auth.user ? state.auth.user.roles : []
  },

  possibleUserRoles() {
    return ['admin', 'lieferant', 'mitarbeiterin']
  },
}
