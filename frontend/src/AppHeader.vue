<script setup>
import { computed } from 'vue'

const props = defineProps({ activeScreen: { type: String, required: true } })
const emit = defineEmits(['navigate', 'logout'])

const activeTab = computed(() => {
  if (['home', 'ingredients', 'comparison', 'completion'].includes(props.activeScreen)) return 'home'
  if (props.activeScreen === 'savings') return 'savings'
  return ''
})
</script>

<template>
  <header class="app-header">
    <div class="app-header-inner">
      <a class="brand" href="#main-top" aria-label="한끼절약 메인으로" @click.prevent="emit('navigate', 'home')">
        <span class="brand-mark" aria-hidden="true">
          <svg viewBox="0 0 32 32" fill="none"><path d="M7 17.1c0-5 4-9.1 9-9.1s9 4.1 9 9.1c0 4.1-4 7.4-9 7.4s-9-3.3-9-7.4Z" /><path d="M16 7.4c.2-2.1 1.7-3.6 4.4-4.4-.1 2.8-1.5 4.6-4.4 5.2M10 18.5c2.1 1.6 4.1 2.3 6 2.3s3.9-.7 6-2.3" /></svg>
        </span>
        <span class="brand-name">한끼절약</span>
      </a>

      <nav class="app-tabs" aria-label="주요 메뉴">
        <button class="app-tab" :class="{ 'app-tab-active': activeTab === 'home' }" type="button" :aria-current="activeTab === 'home' ? 'page' : undefined" @click="emit('navigate', 'home')">메뉴 찾기</button>
        <button class="app-tab" :class="{ 'app-tab-active': activeTab === 'savings' }" type="button" :aria-current="activeTab === 'savings' ? 'page' : undefined" @click="emit('navigate', 'savings')">절약 기록</button>
      </nav>

      <div class="app-header-account">
        <span class="app-preview-badge"><i></i> 미리보기</span>
        <span class="app-avatar" aria-hidden="true">한</span>
        <button class="app-logout" type="button" @click="emit('logout')">나가기</button>
      </div>
    </div>
  </header>
</template>
