<template>
  <v-card>
    <v-card-title>
      <span>Unsere Lieferanten</span>
      <v-chip color="accent" dark class="ml-2" @click="getData">
        Aktualisiere
      </v-chip>
    </v-card-title>
    <v-alert v-if="message" border="left" color="amber darken-3" dark>
      {{ message }}
    </v-alert>
    <v-text-field
      v-model="search"
      append-icon="mdi-magnify"
      label="Search"
      single-line
      hide-details
    ></v-text-field>
    <v-data-table
      :headers="headersLieferant"
      :items="lieferantList"
      :items-per-page="5"
      class="elevation-1"
      mobile-breakpoint="0"
      :search="search"
    >
      <template v-slot:item.istAktiv="{ item }">
        <v-chip class="mr-2" label :color="getColor(item.istAktiv)">
          {{ getStatusText(item.istAktiv) }}
        </v-chip>
      </template>
      <template
        v-if="userRoles.includes('ROLE_ADMIN')"
        v-slot:item.wechsleAktivInaktiv="{ item }"
      >
        <v-chip
          class="mr-2"
          color="warning"
          @click="wechsleAktivInaktiv(item.id)"
        >
          Status wechseln
        </v-chip>
      </template>
      <template
        v-if="userRoles.includes('ROLE_ADMIN')"
        v-slot:item.neuerUser="{ item }"
      >
        <v-chip
          class="mr-2"
          color="warning"
          @click="openCreateNewUserDialog(item)"
        >
          User anlegen
        </v-chip>
      </template>
      <template v-slot:top>
        <NewUserDialog
          :dialog="dialog"
          :item="aktuellesItem"
          data-test="newuserdialog"
          @newUser="saveNewUser"
        ></NewUserDialog>
      </template>
    </v-data-table>
    <v-form v-if="userRoles.includes('ROLE_ADMIN')" @submit.prevent="register">
      <v-card-text>
        <span>Registrierung eines neuen Lieferanten</span>
        <v-text-field
          id="username"
          v-model="lieferantName"
          label="Lieferantname"
          prepend-icon="mdi-account-circle"
        />
        <v-btn color="success" rounded type="submit">Absenden</v-btn>
      </v-card-text>
    </v-form>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
import { API_URL, authservice } from '@/services/AuthService'
import NewUserDialog from '@/components/NewUserDialog'

export default {
  name: 'Lieferanten',
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  components: {
    NewUserDialog,
  },
  data() {
    return {
      search: '',
      expanded: [],
      message: '',
      aktuellesItem: {},
      lieferantIdOfNewUser: '',
      dialog: false,
      lieferantName: '',
      headersLieferant: [
        { text: 'Lieferantname', value: 'name' },
        { text: 'Status', value: 'istAktiv' },
        { text: '', value: 'wechsleAktivInaktiv' },
        { text: '', value: 'neuerUser' },
      ],
      lieferantList: [],
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'userRoles', 'token']),
  },
  created() {
    if (
      this.isAuthenticated &&
      this.userRoles.some((item) =>
        ['ROLE_ADMIN', 'ROLE_MITARBEITERIN'].includes(item)
      )
    ) {
      this.getData()
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    async getData() {
      const result = await this.$axios.get(
        API_URL + 'lieferant',
        authservice.getHeaderMitToken(this.token)
      )
      this.lieferantList = result.data
    },
    async register() {
      const lieferantDto = { name: this.lieferantName }
      const result = await this.$axios.post(
        API_URL + 'lieferant/register',
        lieferantDto,
        authservice.getHeaderMitToken(this.token)
      )
      this.lieferantList.unshift(result.data)
    },
    async wechsleAktivInaktiv(lieferantId) {
      const result = await this.$axios.get(
        API_URL + 'lieferant/wechsle/' + lieferantId,
        authservice.getHeaderMitToken(this.token)
      )
      const lieferant = result.data
      if (lieferant && lieferant.id === lieferantId) {
        const alterIndex = this.lieferantList.findIndex(
          (element) => element.id === lieferantId
        )
        this.lieferantList.splice(alterIndex, 1)
        this.lieferantList.unshift(lieferant)
      }
    },
    openCreateNewUserDialog(item) {
      this.aktuellesItem = item
      this.dialog = true
    },
    async saveNewUser(newUser) {
      if (newUser) {
        const result = await this.$axios.post(
          API_URL + 'auth/signup',
          newUser,
          authservice.getHeaderMitToken(this.token)
        )
        this.message =
          result.data.message +
          ' für den Lieferanten ' +
          this.aktuellesItem.name
      }
      this.dialog = false
      this.aktuellesItem = {}
    },
    getStatusText(lieferantStatus) {
      return lieferantStatus ? 'aktiv' : 'inaktiv'
    },
    getColor(lieferantStatus) {
      return lieferantStatus ? 'success' : 'error'
    },
  },
}
</script>

<style scoped></style>
