import Vue from 'vue'
import Router from 'vue-router'
import EditVoice from '@/components/EditVoice'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'EditVoice',
      component: EditVoice
    }
  ]
})
