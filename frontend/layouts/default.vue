<template>
  <v-app>
    <v-navigation-drawer
      v-model="drawer"
      :mini-variant="miniVariant"
      :clipped="clipped"
      fixed
      app
    >
      <v-list>
        <v-list-item
          v-for="(item, i) in items"
          :key="i"
          :to="item.to"
          router
          exact
        >
          <v-list-item-action>
            <v-icon>{{ item.icon }}</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title v-text="item.title" />
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <v-app-bar :clipped-left="clipped" fixed app>
      <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
      <v-toolbar-title v-text="title" />
      <v-spacer></v-spacer>
    </v-app-bar>
    <v-main>
      <v-container>
        <nuxt />
      </v-container>
    </v-main>
    <v-footer :absolute="!fixed" app>
      <span>&copy; {{ new Date().getFullYear() }}</span>
    </v-footer>
  </v-app>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      clipped: false,
      drawer: false,
      fixed: false,
      miniVariant: false,
      right: true,
      rightDrawer: false,
      title: 'Wareneingang',
    }
  },
  computed: {
    ...mapGetters(['userRoles']),
    items() {
      const menuItems = [
        {
          icon: 'mdi-apps',
          title: 'Welcome',
          to: '/',
        },
        {
          icon: 'mdi-chart-bubble',
          title: 'Login/Logout',
          to: '/login',
        },
      ]
      if (this.userRoles && this.userRoles.includes('ROLE_LIEFERANT')) {
        menuItems.push({
          icon: 'mdi-chart-bubble',
          title: 'csv hochladen',
          to: '/uploadcsv',
        })
      }
      if (
        this.userRoles &&
        this.userRoles.some((role) =>
          ['ROLE_LIEFERANT', 'ROLE_MITARBEITERIN'].includes(role)
        )
      ) {
        menuItems.push({
          icon: 'mdi-chart-bubble',
          title: 'Lieferungen',
          to: '/lieferungen',
        })
      }
      if (
        this.userRoles &&
        this.userRoles.some((role) =>
          ['ROLE_ADMIN', 'ROLE_MITARBEITERIN'].includes(role)
        )
      ) {
        menuItems.push({
          icon: 'mdi-chart-bubble',
          title: 'Lieferanten',
          to: '/lieferanten',
        })
      }
      if (this.userRoles && this.userRoles.includes('ROLE_ADMIN')) {
        menuItems.push({
          icon: 'mdi-chart-bubble',
          title: 'User',
          to: '/user',
        })
      }
      return menuItems
    },
  },
}
</script>
