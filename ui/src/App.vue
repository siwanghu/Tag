<template>
  <div id="app">
    <EditVoice></EditVoice>
  </div>
</template>

<script>
  import EditVoice from './components/EditVoice'
  export default {
    name: 'app',
    components: {
      'EditVoice': EditVoice
    }
  }
</script>

<style>
  #app {
  }
</style>
