<template>
  <v-card>
    <v-tabs dark background-color="teal darken-3" show-arrows>
      <v-tabs-slider color="teal lighten-3"></v-tabs-slider>
      <v-tab @click="showFehlerTab = false">korrekte Meldung</v-tab>
      <v-tab @click="showFehlerTab = true">Fehler</v-tab>
    </v-tabs>
    <v-text-field
      v-model="search"
      append-icon="mdi-magnify"
      label="Search"
      single-line
      hide-details
    ></v-text-field>
    <v-card-title>
      <span>Mein Titel</span>
      <v-spacer></v-spacer>
      <v-btn color="primary" dark small @click="getData">Aktualisiere</v-btn>
    </v-card-title>
    <transition-group name="slide-up" tag="div" appear>
      <v-data-table
        v-if="!showFehlerTab"
        :key="1"
        :headers="headersWareEingang"
        :items="wareEingangList"
        :items-per-page="5"
        mobile-breakpoint="0"
        class="elevation-1"
        :search="search"
      ></v-data-table>
      <v-data-table
        v-if="showFehlerTab"
        :key="2"
        :headers="headersCsvFehler"
        :items="csvFehlerList"
        :items-per-page="5"
        mobile-breakpoint="0"
        class="elevation-1"
        :search="search"
      >
      </v-data-table>
    </transition-group>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
import { API_URL, authservice } from '@/services/AuthService'
import LieferungDto from '@/models/LieferungDto'

export default {
  name: 'CheckLieferung',
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  data() {
    return {
      showError: false,
      errorMsg: '',
      search: '',
      showFehlerTab: false,
      headersWareEingang: [
        { text: 'Beschreibung', value: 'name' },
        { text: 'Menge', value: 'menge' },
        { text: 'Nummer', value: 'nummer' },
      ],
      wareEingangList: [
        {
          id: '1',
          name: 'Schoki 1',
          menge: '10',
          nummer: '1234',
        },
      ],
      headersCsvFehler: [
        { text: 'Fehler', value: 'fehlermeldung' },
        { text: 'Zeile', value: 'zeile' },
        { text: 'Feld', value: 'feld' },
      ],
      csvFehlerList: [],
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'userRoles', 'token']),
  },
  created() {
    if (this.isAuthenticated && this.userRoles.includes('ROLE_LIEFERANT')) {
      this.getData()
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    async getData() {
      const lieferungDto = new LieferungDto(
        this.$route.params.id,
        this.$store.state.lieferung.lieferantId
      )
      const result = await this.$axios.post(
        API_URL + 'checklieferung/getfehler',
        lieferungDto,
        authservice.getHeaderMitToken(this.token)
      )
      this.wareEingangList = result.data.wareEingangList
      this.csvFehlerList = result.data.csvFehlerList
    },
  },
}
</script>

<style scoped>
.slide-up-enter {
  transform: translateX(10px);
  opacity: 0;
}

.slide-up-enter-active {
  transition: all 0.4s ease;
}
</style>
