<template>
  <v-row justify="center" align="center">
    <v-col cols="12" sm="8" md="6">
      <v-card>
        <v-card-text>
          <h2>Willkommen zum Wareneingangs-Programm</h2>
          <p v-if="isAuthenticated">
            Liebe*r Benutzer*in {{ loggedInUser.username }}
          </p>
          <p>
            Hiermit können sie Dateien der Lieferanten einlesen und die
            Positionen der Lieferung einzeln bearbeiten.
          </p>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  transition: {
    name: 'page',
    mode: 'out-in',
  },
  computed: {
    ...mapGetters(['isAuthenticated', 'loggedInUser']),
  },
}
</script>
