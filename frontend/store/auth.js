import { API_URL, authservice } from '../services/AuthService'

export default {
  state() {
    return {
      user: null,
      token: '',
      status: { loggedIn: false },
    }
  },
  mutations: {
    LOGIN_SUCCESS(state, user) {
      if (user.token) {
        state.token = user.token
      }
      state.status.loggedIn = true
      state.user = user
    },
    LOGIN_FAILURE(state) {
      state.status.loggedIn = false
      state.user = null
    },
    LOGOUT() {
      location.reload()
      // lösche damit Vuex state, besser als
      // state.status.loggedIn = false
      // state.user = null
    },
  },
  actions: {
    async login({ commit }, user) {
      try {
        const response = await this.$axios.post(API_URL + 'login', user)
        commit('LOGIN_SUCCESS', response.data)
        return response
      } catch (error) {
        commit('LOGIN_FAILURE')
        return error
      }
    },
    async changePassword({ commit, state }, { changePasswordDto }) {
      try {
        await this.$axios.post(
          API_URL + 'changepassword',
          changePasswordDto,
          authservice.getHeaderMitToken(state.token)
        )
      } catch (error) {
      } finally {
        commit('LOGOUT')
      }
      return 'logout'
    },
    logout({ commit }) {
      commit('LOGOUT')
    },
  },
  getters: {
    // VueMastery:
    // Actions, Mutations, and Getters (even inside modules)
    // are all registered under the global namespace.
  },
}
