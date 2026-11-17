<script setup>
import { computed, ref } from 'vue'

const emit = defineEmits(['back'])
const name = ref('')
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const agreeToTerms = ref(false)
const notice = ref('')
const showPassword = ref(false)
const showPasswordConfirm = ref(false)

const passwordMismatch = computed(() => passwordConfirm.value && password.value !== passwordConfirm.value)

function submitSignup() {
  if (password.value !== passwordConfirm.value) {
    notice.value = '비밀번호가 서로 일치하지 않습니다.'
    return
  }
  notice.value = '회원가입 API 연결 전 미리보기입니다. 입력한 정보로 실제 계정이 생성되지는 않았어요.'
}
</script>

<template>
  <div class="page-shell signup-page">
    <header class="topbar">
      <a class="brand" href="#top" aria-label="한끼절약 홈" @click.prevent="emit('back')">
        <span class="brand-mark" aria-hidden="true"><svg viewBox="0 0 32 32" fill="none"><path d="M7 17.1c0-5 4-9.1 9-9.1s9 4.1 9 9.1c0 4.1-4 7.4-9 7.4s-9-3.3-9-7.4Z" /><path d="M16 7.4c.2-2.1 1.7-3.6 4.4-4.4-.1 2.8-1.5 4.6-4.4 5.2M10 18.5c2.1 1.6 4.1 2.3 6 2.3s3.9-.7 6-2.3" /></svg></span>
        <span class="brand-name">한끼절약</span>
      </a>
      <p class="topbar-note"><span class="status-dot" aria-hidden="true"></span>집밥으로 만드는 작은 여유</p>
    </header>

    <main class="signup-layout">
      <section class="signup-intro">
        <p class="eyebrow"><span>START SAVING</span> 오늘부터 한 끼씩</p>
        <h1>집밥의 즐거움과<br />절약의 기쁨을<br /><span>함께 모아요.</span></h1>
        <p>냉장고 속 재료로 만든 식사와 절약 기록을 한곳에 담아 보세요.</p>
        <div class="signup-benefit"><span>✓</span><div><b>우리 집 재료부터</b><small>가지고 있는 재료에 맞는 메뉴를 찾아요.</small></div></div>
        <div class="signup-benefit"><span>₩</span><div><b>한 끼 절약 기록</b><small>집밥과 외식 비용을 비교해 모아봐요.</small></div></div>
      </section>

      <section class="signup-card" aria-labelledby="signup-title">
        <div class="signup-card-heading">
          <span class="signup-chip">✦ 무료로 시작하기</span>
          <h2 id="signup-title">회원가입</h2>
          <p>간단한 정보만 입력하면 준비가 끝나요.</p>
        </div>

        <form class="signup-form" @submit.prevent="submitSignup">
          <label class="signup-field-label" for="signup-name">이름</label>
          <input id="signup-name" v-model.trim="name" class="signup-input" type="text" autocomplete="name" placeholder="이름을 입력해 주세요" required />

          <label class="signup-field-label" for="signup-email">이메일</label>
          <input id="signup-email" v-model.trim="email" class="signup-input" type="email" autocomplete="email" placeholder="name@example.com" required />

          <label class="signup-field-label" for="signup-password">비밀번호</label>
          <div class="signup-password-wrap"><input id="signup-password" v-model="password" class="signup-input" :type="showPassword ? 'text' : 'password'" autocomplete="new-password" minlength="8" placeholder="8자 이상 입력해 주세요" required /><button type="button" :aria-label="showPassword ? '비밀번호 숨기기' : '비밀번호 표시'" @click="showPassword = !showPassword">{{ showPassword ? '숨기기' : '표시' }}</button></div>

          <label class="signup-field-label" for="signup-password-confirm">비밀번호 확인</label>
          <div class="signup-password-wrap"><input id="signup-password-confirm" v-model="passwordConfirm" class="signup-input" :type="showPasswordConfirm ? 'text' : 'password'" autocomplete="new-password" minlength="8" placeholder="비밀번호를 한 번 더 입력해 주세요" required :aria-invalid="Boolean(passwordMismatch)" /><button type="button" :aria-label="showPasswordConfirm ? '비밀번호 숨기기' : '비밀번호 표시'" @click="showPasswordConfirm = !showPasswordConfirm">{{ showPasswordConfirm ? '숨기기' : '표시' }}</button></div>
          <p v-if="passwordMismatch" class="signup-inline-error">비밀번호가 서로 일치하지 않습니다.</p>

          <label class="signup-agreement"><input v-model="agreeToTerms" type="checkbox" required /><span>서비스 이용약관과 개인정보 처리방침에 동의합니다.</span></label>
          <button class="signup-submit" type="submit">회원가입하기 <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M4 10h11m-5-5 5 5-5 5" /></svg></button>
          <p v-if="notice" class="signup-notice" role="status" aria-live="polite">{{ notice }}</p>
        </form>

        <div class="signup-login-prompt"><span>이미 계정이 있으신가요?</span><button type="button" @click="emit('back')">로그인</button></div>
      </section>
    </main>
  </div>
</template>
