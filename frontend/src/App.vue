<script setup>
import { ref } from 'vue'
import MainScreen from './MainScreen.vue'

const showMain = ref(false)
const email = ref('')
const password = ref('')
const rememberMe = ref(true)
const showPassword = ref(false)
const feedback = ref('')
const weekDays = [
  { name: '월', height: 34 },
  { name: '화', height: 52 },
  { name: '수', height: 43 },
  { name: '목', height: 72 },
  { name: '금', height: 56 },
  { name: '토', height: 88, active: true },
  { name: '일', height: 28 },
]

function handleLogin() {
  showMain.value = true
}

function showComingSoon(feature) {
  feedback.value = `${feature} 기능은 다음 단계에서 연결할 예정이에요.`
}
</script>

<template>
  <MainScreen v-if="showMain" @logout="showMain = false" />
  <div v-else class="page-shell">
    <header class="topbar">
      <a class="brand" href="#top" aria-label="한끼절약 홈">
        <span class="brand-mark" aria-hidden="true">
          <svg viewBox="0 0 32 32" fill="none">
            <path d="M7 17.1c0-5 4-9.1 9-9.1s9 4.1 9 9.1c0 4.1-4 7.4-9 7.4s-9-3.3-9-7.4Z" />
            <path d="M16 7.4c.2-2.1 1.7-3.6 4.4-4.4-.1 2.8-1.5 4.6-4.4 5.2M10 18.5c2.1 1.6 4.1 2.3 6 2.3s3.9-.7 6-2.3" />
          </svg>
        </span>
        <span class="brand-name">한끼절약</span>
      </a>

      <p class="topbar-note">
        <span class="status-dot" aria-hidden="true"></span>
        집밥으로 만드는 작은 여유
      </p>
    </header>

    <main id="top" class="content-grid">
      <section class="intro-panel" aria-labelledby="intro-title">
        <div class="intro-copy">
          <p class="eyebrow"><span>MEAL SAVINGS</span> 오늘의 한 끼부터</p>
          <h1 id="intro-title">잘 먹고,<br />가볍게 아껴요<span class="title-period">.</span></h1>
          <p class="intro-description">
            냉장고에 있는 재료로 만들 수 있는 메뉴를 찾고,
            외식보다 아낀 식비를 한눈에 확인해 보세요.
          </p>
        </div>

        <div class="savings-card" aria-label="한끼절약 서비스 미리보기">
          <div class="card-heading">
            <div class="card-heading-copy">
              <span class="card-kicker">예시 · 이번 주 집밥 리포트</span>
              <strong>한 끼씩 모인 절약</strong>
            </div>
            <span class="report-icon" aria-hidden="true">✳</span>
          </div>

          <div class="savings-total-row">
            <div>
              <span class="muted-label">이번 주 예상 절약액</span>
              <p class="savings-total">18,400<span>원</span></p>
            </div>
            <span class="trend-pill"><span aria-hidden="true">↗</span> 지난주보다 12%</span>
          </div>

          <div class="chart" aria-label="월요일부터 일요일까지의 집밥 기록">
            <div v-for="day in weekDays" :key="day.name" class="chart-day">
              <div class="bar-track">
                <span class="bar" :class="{ 'bar-active': day.active }" :style="{ height: `${day.height}%` }"></span>
              </div>
              <span class="day-label" :class="{ 'day-active': day.active }">{{ day.name }}</span>
            </div>
          </div>

          <div class="meal-example">
            <span class="meal-emoji" aria-hidden="true">🍲</span>
            <div class="meal-info">
              <strong>오늘의 집밥 · 김치찌개</strong>
              <span>외식보다 <b>2,300원</b> 아꼈어요</span>
            </div>
            <svg class="meal-arrow" viewBox="0 0 20 20" fill="none" aria-hidden="true">
              <path d="m7.5 4.5 5.5 5.5-5.5 5.5" />
            </svg>
          </div>
        </div>

        <div class="intro-footnote">
          <span class="footnote-icon" aria-hidden="true">✓</span>
          가격과 기준일을 함께 확인할 수 있어요
        </div>
        <span class="decor decor-one" aria-hidden="true"></span>
        <span class="decor decor-two" aria-hidden="true"></span>
      </section>

      <section class="login-panel" aria-labelledby="login-title">
        <div class="login-card">
          <div class="mobile-brand" aria-hidden="true">
            <span class="brand-mark">
              <svg viewBox="0 0 32 32" fill="none">
                <path d="M7 17.1c0-5 4-9.1 9-9.1s9 4.1 9 9.1c0 4.1-4 7.4-9 7.4s-9-3.3-9-7.4Z" />
                <path d="M16 7.4c.2-2.1 1.7-3.6 4.4-4.4-.1 2.8-1.5 4.6-4.4 5.2M10 18.5c2.1 1.6 4.1 2.3 6 2.3s3.9-.7 6-2.3" />
              </svg>
            </span>
            <span class="brand-name">한끼절약</span>
          </div>

          <div class="login-heading">
            <span class="welcome-chip"><span aria-hidden="true">✦</span> 다시 만나 반가워요</span>
            <h2 id="login-title">로그인</h2>
            <p>계정에 로그인하고<br class="mobile-break" /> 나의 식비 기록을 이어가세요.</p>
          </div>

          <form class="login-form" @submit.prevent="handleLogin">
            <label class="field-label" for="email">이메일</label>
            <div class="input-wrap">
              <svg class="input-icon" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                <rect x="2.5" y="4" width="15" height="12" rx="2.5" />
                <path d="m3.5 5.5 6.5 5 6.5-5" />
              </svg>
              <input
                id="email"
                v-model.trim="email"
                type="email"
                name="email"
                autocomplete="email"
                placeholder="name@example.com"
                required
              />
            </div>

            <div class="password-label-row">
              <label class="field-label" for="password">비밀번호</label>
              <button class="text-button" type="button" @click="showComingSoon('비밀번호 재설정')">
                비밀번호를 잊으셨나요?
              </button>
            </div>
            <div class="input-wrap">
              <svg class="input-icon lock-icon" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                <rect x="3.5" y="8.5" width="13" height="9" rx="2" />
                <path d="M6.5 8.5V6a3.5 3.5 0 0 1 7 0v2.5M10 12v2" />
              </svg>
              <input
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                name="password"
                autocomplete="current-password"
                placeholder="비밀번호를 입력해 주세요"
                required
              />
              <button
                class="visibility-button"
                type="button"
                :aria-label="showPassword ? '비밀번호 숨기기' : '비밀번호 표시'"
                :aria-pressed="showPassword"
                @click="showPassword = !showPassword"
              >
                <svg v-if="!showPassword" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                  <path d="M2.2 10s2.8-4.5 7.8-4.5 7.8 4.5 7.8 4.5-2.8 4.5-7.8 4.5S2.2 10 2.2 10Z" />
                  <circle cx="10" cy="10" r="2.1" />
                </svg>
                <svg v-else viewBox="0 0 20 20" fill="none" aria-hidden="true">
                  <path d="m3 3 14 14M8.6 5.7A8 8 0 0 1 10 5.5c5 0 7.8 4.5 7.8 4.5a13 13 0 0 1-2 2.4M5.1 5.8C3.2 7 2.2 10 2.2 10s2.8 4.5 7.8 4.5c.9 0 1.7-.2 2.4-.4" />
                </svg>
              </button>
            </div>

            <label class="remember-row">
              <input v-model="rememberMe" type="checkbox" name="remember" />
              <span class="custom-checkbox" aria-hidden="true">
                <svg viewBox="0 0 16 16" fill="none"><path d="m3.5 8 3 3 6-6" /></svg>
              </span>
              <span>로그인 상태 유지</span>
            </label>

            <button class="submit-button" type="submit">
              로그인하기
              <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
                <path d="M4 10h11M10 5l5 5-5 5" />
              </svg>
            </button>

            <p v-if="feedback" class="form-feedback" role="status" aria-live="polite">
              <span aria-hidden="true">i</span>{{ feedback }}
            </p>
          </form>

          <div class="signup-prompt">
            <span>아직 계정이 없으신가요?</span>
            <button class="signup-button" type="button" @click="showComingSoon('회원가입')">무료로 시작하기</button>
          </div>

          <div class="privacy-note">
            <svg viewBox="0 0 20 20" fill="none" aria-hidden="true">
              <path d="M10 2.5 16 5v4.2c0 3.8-2.4 6.7-6 8.3-3.6-1.6-6-4.5-6-8.3V5l6-2.5Z" />
              <path d="m7.5 9.8 1.7 1.7 3.4-3.6" />
            </svg>
            <span>집밥 절약 기록을 한곳에서 확인해요</span>
          </div>
        </div>

        <footer class="login-footer">
          <span>© 2026 한끼절약</span>
          <span class="footer-separator" aria-hidden="true">·</span>
          <button type="button" @click="showComingSoon('고객센터')">도움이 필요하신가요?</button>
        </footer>
      </section>
    </main>
  </div>
</template>
