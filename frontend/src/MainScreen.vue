<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const emit = defineEmits(['logout'])

const searchQuery = ref('')
const selectedCategory = ref('전체')
const sortBy = ref('saving')
const selectedMenu = ref(null)
const favoriteIds = ref([])
const toastMessage = ref('')
let toastTimer

const categories = ['전체', '밥·덮밥', '국·찌개', '면·파스타', '볶음·구이', '간단요리']
const quickSearches = ['김치찌개', '계란', '두부', '닭가슴살']

const menus = [
  {
    id: 1,
    name: '김치찌개',
    category: '국·찌개',
    emoji: '🍲',
    art: 'art-kimchi',
    time: 25,
    servings: 2,
    ingredientCount: 8,
    homeCost: 6800,
    dineOutCost: 10000,
    description: '익은 김치와 돼지고기로 끓이는 든든한 한 끼',
    ingredients: ['김치', '돼지고기', '두부', '대파'],
    searchTerms: '김치 돼지고기 찌개 국물 한식',
  },
  {
    id: 2,
    name: '참치마요 덮밥',
    category: '밥·덮밥',
    emoji: '🍛',
    art: 'art-tuna',
    time: 15,
    servings: 1,
    ingredientCount: 6,
    homeCost: 3200,
    dineOutCost: 8000,
    description: '참치와 달걀을 올려 간단히 완성하는 덮밥',
    ingredients: ['참치캔', '달걀', '밥', '마요네즈'],
    searchTerms: '참치 마요 덮밥 밥 달걀 계란',
  },
  {
    id: 3,
    name: '된장찌개',
    category: '국·찌개',
    emoji: '🥘',
    art: 'art-doenjang',
    time: 20,
    servings: 2,
    ingredientCount: 7,
    homeCost: 5400,
    dineOutCost: 9000,
    description: '냉장고 속 채소를 넣어 보글보글 끓여요',
    ingredients: ['된장', '애호박', '두부', '양파'],
    searchTerms: '된장 찌개 애호박 두부 채소 국',
  },
  {
    id: 4,
    name: '제육볶음',
    category: '볶음·구이',
    emoji: '🥓',
    art: 'art-jeyuk',
    time: 30,
    servings: 2,
    ingredientCount: 9,
    homeCost: 7200,
    dineOutCost: 11000,
    description: '매콤달콤한 양념에 볶아 밥과 함께 즐겨요',
    ingredients: ['돼지고기', '양파', '대파', '고추장'],
    searchTerms: '제육 돼지고기 볶음 매콤 고추장',
  },
  {
    id: 5,
    name: '토마토 파스타',
    category: '면·파스타',
    emoji: '🍝',
    art: 'art-pasta',
    time: 25,
    servings: 2,
    ingredientCount: 7,
    homeCost: 8600,
    dineOutCost: 13000,
    description: '토마토소스와 마늘 향으로 만드는 주말 메뉴',
    ingredients: ['파스타면', '토마토소스', '마늘', '양파'],
    searchTerms: '토마토 파스타 면 스파게티 마늘',
  },
  {
    id: 6,
    name: '계란볶음밥',
    category: '밥·덮밥',
    emoji: '🍳',
    art: 'art-egg',
    time: 12,
    servings: 1,
    ingredientCount: 5,
    homeCost: 2500,
    dineOutCost: 7000,
    description: '남은 밥과 달걀로 빠르게 만드는 한 그릇',
    ingredients: ['달걀', '밥', '대파', '간장'],
    searchTerms: '계란 달걀 볶음밥 밥 간단',
  },
  {
    id: 7,
    name: '두부조림',
    category: '간단요리',
    emoji: '🍽️',
    art: 'art-tofu',
    time: 18,
    servings: 2,
    ingredientCount: 6,
    homeCost: 3900,
    dineOutCost: 8500,
    description: '두부에 짭조름한 양념을 더한 밥도둑 반찬',
    ingredients: ['두부', '대파', '간장', '고춧가루'],
    searchTerms: '두부 조림 반찬 간단 밥',
  },
  {
    id: 8,
    name: '닭가슴살 샐러드',
    category: '간단요리',
    emoji: '🥗',
    art: 'art-salad',
    time: 15,
    servings: 1,
    ingredientCount: 6,
    homeCost: 4800,
    dineOutCost: 10500,
    description: '신선한 채소와 닭가슴살로 가볍게 채워요',
    ingredients: ['닭가슴살', '양상추', '방울토마토', '드레싱'],
    searchTerms: '닭가슴살 샐러드 채소 다이어트 간단',
  },
]

