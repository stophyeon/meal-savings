<script setup>
import { computed } from 'vue'

const props = defineProps({
  record: { type: Object, required: true },
  totalSavings: { type: Number, required: true },
})

const emit = defineEmits(['home', 'history'])
const savedSomething = computed(() => props.record.savings > 0)

function formatWon(value) {
  return `${new Intl.NumberFormat('ko-KR').format(value)}원`
}
</script>

<template>
  <div class="flow-screen completion-screen">
    <main class="completion-layout">
      <section class="completion-card">
        <div class="completion-mark" aria-hidden="true"><span>✓</span><i>✦</i><b>✦</b></div>
        <p class="flow-eyebrow">MEAL SAVED</p>
        <h1>{{ savedSomething ? '오늘도 알뜰한 한 끼 완료!' : '오늘의 요리를 기록했어요.' }}</h1>
        <p class="completion-subtitle">{{ record.dateLabel }} · {{ record.name }} · {{ record.servings }}인분</p>

        <div class="completion-savings-box" :class="{ 'completion-no-savings': !savedSomething }">
          <span>{{ savedSomething ? '이번 요리로 아낀 식비' : '이번 요리의 예상 절약액' }}</span>
          <strong>{{ formatWon(record.savings) }}</strong>
          <small>{{ savedSomething ? '절약 기록에 더했어요.' : '외식 기준 가격보다 집밥 재료비가 높아 절약액은 0원으로 기록했어요.' }}</small>
          <span class="completion-emoji" aria-hidden="true">{{ record.emoji }}</span>
        </div>

        <div class="completion-total"><span>이번 세션 누적 절약</span><strong>{{ formatWon(totalSavings) }}</strong></div>
        <div class="completion-actions">
          <button class="flow-primary-button" type="button" @click="emit('home')">다른 메뉴 둘러보기</button>
          <button class="completion-secondary" type="button" @click="emit('history')">절약 기록 보기</button>
        </div>
        <p class="completion-note">미리보기 세션 기록이며, 새로고침하면 초기화됩니다. 계정·DB 저장은 아직 연결되지 않았어요.</p>
      </section>
    </main>
  </div>
</template>
