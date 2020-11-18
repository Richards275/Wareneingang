<template>
  <v-app>
    <v-card>
      <v-form @submit.prevent="login" data-test="form">
        <v-card-title>
          <h2>Login/Logout</h2>
        </v-card-title>
        <v-card-text>
          <v-text-field
            v-if="!changePassword"
            id="username"
            v-model="user.username"
            label="Username oder Email"
            prepend-icon="mdi-account-circle"
            :rules="usernameRules"
            data-test="username"
          />
          <v-text-field
            v-if="!changePassword"
            id="password"
            v-model="user.password"
            :rules="passwordRules"
            :type="showPassword ? 'text' : 'password'"
            label="Passwort"
            prepend-icon="mdi-lock"
            :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
            data-test="password"
            @click:append="showPassword = !showPassword"
          />
          <v-text-field
            v-if="changePassword"
            v-model="newPassword.eins"
            :rules="passwordRules"
            :type="showPasswordEins ? 'text' : 'password'"
            label="Neues Passwort"
            prepend-icon="mdi-lock"
            :append-icon="showPasswordEins ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="showPasswordEins = !showPasswordEins"
          />
          <v-text-field
            v-if="changePassword"
            v-model="newPassword.zwei"
            :rules="passwordRules"
            :type="showPasswordZwei ? 'text' : 'password'"
            label="Neues Passwort erneut"
            prepend-icon="mdi-lock"
            :append-icon="showPasswordZwei ? 'mdi-eye' : 'mdi-eye-off'"
            @click:append="showPasswordZwei = !showPasswordZwei"
          />
          <v-divider></v-divider>
        </v-card-text>
        <v-card-actions>
          <v-container>
            <v-row class="d-flex justify-space-between">
              <v-col cols="12" sm="6" class="d-flex justify-start">
                <v-btn
                  v-if="!changePassword"
                  id="submit"
                  color="success"
                  type="submit"
                  rounded
                  class="mr-2"
                  data-test="loginbutton"
                >
                  Login
                </v-btn>
                <v-btn
                  v-if="!changePassword"
                  color="success"
                  rounded
                  @click="logout"
                >
                  Logout
                </v-btn>
                <v-btn
                  v-if="changePassword"
                  color="success"
                  type="submit"
                  rounded
                >
                  Passwort ändern
                </v-btn>
              </v-col>
              <v-col cols="12" sm="6" class="d-flex justify-end">
                <v-chip
                  v-if="!changePassword"
                  color="accent"
                  class="mr-2"
                  @click="newPasswordByEmail"
                >
                  Neues Passwort
                </v-chip>
                <v-chip
                  v-if="!changePassword && isAuthenticated"
                  color="accent"
                  class="mr-2"
                  @click="changePassword = true"
                >
                  Passwort ändern
                </v-chip>
              </v-col>
            </v-row>
          </v-container>
        </v-card-actions>
      </v-form>
    </v-card>
  </v-app>
</template>

<script>
import { API_URL } from '@/services/AuthService'
import { mapGetters } from 'vuex'
import ChangePasswordDto from '@/models/ChangePasswordDto'
import User from '../models/User'

export default {
  name: 'Login',
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  data() {
    return {
      showPassword: false,
      showPasswordEins: false,
      showPasswordZwei: false,
      changePassword: false,
      newPassword: { eins: '', zwei: '' },
      user: new User('', ''),
      errorMessage: '',
      usernameRules: [(value) => !!value || 'Username required'],
      passwordRules: [
        (value) => !!value || 'Password required',
        (value) =>
          (value && value.length >= 8) ||
          'Password muss mindestens 8 Zeichen enthalten',
      ],
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated']),
  },
  methods: {
    async login() {
      try {
        if (this.changePassword) {
          const changePasswordDto = new ChangePasswordDto(
            this.user.username,
            this.user.password,
            this.newPassword.eins,
            this.newPassword.zwei
          )
          await this.$store.dispatch('auth/changePassword', {
            changePasswordDto,
          })
        } else {
          const user = {
            username: this.user.username,
            password: this.user.password,
          }
          await this.$store.dispatch('auth/login', user)
          await this.$router.push({ name: 'index' })
        }
      } catch (error) {
        this.errorMessage = 'Failed to login user. ' + error.message
      }
    },
    newPasswordByEmail() {
      this.$axios.post(API_URL + 'newpassword', {
        email: this.user.username,
      })
    },
    async logout() {
      await this.$store.dispatch('auth/logout')
    },
  },
}
</script>

<style scoped></style>
