export default class WareDto {
  constructor(
    name,
    nummer,
    mengeeditiert,
    bemerkung,
    zustand,
    lieferantId,
    lieferungId
  ) {
    this.name = name
    this.nummer = nummer
    this.mengeeditiert = mengeeditiert
    this.bemerkung = bemerkung
    this.zustand = zustand
    this.lieferantId = lieferantId
    this.lieferungId = lieferungId
  }
}
