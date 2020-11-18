<template>
  <v-container>
    <v-row>
      <v-col>
        <v-tabs dark background-color="teal darken-3" show-arrows>
          <v-tabs-slider color="teal lighten-3"></v-tabs-slider>
          <v-tab @click="meldungReiter = true">Meldung</v-tab>
          <v-tab @click="meldungReiter = false">Bearbeitet</v-tab>
        </v-tabs>
      </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" md="8">
        Lieferant: {{ lieferantName }}, Lieferdatum:
        {{ lieferdatum }}
      </v-col>
      <v-spacer></v-spacer>
      <v-col v-if="!isCameraActive && !isCameraButtonClicked" cols="12" md="4">
        <v-btn class="mr-3" elevation="2" fab @click="turnOnCamera">
          <v-icon color="red darken-2">mdi-camera-outline</v-icon>
        </v-btn>
        <SpeechToText class="mr-3" @speech="onSpeechReceived($event)" />
        <v-chip class="mr-2" color="accent" dark @click="getData">
          Aktualisiere
        </v-chip>
      </v-col>
    </v-row>
    <v-row v-if="!isCameraActive && isCameraButtonClicked">
      <v-col class="d-flex justify-space-around">
        <v-chip color="error" dark @click="activateCamera">
          Aktiviere Kamera
        </v-chip>
        <v-chip color="error" dark @click="isCameraButtonClicked = false">
          Kamera nicht aktivieren
        </v-chip>
      </v-col>
    </v-row>
    <v-row class="d-flex justify-center">
      <v-col>
        <Video v-if="isCameraActive" @scanneranswer="scannerAnswer"></Video>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-card>
          <v-chip
            v-if="!meldungReiter && wareEingangList.length === 0"
            class="ml-2"
            color="success"
            dark
            @click="beendeBearbeitung"
          >
            Lieferung als bearbeitet bestätigen
          </v-chip>
          <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="Suche, auch mit Barcode-Scanner oder Spracherkennung"
            single-line
            hide-details
          ></v-text-field>
          <transition-group name="slide-up" tag="div" appear>
            <v-data-table
              v-if="meldungReiter"
              :key="1"
              :headers="headersWareEingang"
              :items="wareEingangList"
              :items-per-page="5"
              mobile-breakpoint="0"
              class="elevation-1"
              :search="search"
              :custom-filter="filterSearchIncludesProduktnummer"
              show-expand
              single-expand
              :expanded.sync="expandedMeldung"
              @click:row="handleRowClick"
            >
              <template v-slot:item.ok="{ item }">
                <v-chip
                  class="mr-2"
                  color="success"
                  @click.stop="verschiebe(item, 'OK')"
                >
                  OK
                </v-chip>
              </template>
              <template v-slot:expanded-item="{ headers, item }">
                <td :colspan="headers.length">
                  Menge: {{ item.menge }}, Produktnummer: {{ item.nummer }}
                </td>
              </template>
              <template v-slot:top>
                <BearbeiteDialog
                  :dialog="dialog"
                  :item="editedItem"
                  @close="verschiebe"
                ></BearbeiteDialog>
              </template>
            </v-data-table>

            <v-data-table
              v-if="!meldungReiter"
              :key="2"
              :headers="headersWareBearbeitet"
              :items="wareBearbeitetList"
              :items-per-page="5"
              mobile-breakpoint="0"
              class="elevation-1"
              :custom-filter="filterSearchIncludesProduktnummer"
              :search="search"
              show-expand
              single-expand
              :expanded.sync="expandedBearbeitet"
              @click:row="handleRowClickBearbeitet"
            >
              <template v-slot:expanded-item="{ headers, item }">
                <td :colspan="headers.length">
                  Produktnummer: {{ item.nummer }}
                </td>
              </template>
              <template v-slot:top>
                <AnzeigeDialog
                  :dialog-bearbeitet="dialogBearbeitet"
                  :item="displayedItem"
                  @closeBearbeite="closeBearbeitet"
                ></AnzeigeDialog>
              </template>
            </v-data-table>
          </transition-group>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import LieferungDto from '@/models/LieferungDto'
import WareDto from '@/models/WareDto'
import { API_URL, authservice } from '@/services/AuthService'
import { mapGetters } from 'vuex'
import BearbeiteDialog from '@/components/BearbeiteDialog'
import AnzeigeDialog from '@/components/AnzeigeDialog'
import Video from '@/components/Video'
import SpeechToText from '../../components/SpeechToText.vue'

