<template>
  <v-container>
    <v-row>
      <v-col class="d-flex justify-center">
        <v-btn class="mr-3" elevation="2" fab @click="scannerAnswer(null)">
          <v-icon color="red darken-2">mdi-camera-off</v-icon>
        </v-btn>
        <v-btn class="mr-3" elevation="2" fab @click="capture($event)">
          <v-icon color="red darken-2">mdi-camera-outline</v-icon>
        </v-btn>
      </v-col>
    </v-row>
    <v-row v-if="errorMsg">
      <v-col class="d-flex justify-center">
        <v-chip close color="red" outlined @click:close="errorMsg = ''">
          {{ errorMsg }}
        </v-chip>
      </v-col>
    </v-row>
    <v-row>
      <v-col class="d-flex justify-center">
        <video
          id="player"
          ref="player"
          autoplay
          width="300"
          :class="{ hidden: isSnapshot }"
        ></video>
        <!-- Start: show taken picture by removing hidden attribute in col and canvas -->
        <canvas
          id="canvas"
          ref="canvas"
          hidden
          :class="{ hidden: !isSnapshot, snapshot: isSnapshotTaken }"
        ></canvas>
        <!-- End: show taken picture by removing hidden attribute in col and canvas -->
        <input
          id="image-picker"
          ref="image-picker"
          hidden
          type="file"
          accept="image/*"
        />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Quagga from 'quagga'

export default {
  name: 'Video',
  props: {},
  mounted() {
    this.initializeMedia(this.$refs.player)
    this.errorMsg = ''
  },
  data() {
    return {
      isSnapshot: false,
      isSnapshotTaken: false,
      errorMsg: '',
    }
  },
  computed: {},
  methods: {
    async scannerAnswer(code) {
      await this.stopVideo(this.$refs.player)
      this.$emit('scanneranswer', code)
    },
    capture(ev) {
      this.message = '' // da neue Aufnahme
      this.isSnapshot = true
      const canvas = this.$refs.canvas
      const context = this.$refs.canvas.getContext('2d')
      const videoPlayer = this.$refs.player
      context.drawImage(videoPlayer, 0, 0, canvas.width, canvas.height)
      this.generateBarcode(canvas.toDataURL('image/jpeg', 1))
    },
    generateBarcode(dataURIjpg) {
      const self = this
      dataURIjpg = dataURIjpg.replace('image/jpeg', 'image/jpg')
      Quagga.decodeSingle(
        {
          numOfWorkers: 0, // Needs to be 0 when used within node
          decoder: {
            readers: ['ean_reader'], // List of active readers
          },
          locate: true, // try to locate the barcode in the image
          multiple: false,
          src: dataURIjpg,
        },
        function (result) {
          if (!result || !result.codeResult) {
            self.initialState()
            self.errorMsg = `Der Barcode wurde nicht erkannt.`
          } else {
            self.scannerAnswer(result.codeResult.code)
          }
        }
      )
    },
    initialState() {
      this.isSnapshot = false
      this.isSnapshotTaken = false
    },
    async initializeMedia(ElemRef) {
      const self = this
      try {
        const mediaStream = await navigator.mediaDevices.getUserMedia({
          video: true,
        })
        const video = ElemRef
        video.srcObject = mediaStream
        video.onloadedmetadata = function (e) {
          video.play()
        }
      } catch (err) {
        self.$emit('setcamerainactive') //  self.isCameraActive = false
      }
    },
    stopVideo(ElemRef) {
      ElemRef.srcObject.getTracks().forEach((track) => track.stop())
      this.$emit('setcamerainactive') //  self.isCameraActive = false
    },
  },
}
</script>

<style scoped></style>
