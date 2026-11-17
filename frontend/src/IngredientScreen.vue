<script setup>
import { computed, ref, watch } from 'vue'
import { calculateMealEstimate } from './meal-data.js'

const props = defineProps({
  menu: { type: Object, required: true },
  servings: { type: Number, required: true },
  ownedIngredients: { type: Array, required: true },
})

const emit = defineEmits(['back', 'compare'])
const currentServings = ref(props.servings)
const owned = ref([...props.ownedIngredients])

watch(() => props.servings, (value) => { currentServings.value = value })
watch(() => props.ownedIngredients, (value) => { owned.value = [...value] })

const estimate = computed(() => calculateMealEstimate(props.menu, currentServings.value, owned.value))

function formatWon(value) {
  return `${new Intl.NumberFormat('ko-KR').format(value)}원`
}

function toggleIngredient(name, checked) {
  owned.value = checked
    ? [...new Set([...owned.value, name])]
    : owned.value.filter((item) => item !== name)
}

function changeServings(delta) {
  currentServings.value = Math.min(8, Math.max(1, currentServings.value + delta))
}

function continueToComparison() {
  emit('compare', { servings: currentServings.value, ownedIngredients: owned.value })
}
</script>

<template>
  <div class="flow-screen">
    <main class="flow-layout">
      <nav class="flow-steps" aria-label="식비 비교 단계">
        <span class="flow-step done"><b>01</b> 메뉴 선택</span><i></i>
        <span class="flow-step active"><b>02</b> 재료 확인</span><i></i>
        <span class="flow-step"><b>03</b> 비용 비교</span>
      </nav>

      <div class="flow-heading">
        <div>
          <p class="flow-eyebrow">INGREDIENT CHECK</p>
          <h1>냉장고에 있는 재료를<br class="mobile-break" /> 체크해 주세요<span>.</span></h1>
          <p>가지고 있는 재료와 필요한 양을 기준으로 집밥 비용을 계산해요.</p>
        </div>
        <div class="chosen-menu-chip"><span>{{ menu.emoji }}</span><div><small>선택한 메뉴</small><b>{{ menu.name }}</b></div></div>
      </div>

      <div class="ingredient-layout">
        <section class="ingredient-card">
          <div class="ingredient-card-heading">
            <div><h2>필요한 재료</h2><p>집에 있는 재료를 체크하면 장보기 비용이 계산돼요.</p></div>
            <div class="servings-control" aria-label="인분 선택">
              <button type="button" aria-label="인분 줄이기" :disabled="currentServings <= 1" @click="changeServings(-1)">−</button>
              <span><b>{{ currentServings }}</b>인분</span>
              <button type="button" aria-label="인분 늘리기" :disabled="currentServings >= 8" @click="changeServings(1)">+</button>
            </div>
          </div>

          <div class="ingredient-list">
            <label
              v-for="(ingredient, index) in estimate.ingredients"
              :key="ingredient.name"
              class="ingredient-item"
              :class="{ 'ingredient-item-owned': ingredient.owned }"
              :for="`ingredient-${menu.id}-${index}`"
            >
              <input
                :id="`ingredient-${menu.id}-${index}`"
                type="checkbox"
                :checked="ingredient.owned"
                @change="toggleIngredient(ingredient.name, $event.target.checked)"
              />
              <span class="ingredient-checkbox" aria-hidden="true"><svg viewBox="0 0 16 16" fill="none"><path d="m3.5 8 3 3 6-6" /></svg></span>
              <span class="ingredient-item-main">
                <b>{{ ingredient.name }}</b>
                <small>기준 사용량 {{ ingredient.amount }} · 사용 재료비 {{ formatWon(ingredient.usageCost) }}</small>
              </span>
              <span v-if="ingredient.owned" class="ingredient-state state-owned">보유 중</span>
              <span v-else class="ingredient-state state-missing">구매 {{ formatWon(ingredient.purchaseCost) }}</span>
            </label>
          </div>

          <p class="ingredient-footnote"><span aria-hidden="true">ⓘ</span> 사용 재료비는 레시피에서 실제 사용하는 분량, 구매 예상액은 포장 단위 기준의 예시예요.</p>
        </section>

        <aside class="ingredient-summary">
          <div class="summary-topline"><span>현재 계산</span><span class="summary-menu-emoji" aria-hidden="true">{{ menu.emoji }}</span></div>
          <h2>{{ menu.name }}</h2>
          <p class="summary-meta">{{ currentServings }}인분 · 주요 재료 {{ estimate.ingredients.length }}개</p>

          <div class="ingredient-progress"><span :style="{ width: `${estimate.ownedCount / estimate.ingredients.length * 100}%` }"></span></div>
          <div class="progress-caption"><span>보유 재료</span><b>{{ estimate.ownedCount }} / {{ estimate.ingredients.length }}</b></div>

          <div class="summary-divider"></div>
          <div class="summary-price-line"><span>집밥 재료 사용액</span><b>{{ formatWon(estimate.cookingCost) }}</b></div>
          <div class="summary-price-line"><span>추가 장보기 예상</span><b class="summary-missing-price">{{ formatWon(estimate.shoppingCost) }}</b></div>

          <button class="flow-primary-button" type="button" @click="continueToComparison">
            집밥 비용 비교하기
            <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M4 10h11m-5-5 5 5-5 5" /></svg>
          </button>
          <button class="summary-secondary-button" type="button" @click="emit('back')">다른 메뉴 고르기</button>
          <p class="summary-note">가격은 실제 데이터 연동 전 샘플 기준입니다.</p>
        </aside>
      </div>
    </main>
  </div>
</template>