export default {
  name: 'Bearbeite',
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  components: {
    SpeechToText,
    BearbeiteDialog,
    AnzeigeDialog,
    Video,
  },
  data() {
    return {
      isCameraButtonClicked: false,
      isCameraActive: false,
      dialog: false,
      dialogBearbeitet: false,
      editedItem: {},
      editNew: 'Edit',
      displayedItem: {},
      search: '',
      lieferantName: 'TestGepa',
      lieferantId: 0,
      lieferdatum: '11.11.1111',
      meldungReiter: true,
      headersWareEingang: [
        { text: '', value: 'ok' },
        { text: 'Beschreibung', value: 'name' },
      ],
      wareEingangList: [],
      expandedMeldung: [],
      expandedBearbeitet: [],
      headersWareBearbeitet: [
        { text: '', value: 'edit' },
        { text: 'Beschreibung', value: 'name' },
      ],
      wareBearbeitetList: [],
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
      this.lieferantName = this.$store.state.lieferung.lieferantName
      this.lieferantId = this.$store.state.lieferung.lieferantId
      this.lieferdatum = new Intl.DateTimeFormat().format(
        new Date(this.$store.state.lieferung.lieferdatum)
      )
      this.getData()
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    async getData() {
      const lieferungDto = new LieferungDto(
        this.$route.params.id,
        this.lieferantId
      )
      const result = await this.$axios.post(
        API_URL + 'ware/allezulieferung',
        lieferungDto,
        authservice.getHeaderMitToken(this.token)
      )
      this.wareEingangList = result.data.wareEingangList
      this.wareBearbeitetList = result.data.wareBearbeitetList
    },
    onSpeechReceived(speech) {
      this.search = speech
    },
    turnOnCamera() {
      this.isCameraButtonClicked = true
    },
    activateCamera() {
      this.isCameraActive = true
    },
    scannerAnswer(code) {
      if (code) {
        this.search = code
      }
      this.isCameraActive = false
      this.isCameraButtonClicked = false
    },
    handleRowClick(item) {
      this.editedItem = Object.assign({}, item)
      this.editNew = null
      this.dialog = true
    },
    handleRowClickBearbeitet(item) {
      this.displayedItem = Object.assign({}, item)
      this.dialogBearbeitet = true
    },
    async verschiebe(item, typ) {
      this.dialog = false
      this.editedItem = {}
      if (item) {
        let neuerZustand
        let neueMenge
        if (typ) {
          // Aufruf aus OK-Button in v-data-table
          neuerZustand = typ
          neueMenge = item.menge
        } else {
          // Aufruf aus BearbeiteDialog
          neuerZustand = item.zustand
          neueMenge = item.mengeeditiert
        }
        const wareDto = new WareDto(
          item.name,
          item.nummer,
          neueMenge,
          item.bemerkung,
          neuerZustand,
          this.lieferantId,
          this.$route.params.id
        )
        const result = await this.$axios.post(
          API_URL + 'ware/geliefert',
          wareDto,
          authservice.getHeaderMitToken(this.token)
        )
        const wareBearbeitet = result.data
        if (wareBearbeitet) {
          const alterIndex = this.wareEingangList.findIndex(
            (element) =>
              element.name === wareBearbeitet.name &&
              element.nummer === wareBearbeitet.nummer
          )
          this.wareEingangList.splice(alterIndex, 1)
          this.wareBearbeitetList.unshift(wareBearbeitet)
        }
      }
    },
    async closeBearbeitet(ware) {
      this.dialogBearbeitet = false
      this.displayedItem = {}
      if (ware) {
        const wareDto = new WareDto(
          ware.name,
          ware.nummer,
          ware.mengeeditiert,
          ware.bemerkung,
          ware.zustand,
          this.lieferantId,
          this.$route.params.id
        )
        const result = await this.$axios.post(
          API_URL + 'ware/zubearbeiten',
          wareDto,
          authservice.getHeaderMitToken(this.token)
        )
        const wareEingang = result.data
        if (wareEingang) {
          const alterIndex = this.wareBearbeitetList.findIndex(
            (element) =>
              element.name === wareEingang.name &&
              element.nummer === wareEingang.nummer
          )
          this.wareBearbeitetList.splice(alterIndex, 1)
          this.wareEingangList.unshift(wareEingang)
        }
      }
    },
    async beendeBearbeitung() {
      await this.$axios.get(
        API_URL + 'bearbeitelieferung/beende/' + this.$route.params.id,
        authservice.getHeaderMitToken(this.token)
      )
      await this.$router.push('/lieferungen/')
    },
    filterSearchIncludesProduktnummer(value, search, item) {
      const arrayOfColumnEntries = Object.values(item)
      const wrongColumnSearchValues = [
        true,
        false,
        'OK',
        'FEHLT',
        'TEILWEISE_GELIEFERT',
        'FEHLER',
      ]
      const arrayFilteredOutWrongFields = arrayOfColumnEntries.filter(
        (item) => !wrongColumnSearchValues.includes(item)
      )
      return arrayFilteredOutWrongFields.some(
        (v) =>
          v &&
          v.toString().toLowerCase().includes(search.toString().toLowerCase())
      )
    },
  },
}
</script>

<style scoped>
.slide-up-enter {
  transform: translateX(40px);
  opacity: 0;
}

.slide-up-enter-active {
  transition: all 0.7s ease;
}
</style>
