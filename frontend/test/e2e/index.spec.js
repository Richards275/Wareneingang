import { resolve } from 'path'
import test from 'ava'
import { Builder, Nuxt } from 'nuxt'

let nuxt = null

test.before(async (t) => {
  const config = {
    dev: false,
    rootDir: resolve(__dirname, '../../'),
  }
  nuxt = new Nuxt(config)
  t.context.nuxt = nuxt
  await new Builder(nuxt).build()
  await nuxt.server.listen(4000, 'localhost')
}, 30000)

test('Route /login exists and renders HTML', async (t) => {
  const { html } = await nuxt.renderRoute('/login', {})
  t.true(html.includes('Login'))
  t.true(html.includes('Username'))
})


test.after('Closing server and nuxt.js', () => {
  nuxt.close()
})
