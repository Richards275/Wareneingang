export default {
  state() {
    return {
      lieferantName: '',
      lieferantId: '',
      lieferdatum: '',
    }
  },
  mutations: {
    SET_AKTUELLE_LIEFERUNG(state, item) {
      state.lieferantName = item.lieferantName
      state.lieferantId = item.lieferantId
      state.lieferdatum = item.lieferdatum
    },
  },
  actions: {
    setAktuelleLieferung(
      { commit },
      { lieferantName, lieferantId, lieferdatum }
    ) {
      commit('SET_AKTUELLE_LIEFERUNG', {
        lieferantName,
        lieferantId,
        lieferdatum,
      })
    },
  },
}
