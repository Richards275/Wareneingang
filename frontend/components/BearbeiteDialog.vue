<template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-text>
        <h4>Produkt:</h4>
        <p>{{ item.name }}</p>
        <h4>Produktnummer:</h4>
        <p>{{ item.nummer }}</p>
        <h4>Gemeldete Menge:</h4>
        <p>{{ item.menge }}</p>
        <v-container>
          <v-row v-if="neuerZustand === 'TEILWEISE_GELIEFERT'">
            <v-col>
              <v-text-field
                v-model="editedItem.mengeeditiert"
                label="gelieferte Menge"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row
            v-if="
              neuerZustand === 'TEILWEISE_GELIEFERT' ||
              neuerZustand === 'FEHLER'
            "
          >
            <v-col>
              <v-text-field
                v-model="editedItem.bemerkung"
                label="Bemerkung"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row v-if="!neuerZustand">
            <v-col cols="12" sm="6">
              <v-chip color="success" @click="verschiebe(item, 'OK')">
                OK
              </v-chip>
            </v-col>
            <v-col cols="12" sm="6">
              <v-chip color="error" @click="verschiebe(item, 'FEHLT')">
                Fehlt
              </v-chip>
            </v-col>
            <v-col cols="12" sm="6">
              <v-chip
                color="warning"
                @click="editItem(item, 'TEILWEISE_GELIEFERT')"
              >
                andere Menge
              </v-chip>
            </v-col>
            <v-col cols="12" sm="6">
              <v-chip color="accent" @click="editItem(item, 'FEHLER')">
                Fehler
              </v-chip>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-btn
          v-if="
            neuerZustand === 'TEILWEISE_GELIEFERT' || neuerZustand === 'FEHLER'
          "
          color="blue darken-1"
          text
          @click="save"
        >
          Save
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="cancel"> Cancel</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  name: 'BearbeiteDialog',
  props: {
    item: {
      type: Object,
      required: true,
    },
    dialog: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      wareDto: {},
      editedItem: {},
      neuerZustand: '',
    }
  },
  methods: {
    editItem(item, typ) {
      this.neuerZustand = typ
      this.editedItem = Object.assign({}, item)
    },
    verschiebe(item, typ) {
      const ware = Object.assign({}, item)
      ware.zustand = typ
      this.close(ware)
    },
    save() {
      this.editedItem.zustand = this.neuerZustand
      this.close(this.editedItem)
    },
    cancel() {
      this.close()
    },
    close(wareEditiert) {
      this.editedItem = {}
      this.neuerZustand = ''
      this.$emit('close', wareEditiert)
    },
  },
}
</script>

<style scoped></style>