const visibleMenus = computed(() => {
  const query = searchQuery.value.trim().toLocaleLowerCase('ko-KR')
  const results = menus.filter((menu) => {
    const matchesCategory = selectedCategory.value === '전체' || menu.category === selectedCategory.value
    const searchable = `${menu.name} ${menu.category} ${menu.description} ${menu.ingredients.join(' ')} ${menu.searchTerms}`
      .toLocaleLowerCase('ko-KR')
    return matchesCategory && (!query || searchable.includes(query))
  })

  return results.sort((a, b) => {
    if (sortBy.value === 'time') return a.time - b.time
    if (sortBy.value === 'cost') return a.homeCost - b.homeCost
    return b.dineOutCost - b.homeCost - (a.dineOutCost - a.homeCost)
  })
})

function formatWon(value) {
  return `${new Intl.NumberFormat('ko-KR').format(value)}원`
}

function toggleFavorite(menuId) {
  favoriteIds.value = favoriteIds.value.includes(menuId)
    ? favoriteIds.value.filter((id) => id !== menuId)
    : [...favoriteIds.value, menuId]
}

function showToast(message) {
  toastMessage.value = message
  window.clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => {
    toastMessage.value = ''
  }, 2800)
}

function closeMenuDialog() {
  selectedMenu.value = null
}

function openMenuDialog(menu) {
  selectedMenu.value = menu
}

function handleKeydown(event) {
  if (event.key === 'Escape') closeMenuDialog()
}

onMounted(() => window.addEventListener('keydown', handleKeydown))
onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown)
  window.clearTimeout(toastTimer)
})
</script>

