<template>
  <v-card>
    <v-card-title>
      Die Lieferungen
      <v-chip
        color="accent"
        dark
        class="ml-2"
        @click="getData"
        data-test="Aktualisiere"
      >
        Aktualisiere
      </v-chip>
    </v-card-title>
    <v-card-text data-test="explanation">
      Bitte klicken Sie zum Bearbeiten auf eine Zeile
    </v-card-text>
    <v-text-field
      v-model="search"
      append-icon="mdi-magnify"
      label="Search"
      single-line
      hide-details
    ></v-text-field>
    <v-data-table
      :headers="headersLieferung"
      :items="lieferungList"
      :items-per-page="5"
      class="elevation-1"
      :search="search"
      show-expand
      single-expand
      :expanded.sync="expanded"
    >
      <template v-slot:expanded-item="{ headers, item }">
        <td :colspan="headers.length">Bemerkung: {{ item.bemerkung }}</td>
      </template>
      <template v-slot:item.lieferDatum="{ item }">
        <v-chip label :color="getColor(item.lieferungsStatus)">
          {{ formatDate(item.lieferDatum) }}
        </v-chip>
      </template>
      <template v-slot:item.optionen="{ item }">
        <v-chip
          v-if="['NEU', 'FEHLER'].includes(item.lieferungsStatus)"
          class="mr-2"
          color="warning"
          @click="geheZuPageCheckLieferung(item)"
        >
          prüfe
        </v-chip>
        <v-chip
          v-if="item.lieferungsStatus === 'NEU'"
          class="mr-2"
          color="success"
          @click="gebeFrei(item)"
        >
          freigeben
        </v-chip>
        <v-chip
          v-if="['BESTAETIGT', 'INBEARBEITUNG'].includes(item.lieferungsStatus)"
          class="mr-2"
          color="success"
        >
          <span v-if="userRoles.includes('ROLE_LIEFERANT')">
            in Bearbeitung
          </span>
          <span
            v-if="userRoles.includes('ROLE_MITARBEITERIN')"
            @click="geheZuPageBearbeite(item)"
          >
            bearbeite
          </span>
        </v-chip>
        <v-chip
          v-if="item.lieferungsStatus === 'FEHLER'"
          color="error"
          @click="deleteLieferung(item)"
        >
          löschen
        </v-chip>
        <v-chip
          v-if="
            userRoles.includes('ROLE_LIEFERANT') &&
            ['FEHLER', 'VERARBEITET'].includes(item.lieferungsStatus)
          "
          class="mr-2"
          color="primary"
          @click="download(item)"
        >
          download
        </v-chip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
import { API_URL, authservice } from '@/services/AuthService'
import LieferungDto from '@/models/LieferungDto'

export default {
  name: 'Lieferungen',
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  data() {
    return {
      search: '',
      expanded: [],
      headersLieferung: [
        { text: 'Lieferdatum', value: 'lieferDatum' },
        { text: 'Lieferantname', value: 'lieferant.name' },
        { text: '', value: 'optionen' },
      ],
      lieferungList: [],
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'userRoles', 'token']),
  },
  created() {
    if (
      this.isAuthenticated &&
      this.userRoles.some((role) =>
        ['ROLE_LIEFERANT', 'ROLE_MITARBEITERIN'].includes(role)
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
        API_URL + 'lieferung/get',
        authservice.getHeaderMitToken(this.token)
      )
      this.lieferungList = result.data
    },
    async geheZuPageBearbeite(item) {
      if (
        item.lieferungsStatus === 'BESTAETIGT' ||
        item.lieferungsStatus === 'INBEARBEITUNG'
      ) {
        await this.$axios.get(
          API_URL + 'bearbeitelieferung/' + item.id,
          authservice.getHeaderMitToken(this.token)
        )
        await this.$store.dispatch('lieferung/setAktuelleLieferung', {
          lieferantName: item.lieferant.name,
          lieferantId: item.lieferant.id,
          lieferdatum: item.lieferDatum,
        })
        await this.$router.push('/bearbeite/' + item.id)
        // in router-Ziel wird es ausgelesen: mounted() { this.getData()}
      }
    },
    async geheZuPageCheckLieferung(item) {
      await this.$store.dispatch('lieferung/setAktuelleLieferung', {
        lieferantName: item.lieferant.name,
        lieferantId: item.lieferant.id,
        lieferdatum: item.lieferDatum,
      })
      await this.$router.push('/checklieferung/' + item.id)
      // in router-Ziel wird es ausgelesen: mounted() { this.getData()}
    },
    async gebeFrei(item) {
      if (item.lieferungsStatus === 'NEU') {
        const lieferungDto = new LieferungDto(item.id, item.lieferant.id)
        const result = await this.$axios.post(
          API_URL + 'checklieferung/freigabe',
          lieferungDto,
          authservice.getHeaderMitToken(this.token)
        )
        const lieferung = result.data
        if (lieferung.lieferungsStatus === 'BESTAETIGT') {
          item.lieferungsStatus = 'BESTAETIGT'
        }
      }
    },
    async deleteLieferung(item) {
      const lieferungDto = new LieferungDto(item.id, item.lieferant.id)
      await this.$axios.post(
        API_URL + 'checklieferung/delete',
        lieferungDto,
        authservice.getHeaderMitToken(this.token)
      )
      const index = this.lieferungList.findIndex(
        (element) => element.id === item.id
      )
      this.lieferungList.splice(index, 1)
    },
    async download(item) {
      let response
      const lieferungDto = new LieferungDto(item.id, item.lieferant.id)
      if (item.lieferungsStatus === 'FEHLER') {
        response = await this.$axios.post(
          API_URL + 'csv/download/fehler',
          lieferungDto,
          authservice.getHeaderMitToken(this.token)
        )
      } else {
        response = await this.$axios.post(
          API_URL + 'csv/download/warebearbeitet',
          lieferungDto,
          authservice.getHeaderMitToken(this.token)
        )
      }
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      link.href = url
      const fileName = `FehlerLieferdatum${item.lieferDatum}.csv` // whatever your file name .
      link.setAttribute('download', fileName)
      document.body.appendChild(link)
      link.click()
      link.remove() // you need to remove that element which is created before.
    },
    getColor(lieferungsStatus) {
      switch (lieferungsStatus) {
        case 'FEHLER':
          return 'error'
        case 'VERARBEITET':
          return 'secondary'
        default:
          return 'accent'
      }
    },
    getStatusText(lieferungsStatus) {
      switch (lieferungsStatus) {
        case 'FEHLER':
          return 'Fehler'
        case 'NEU':
          return 'neu'
        case 'BESTAETIGT':
          return 'bestätigt'
        case 'INBEARBEITUNG':
          return 'in Bearbeitung'
        case 'VERARBEITET':
          return 'verarbeitet'
      }
    },
    formatDate(date) {
      return new Intl.DateTimeFormat().format(new Date(date))
    },
  },
}
</script>

<style scoped></style>
