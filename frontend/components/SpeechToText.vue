<template>
  <v-btn fab class="mr-3" @click="onClick()">
    <v-icon v-if="!isSpeaking" color="red darken-2">mdi-microphone</v-icon>
    <v-icon v-if="isSpeaking" color="red darken-2">mdi-square</v-icon>
  </v-btn>
</template>

<script>
import SpeechToText from '../services/SpeechToText'

export default {
  name: 'SpeechToText',
  data() {
    return {
      isSpeaking: false,
      speech: '',
      speechService: {},
    }
  },
  created() {
    this.speechService = new SpeechToText()
  },
  methods: {
    onClick() {
      this.isSpeaking = true
      this.speechService.speak().subscribe(
        (result) => {
          this.speech = result
          this.$emit('speech', this.speech)
          this.isSpeaking = false
        },
        (error) => {
          console.log('Error', error)
          this.isSpeaking = false
        },
        () => {
          this.isSpeaking = false
        }
      )
    },
  },
}
</script>