<template>
  <div class="main-screen">
    <header class="main-topbar">
      <div class="main-topbar-inner">
        <a class="brand" href="#main-top" aria-label="한끼절약 메인으로">
          <span class="brand-mark" aria-hidden="true">
            <svg viewBox="0 0 32 32" fill="none">
              <path d="M7 17.1c0-5 4-9.1 9-9.1s9 4.1 9 9.1c0 4.1-4 7.4-9 7.4s-9-3.3-9-7.4Z" />
              <path d="M16 7.4c.2-2.1 1.7-3.6 4.4-4.4-.1 2.8-1.5 4.6-4.4 5.2M10 18.5c2.1 1.6 4.1 2.3 6 2.3s3.9-.7 6-2.3" />
            </svg>
          </span>
          <span class="brand-name">한끼절약</span>
        </a>

        <nav class="main-nav" aria-label="주요 메뉴">
          <a class="main-nav-link active" href="#menu-list">메뉴 찾기</a>
          <button class="main-nav-link" type="button" @click="showToast('절약 기록 화면은 다음 단계에서 연결할 예정이에요.')">
            절약 기록
          </button>
        </nav>

        <div class="main-account">
          <span class="demo-badge"><span aria-hidden="true"></span> 미리보기</span>
          <span class="account-avatar" aria-hidden="true">한</span>
          <button class="logout-button" type="button" @click="emit('logout')">나가기</button>
        </div>
      </div>
    </header>

    <main id="main-top" class="main-layout">
      <section class="main-hero" aria-labelledby="main-title">
        <div class="hero-content">
          <p class="main-eyebrow"><span class="eyebrow-sparkle" aria-hidden="true">✦</span> 오늘의 식비를 가볍게</p>
          <h1 id="main-title">오늘은 어떤 메뉴로<br /><span>식비를 아껴볼까요?</span></h1>
          <p class="hero-description">메뉴를 고르고 냉장고에 있는 재료를 체크하면<br class="desktop-break" /> 집밥과 외식의 가격을 비교해 드려요.</p>

          <div class="main-search-wrap">
            <label class="sr-only" for="menu-search">메뉴 검색</label>
            <div class="main-search">
              <svg viewBox="0 0 24 24" fill="none" aria-hidden="true">
                <circle cx="10.8" cy="10.8" r="6.8" />
                <path d="m16 16 4.2 4.2" />
              </svg>
              <input
                id="menu-search"
                v-model.trim="searchQuery"
                type="search"
                placeholder="만들고 싶은 메뉴를 검색해 보세요"
                autocomplete="off"
              />
              <button v-if="searchQuery" class="clear-search" type="button" aria-label="검색어 지우기" @click="searchQuery = ''">
                <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="m5 5 10 10M15 5 5 15" /></svg>
              </button>
              <span v-else class="search-key" aria-hidden="true">검색</span>
            </div>
          </div>

          <div class="quick-searches" aria-label="추천 검색어">
            <span>인기 검색</span>
            <button v-for="term in quickSearches" :key="term" type="button" @click="searchQuery = term">{{ term }}</button>
          </div>
        </div>

        <aside class="monthly-card" aria-label="이번 달 절약 요약 예시">
          <div class="monthly-card-top">
            <span class="monthly-icon" aria-hidden="true">₩</span>
            <span class="monthly-dots" aria-hidden="true">•••</span>
          </div>
          <span class="monthly-label">이번 달 예상 절약액 <span>예시</span></span>
          <strong class="monthly-total">18,400<span>원</span></strong>
          <div class="monthly-progress" aria-hidden="true"><span></span></div>
          <div class="monthly-card-bottom">
            <span>집밥으로 차곡차곡</span>
            <span><b>6</b>끼 기록</span>
          </div>
          <span class="monthly-decoration" aria-hidden="true">✳</span>
        </aside>
      </section>

      <section id="menu-list" class="menu-section" aria-labelledby="menu-section-title">
        <div class="menu-section-heading">
          <div>
            <p class="section-kicker">MENU PICKER</p>
            <h2 id="menu-section-title">만들고 싶은 메뉴를 골라보세요<span>.</span></h2>
            <p class="section-description">메뉴를 선택하면 필요한 재료와 예상 절약액을 확인할 수 있어요.</p>
          </div>
          <label class="sort-control">
            <span>정렬</span>
            <select v-model="sortBy" aria-label="메뉴 정렬">
              <option value="saving">절약액 높은 순</option>
              <option value="time">조리시간 짧은 순</option>
              <option value="cost">집밥 재료비 낮은 순</option>
            </select>
            <svg viewBox="0 0 16 16" fill="none" aria-hidden="true"><path d="m4 6 4 4 4-4" /></svg>
          </label>
        </div>

        <div class="category-row" role="group" aria-label="메뉴 카테고리">
          <button
            v-for="category in categories"
            :key="category"
            class="category-chip"
            :class="{ 'category-chip-active': selectedCategory === category }"
            type="button"
            :aria-pressed="selectedCategory === category"
            @click="selectedCategory = category"
          >{{ category }}</button>
          <span class="result-count"><b>{{ visibleMenus.length }}</b>개 메뉴</span>
        </div>

        <div v-if="visibleMenus.length" class="menu-grid">
          <article v-for="menu in visibleMenus" :key="menu.id" class="menu-card">
            <div class="menu-art" :class="menu.art">
              <span class="menu-emoji" aria-hidden="true">{{ menu.emoji }}</span>
              <span class="menu-art-label">{{ menu.category }}</span>
              <button
                class="favorite-button"
                :class="{ 'favorite-active': favoriteIds.includes(menu.id) }"
                type="button"
                :aria-label="favoriteIds.includes(menu.id) ? `${menu.name} 즐겨찾기 해제` : `${menu.name} 즐겨찾기 추가`"
                :aria-pressed="favoriteIds.includes(menu.id)"
                @click="toggleFavorite(menu.id)"
              >
                <svg viewBox="0 0 24 24" :fill="favoriteIds.includes(menu.id) ? 'currentColor' : 'none'" aria-hidden="true">
                  <path d="M20.8 8.7c0 4.3-8.8 10.1-8.8 10.1S3.2 13 3.2 8.7a4.7 4.7 0 0 1 8.8-2.3 4.7 4.7 0 0 1 8.8 2.3Z" />
                </svg>
              </button>
              <span class="time-badge"><svg viewBox="0 0 16 16" fill="none" aria-hidden="true"><circle cx="8" cy="8" r="5.5" /><path d="M8 4.8V8l2.1 1.3" /></svg>{{ menu.time }}분</span>
            </div>

            <div class="menu-card-content">
              <div class="menu-card-title-row">
                <div>
                  <h3>{{ menu.name }}</h3>
                  <p>{{ menu.description }}</p>
                </div>
                <span class="servings-badge">{{ menu.servings }}인분</span>
              </div>

              <div class="ingredient-count"><span class="ingredient-dot" aria-hidden="true">✦</span> 필요한 재료 <b>{{ menu.ingredientCount }}개</b></div>

              <div class="price-comparison">
                <div class="price-row"><span>집밥 예상 재료비</span><strong>{{ formatWon(menu.homeCost) }}</strong></div>
                <div class="price-row"><span>외식 기준 가격</span><strong class="restaurant-price">{{ formatWon(menu.dineOutCost) }}</strong></div>
                <div class="saving-row"><span>집밥으로 아껴요</span><strong>+{{ formatWon(menu.dineOutCost - menu.homeCost) }}</strong></div>
              </div>

              <button class="menu-select-button" type="button" @click="openMenuDialog(menu)">
                메뉴 선택하기
                <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M4 10h11M10 5l5 5-5 5" /></svg>
              </button>
            </div>
          </article>
        </div>

        <div v-else class="empty-results">
          <span class="empty-icon" aria-hidden="true">🔎</span>
          <h3>검색 결과가 없어요</h3>
          <p>다른 메뉴 이름으로 검색하거나 카테고리를 바꿔보세요.</p>
          <button type="button" @click="searchQuery = ''; selectedCategory = '전체'">검색 초기화</button>
        </div>
      </section>

      <footer class="main-footer">
        <span>가격은 예시 데이터이며 실제 재료 가격과 다를 수 있어요.</span>
        <span>한 끼의 선택이 만드는 작은 절약</span>
      </footer>
    </main>

    <Transition name="toast-fade">
      <div v-if="toastMessage" class="main-toast" role="status" aria-live="polite">
        <span class="toast-check" aria-hidden="true">✓</span>{{ toastMessage }}
      </div>
    </Transition>

    <Transition name="dialog-fade">
      <div v-if="selectedMenu" class="dialog-backdrop" @click.self="closeMenuDialog">
        <section class="menu-dialog" role="dialog" aria-modal="true" :aria-labelledby="`dialog-title-${selectedMenu.id}`">
          <button class="dialog-close" type="button" aria-label="닫기" @click="closeMenuDialog">
            <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="m5 5 10 10M15 5 5 15" /></svg>
          </button>
          <div class="dialog-art" :class="selectedMenu.art"><span aria-hidden="true">{{ selectedMenu.emoji }}</span></div>
          <p class="dialog-kicker">{{ selectedMenu.category }} · {{ selectedMenu.time }}분 · {{ selectedMenu.servings }}인분</p>
          <h2 :id="`dialog-title-${selectedMenu.id}`">{{ selectedMenu.name }}</h2>
          <p class="dialog-description">{{ selectedMenu.description }}</p>

          <div class="dialog-ingredients">
            <div class="dialog-subheading"><strong>주요 재료</strong><span>전체 {{ selectedMenu.ingredientCount }}개 중</span></div>
            <div class="ingredient-tags">
              <span v-for="ingredient in selectedMenu.ingredients" :key="ingredient">{{ ingredient }}</span>
              <span class="more-ingredients">외 {{ selectedMenu.ingredientCount - selectedMenu.ingredients.length }}개</span>
            </div>
          </div>

          <div class="dialog-savings">
            <span>외식 대신 집밥으로 예상 절약</span>
            <strong>+{{ formatWon(selectedMenu.dineOutCost - selectedMenu.homeCost) }}</strong>
          </div>

          <button class="dialog-primary" type="button" @click="closeMenuDialog(); showToast('재료 보유 확인 화면은 다음 단계에서 연결할 예정이에요.')">
            재료 보유 여부 확인하기
            <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M4 10h11M10 5l5 5-5 5" /></svg>
          </button>
          <p class="dialog-note">현재는 메뉴 선택 미리보기 단계예요. 재료 계산 기능은 준비 중입니다.</p>
        </section>
      </div>
    </Transition>
  </div>
</template>
