<script setup>
import { computed } from 'vue'

const props = defineProps({
  menu: { type: Object, required: true },
  servings: { type: Number, required: true },
  ownedIngredients: { type: Array, required: true },
  estimate: { type: Object, required: true },
})

const emit = defineEmits(['back', 'home', 'record'])
const positiveSaving = computed(() => props.estimate.savings >= 0)
const comparisonText = computed(() => `${props.estimate.savings < 0 ? '−' : '+'}${new Intl.NumberFormat('ko-KR').format(Math.abs(props.estimate.savings))}원`)
const cookingBarWidth = computed(() => `${Math.min(100, props.estimate.cookingCost / Math.max(props.estimate.dineOutCost, 1) * 100)}%`)
const shoppingDifference = computed(() => props.estimate.shoppingCost - props.estimate.dineOutCost)
const shoppingDifferenceText = computed(() => `${new Intl.NumberFormat('ko-KR').format(Math.abs(shoppingDifference.value))}원`)

function formatWon(value) {
  return `${new Intl.NumberFormat('ko-KR').format(value)}원`
}
</script>

<template>
  <div class="flow-screen">
    <header class="flow-topbar">
      <div class="flow-topbar-inner">
        <button class="flow-back" type="button" @click="emit('back')"><svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M16 10H4m5 5-5-5 5-5" /></svg> 재료 확인으로</button>
        <a class="brand" href="#main-top" aria-label="한끼절약"><span class="brand-mark" aria-hidden="true"><svg viewBox="0 0 32 32" fill="none"><path d="M7 17.1c0-5 4-9.1 9-9.1s9 4.1 9 9.1c0 4.1-4 7.4-9 7.4s-9-3.3-9-7.4Z" /><path d="M16 7.4c.2-2.1 1.7-3.6 4.4-4.4-.1 2.8-1.5 4.6-4.4 5.2M10 18.5c2.1 1.6 4.1 2.3 6 2.3s3.9-.7 6-2.3" /></svg></span><span class="brand-name">한끼절약</span></a>
        <span class="flow-demo-badge"><i></i> 미리보기</span>
      </div>
    </header>

    <main class="flow-layout comparison-layout">
      <nav class="flow-steps" aria-label="식비 비교 단계">
        <span class="flow-step done"><b>01</b> 메뉴 선택</span><i></i>
        <span class="flow-step done"><b>02</b> 재료 확인</span><i></i>
        <span class="flow-step active"><b>03</b> 비용 비교</span>
      </nav>

      <div class="flow-heading comparison-heading">
        <div>
          <p class="flow-eyebrow">COST COMPARISON</p>
          <h1>집밥으로 얼마나 아낄까요<span>?</span></h1>
          <p>{{ menu.name }} · {{ servings }}인분 기준으로 비교했어요.</p>
        </div>
        <span class="comparison-menu-icon" aria-hidden="true">{{ menu.emoji }}</span>
      </div>

      <section class="comparison-result" :class="{ 'comparison-result-positive': positiveSaving }">
        <div class="comparison-result-copy">
          <span>{{ positiveSaving ? '한 끼 사용량 기준 예상 절약' : '한 끼 사용량 기준 비용 비교' }}</span>
          <strong>{{ comparisonText }}</strong>
          <small>{{ positiveSaving ? '요리 완료를 기록하면 사용량 기준 절약액을 더해요.' : '집에 있는 재료와 포장 단위에 따라 실제 지출은 달라져요.' }}</small>
        </div>
        <div class="comparison-result-emoji" aria-hidden="true">{{ menu.emoji }}</div>
        <span class="comparison-result-decoration" aria-hidden="true">✳</span>
      </section>

      <section class="comparison-grid" aria-label="집밥과 외식 가격">
        <article class="comparison-card cooking-card">
          <div class="comparison-card-head"><span class="comparison-icon home-icon" aria-hidden="true">⌂</span><span>집밥 예상 재료비</span></div>
          <strong>{{ formatWon(estimate.cookingCost) }}</strong>
          <small>{{ servings }}인분에 실제 사용하는 분량</small>
          <div class="comparison-bar-track"><span :style="{ width: cookingBarWidth }"></span></div>
          <div class="comparison-bar-label"><span>재료 사용액</span><b>{{ Math.round(estimate.cookingCost / Math.max(estimate.dineOutCost, 1) * 100) }}%</b></div>
        </article>

        <article class="comparison-card eating-card">
          <div class="comparison-card-head"><span class="comparison-icon restaurant-icon" aria-hidden="true">↗</span><span>외식 기준 가격</span></div>
          <strong>{{ formatWon(estimate.dineOutCost) }}</strong>
          <small>{{ servings }}인분 외식 예상 비용</small>
          <div class="comparison-bar-track"><span style="width:100%"></span></div>
          <div class="comparison-bar-label"><span>메뉴 기준 가격</span><b>100%</b></div>
        </article>
      </section>

      <section class="shopping-callout">
        <div class="shopping-callout-icon" aria-hidden="true">🛒</div>
        <div><strong>처음 장보기 {{ formatWon(estimate.shoppingCost) }} · 외식 1회보다 {{ shoppingDifferenceText }} {{ shoppingDifference >= 0 ? '더 먼저 지출해요' : '적게 들어요' }}.</strong><p>없는 재료를 포장 단위로 구매한 금액이에요. 한 끼 사용량 비용과 차이가 있으며 남은 재료는 다음 요리에 사용할 수 있어요.</p></div>
        <span class="shopping-callout-label">추가 구매</span>
      </section>

      <section class="comparison-breakdown">
        <div class="breakdown-heading"><div><h2>비용을 나눠서 볼게요</h2><p>선택한 재료 보유 여부를 기준으로 계산했습니다.</p></div><button type="button" @click="emit('back')">재료 수정</button></div>
        <div class="breakdown-row"><span>집에 있는 재료 <b>{{ estimate.ownedCount }}개</b></span><strong>{{ formatWon(estimate.ingredients.filter((item) => item.owned).reduce((sum, item) => sum + item.usageCost, 0)) }}</strong></div>
        <div class="breakdown-row"><span>새로 구매할 재료 <b>{{ estimate.ingredients.length - estimate.ownedCount }}개</b></span><strong>{{ formatWon(estimate.ingredients.filter((item) => !item.owned).reduce((sum, item) => sum + item.usageCost, 0)) }}</strong></div>
        <div class="breakdown-row breakdown-total"><span>레시피에 사용할 재료비</span><strong>{{ formatWon(estimate.cookingCost) }}</strong></div>
        <p class="comparison-disclaimer">가격은 샘플 데이터입니다. 참가격 및 재료 가격 이력 연동 후 실제 기준으로 바뀝니다.</p>
      </section>

      <div class="comparison-actions">
        <button class="comparison-secondary" type="button" @click="emit('home')">메뉴 다시 고르기</button>
        <button class="flow-primary-button" type="button" @click="emit('record')">요리 완료 기록하기 <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M4 10h11m-5-5 5 5-5 5" /></svg></button>
      </div>
    </main>
  </div>
</template>
