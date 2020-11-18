<template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <!-- <v-card-title></v-card-title>
      Ein Titel macht an dieser Stelle am Zeilenumende
      Umbrüche innerhalb eines Wortes. Daher Titel außerhalb v-card-title
      -->
      <v-card-text>
        <h3>Produkt:</h3>
        <p>{{ item.name }}</p>
        <h3>Produktnummer:</h3>
        <p>{{ item.nummer }}</p>
        <h3>Gemeldete Menge:</h3>
        <p>{{ item.menge }}</p>
        <h3>Gelieferte Menge:</h3>
        <p>{{ item.mengeeditiert }}</p>
        <h3>Bemerkung:</h3>
        <p>{{ item.bemerkung }}</p>
        <h3>Zustand:</h3>
        <v-chip label :color="getColor(item.zustand)">
          {{ getStatusText(item.zustand) }}
        </v-chip>
      </v-card-text>
      <v-card-actions>
        <v-btn color="blue darken-1" text @click="close"> Close</v-btn>
        <v-spacer></v-spacer>
        <v-chip class="mr-2" color="primary" @click="verschiebe(item)">
          Bearbeite
        </v-chip>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: 'AnzeigeDialog',
  props: {
    item: {
      type: Object,
      required: true,
    },
    dialogBearbeitet: {
      type: Boolean,
      required: true,
    },
  },
  computed: {
    dialog() {
      return this.dialogBearbeitet
    },
  },
  methods: {
    verschiebe(wareBearbeitet) {
      this.close(wareBearbeitet)
      this.$emit('closeBearbeite', wareBearbeitet)
    },
    close() {
      this.$emit('closeBearbeite')
    },
    getStatusText(zustand) {
      switch (zustand) {
        case 'OK':
          return 'Ok'
        case 'FEHLT':
          return 'fehlt'
        case 'TEILWEISE_GELIEFERT':
          return 'teilweise geliefert'
        case 'FEHLER':
          return 'Fehler'
      }
    },
    getColor(zustand) {
      switch (zustand) {
        case 'OK':
          return 'success'
        case 'FEHLT':
          return 'error'
        case 'TEILWEISE_GELIEFERT':
          return 'warning'
        case 'FEHLER':
          return 'secondary'
      }
    },
  },
}
</script>

<style scoped></style>
