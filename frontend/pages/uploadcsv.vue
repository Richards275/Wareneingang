<template>
  <div>
    <div v-if="currentFile">
      <div>
        <v-progress-linear
          v-model="progress"
          color="light-blue"
          height="25"
          reactive
        >
          <strong>{{ progress }} %</strong>
        </v-progress-linear>
      </div>
    </div>

    <v-alert v-if="message" border="left" color="amber darken-3" dark>
      {{ message }}
    </v-alert>

    <v-row justify="center">
      <v-col cols="12" lg="9">
        <h2>Laden Sie Ihre Lieferdaten hoch</h2>
        <v-form
          ref="form"
          v-model="valid"
          lazy-validation
          @submit.prevent="upload"
        >
          <v-file-input
            v-model="currentFile"
            show-size
            label="csv-Datei auswählen"
            :rules="dateiRules"
            @change="selectFile"
          ></v-file-input>
          <v-textarea
            v-model="bemerkung"
            label="Bemerkung"
            auto-grow
            rows="1"
          />
          <v-text-field
            v-model="lieferdatum"
            label="Lieferdatum"
            placeholder="Bitte im Kalender auswählen"
            readonly
            :rules="[
              () => !!lieferdatum || 'Bitte Lieferdatum im Kalender auswählen.',
            ]"
          />
          <v-btn color="success" rounded dark type="submit">
            csv-Datei hochladen
          </v-btn>
        </v-form>
      </v-col>
      <v-col cols="12" lg="3">
        <v-date-picker v-model="lieferdatum" color="accent" />
      </v-col>
    </v-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { API_URL, authservice } from '@/services/AuthService'

export default {
  name: 'UploadWareCsv',
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  data() {
    return {
      valid: true,
      currentFile: {},
      progress: 0,
      message: '',
      fileInfos: [],
      lieferdatum: '',
      bemerkung: '',
      dateiRules: [
        (value) => value || 'Eine Datei muss ausgewählt werden',
        (value) =>
          (value && value.size <= 100000) ||
          'die Dateigröße darf höchstens 100000 Zeichen betragen',
        (value) =>
          (value && value.name && value.name.endsWith('csv')) ||
          'der Dateityp muss csv sein',
      ],
    }
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'userRoles', 'token', 'loggedInUser']),
  },
  created() {
    if (this.isAuthenticated && this.userRoles.includes('ROLE_LIEFERANT')) {
      // ok
    } else {
      this.$router.push('/')
    }
  },
  methods: {
    selectFile(file) {
      this.progress = 0
      this.currentFile = file
    },
    async upload() {
      if (!this.$refs.form.validate()) {
        return
      }
      this.message = ''
      try {
        const CSVRequestBody = {
          userId: this.loggedInUser.id,
          lieferantId: this.loggedInUser.lieferantId,
          lieferdatum: this.lieferdatum,
          bemerkung: this.bemerkung,
        }
        const csvParameters = JSON.stringify(CSVRequestBody)
        const onUploadProgress = (event) => {
          this.progress = Math.round((100 * event.loaded) / event.total)
        }
        const formData = new FormData()
        formData.append('file', this.currentFile)
        formData.append('csvparams', csvParameters)
        const response = await this.$axios.post(
          API_URL + 'csv/upload/wareeingang',
          formData,
          authservice.getHeaderMitBearerTokenAndMultipartFile(
            onUploadProgress,
            this.token
          )
        )
        this.message = response.data.message
        this.clear()
      } catch {
        this.progress = 0
        this.message = 'Could not upload the file!'
        this.clear()
      }
    },
    clear() {
      // lösche Einträge in input Feldern
      this.currentFile = undefined
      this.bemerkung = undefined
      this.lieferdatum = undefined
      this.$refs.form.resetValidation()
    },
  },
}
</script>

<style scoped></style>
