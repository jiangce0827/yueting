import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus'; //导入所有模块、功能
import 'element-plus/dist/index.css'; //导入所有所需的css样式
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'; //导入 ElementPlus 组件库的中文语言包
import VueCropper from 'vue-cropper';
import 'vue-cropper/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'; //导入 ElementPlus 组件库中的所有图标
const app = createApp(App);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component);
}
app.use(ElementPlus, {
	locale: zhCn, //使用中文语言包
});
app.use(router);
app.use(VueCropper);
app.use(createPinia());
app.mount('#app');
