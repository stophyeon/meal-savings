<script setup>
const props = defineProps({
  records: { type: Array, required: true },
  totalSavings: { type: Number, required: true },
})

const emit = defineEmits(['back'])

function formatWon(value) {
  return `${new Intl.NumberFormat('ko-KR').format(value)}원`
}
</script>

<template>
  <div class="flow-screen savings-screen">
    <main class="flow-layout savings-layout">
      <div class="flow-heading savings-heading">
        <div><p class="flow-eyebrow">SAVINGS JOURNAL</p><h1>차곡차곡 모인<br class="mobile-break" /> 나의 절약 기록<span>.</span></h1><p>집밥 한 끼마다 아낀 금액을 모아 보여드려요.</p></div>
        <div class="savings-total-card"><span>현재 세션 누적</span><strong>{{ formatWon(totalSavings) }}</strong><small>{{ records.length }}끼 기록</small></div>
      </div>

      <section v-if="records.length" class="savings-records" aria-label="절약 기록 목록">
        <article v-for="record in records" :key="record.id" class="savings-record">
          <span class="record-emoji" aria-hidden="true">{{ record.emoji }}</span>
          <div class="record-main"><strong>{{ record.name }}</strong><span>{{ record.dateLabel }} · {{ record.servings }}인분 · 한 끼 사용 {{ formatWon(record.cookingCost) }} · 첫 장보기 {{ formatWon(record.shoppingCost) }}</span></div>
          <div class="record-amount"><strong>+{{ formatWon(record.savings) }}</strong><span>외식 {{ formatWon(record.dineOutCost) }}</span></div>
        </article>
        <p class="savings-session-note">미리보기 세션에 저장된 기록입니다. 새로고침하면 초기화되며, 계정과 DB 저장은 아직 연결되지 않았어요.</p>
      </section>

      <section v-else class="savings-empty">
        <span class="empty-plate" aria-hidden="true">🍽️</span>
        <p class="flow-eyebrow">YOUR FIRST MEAL</p>
        <h2>아직 기록한 집밥이 없어요</h2>
        <p>메뉴를 골라 재료비를 비교하고 요리를 완료하면 이곳에 절약액이 쌓여요.</p>
        <button class="flow-primary-button" type="button" @click="emit('back')">첫 메뉴 골라보기 <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M4 10h11m-5-5 5 5-5 5" /></svg></button>
      </section>
    </main>
  </div>
</template>
