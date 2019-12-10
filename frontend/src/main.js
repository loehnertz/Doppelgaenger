import Vue from 'vue'
import App from './App.vue'
import VueClipboard from 'vue-clipboard2'

Vue.config.productionTip = false;

Vue.prototype.$backendHost = location.hostname + ':8888';

Vue.use(VueClipboard);

new Vue({
    render: h => h(App),
}).$mount('#app');
