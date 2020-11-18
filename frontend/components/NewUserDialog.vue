<template>
  <v-dialog v-model="dialog" max-width="800px" data-test="newuserdialog">
    <v-card>
      <v-card-text>
        <v-container>
          <v-row>
            <v-col>
              <h3>Zu Lieferant {{ item.name }} neue*n Benutzer*in anlegen</h3>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-text-field
                v-model="neuerUser.username"
                label="Username"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="neuerUser.email"
                label="Email"
              ></v-text-field>
            </v-col>
            <v-col cols="12">
              <v-select
                v-model="neuerUser.role"
                :items="possibleUserRoles"
                label="Rollen"
                multiple
              ></v-select>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="cancel"> Cancel</v-btn>
        <v-btn color="blue darken-1" text @click="saveNewUser"> Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'NewUserDialog',
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
      neuerUser: {},
    }
  },
  computed: {
    ...mapGetters(['possibleUserRoles']),
  },
  methods: {
    saveNewUser() {
      this.neuerUser.lieferantId = this.item.id
      this.$emit('newUser', this.neuerUser)
      this.close()
    },
    cancel() {
      this.$emit('newUser')
      this.close()
    },
    close() {
      this.neuerUser = {}
    },
  },
}
</script>

<style scoped></style>
