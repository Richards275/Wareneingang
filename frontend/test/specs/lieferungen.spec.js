import Vue from 'vue'
import test from 'ava'
import { createLocalVue, shallowMount } from '@vue/test-utils'
import Vuex from 'vuex'
import Vuetify from 'vuetify'
import MockAdapter from 'axios-mock-adapter'
import axios from 'axios'
import { state } from '@/store'
import Lieferungen from '@/pages/lieferungen'
import VueRouter from 'vue-router'
import { API_URL } from '@/services/AuthService'
import sinon from 'sinon'

const mock = new MockAdapter(axios)
Vue.use(Vuetify)
const localVue = createLocalVue()
localVue.use(Vuex)
localVue.use(VueRouter)

const routes = [{ path: '/' }]
let router = new VueRouter({ routes, mode: 'abstract' })
let store, getters

function createStore(state, getters) {
  return new Vuex.Store({
    state,
    getters
  })
}

let vuetify

test.beforeEach((t) => {
  vuetify = new Vuetify()
  store = createStore(state)
  mock.onGet(API_URL + 'lieferung/get').reply(200, ['name'])
})

test('should route away if not authenticated', (t) => {
  const localVue = createLocalVue()
  localVue.use(Vuex)
  const getters = {
    isAuthenticated: () => false,
    userRoles: () => ['ROLE_MITARBEITERIN']
  }
  store = createStore(state, getters)
  const spy = sinon.spy()
  let wrapper = shallowMount(Lieferungen, {
    store,
    localVue,
    mocks: {
      $router: {
        push: spy
      },
      $axios: axios
    }

  })
  t.true(spy.calledOnce)
  t.true(spy.calledWith('/'))
  t.false(spy.calledWith('/lieferungen'))
})

test('should route away if role admin', (t) => {
  const localVue = createLocalVue()
  localVue.use(Vuex)
  const getters = {
    isAuthenticated: () => true,
    userRoles: () => ['ROLE_ADMIN']
  }
  store = createStore(state, getters)
  const spy = sinon.spy()
  shallowMount(Lieferungen, {
    store,
    localVue,
    mocks: {
      $router: {
        push: spy
      },
      $axios: axios
    }

  })
  t.true(spy.calledOnce)
  t.true(spy.calledWith('/'))
  t.false(spy.calledWith('/lieferungen'))
})


test('Lieferungen is a Vue instance and renders Text', (t) => {
  const localVue = createLocalVue()
  localVue.use(Vuex)
  localVue.use(VueRouter)
  getters =
    {
      isAuthenticated: () => true,
      userRoles: () => ['ROLE_MITARBEITERIN']
    }
  store = createStore(state, getters)
  let wrapper = shallowMount(Lieferungen, {
    store,
    localVue,
    router,
    mocks: {
      $axios: axios
    }
  })
  t.truthy(wrapper.vm)
  t.true(wrapper.text().includes('Bitte klicken Sie zum Bearbeiten auf eine Zeile'))
  t.true(wrapper.html().includes('data-test="explanation"'))
  t.true(wrapper.findAll('[data-test="explanation"]').isVisible())
  wrapper.vm.$router.push('lieferung')
})


test('should call store on click prüfen', async (t) => {
  mock.onGet(API_URL + 'lieferung/get').reply(200, ['hola'])
  const localVue = createLocalVue()
  localVue.use(Vuex)
  localVue.use(VueRouter)
  getters =
    {
      isAuthenticated: () => true,
      userRoles: () => ['ROLE_MITARBEITERIN']
    }
  store = createStore(state, getters)
  let wrapper = shallowMount(Lieferungen, {
    store,
    localVue,
    router,
    mocks: {
      $axios: axios
    }
  })
  const button = wrapper.findAll('[data-test="Aktualisiere"]')
  t.true(button.isVisible())
  await button.trigger('click')
  await Vue.nextTick()
  t.true('hola' === wrapper.vm.$data.lieferungList[0])

})

