<template>
  <v-card>
    <v-card-title>
      <span>Unsere Benutzer*innen</span>
      <v-chip color="accent" dark class="ml-2" @click="getData">
        Aktualisiere
      </v-chip>
    </v-card-title>
    <v-text-field
      v-model="search"
      append-icon="mdi-magnify"
      label="Search"
      single-line
      hide-details
    ></v-text-field>
    <v-data-table
      :headers="headersUser"
      :items="userList"
      :items-per-page="5"
      class="elevation-1"
      :search="search"
    >
    </v-data-table>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
import { API_URL, authservice } from '@/services/AuthService'

export default {
  name: 'User',
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  data() {
    return {
      search: '',
      headersUser: [
        { text: 'Username', value: 'name' },
        { text: 'Lieferantname', value: 'lieferantName' },
      ],
      userList: [],
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'userRoles', 'token']),
  },
  created() {
    if (this.isAuthenticated && this.userRoles.includes('ROLE_ADMIN')) {
      this.getData()
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    async getData() {
      const response = await this.$axios.get(
        API_URL + 'auth/user',
        authservice.getHeaderMitToken(this.token)
      )
      this.userList = response.data
    },
  },
}
</script>

<style scoped></style>
