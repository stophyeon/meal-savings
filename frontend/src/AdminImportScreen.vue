<script setup>
import { computed, ref } from 'vue'

const emit = defineEmits(['back'])
const selectedFile = ref(null)
const validationMessage = ref('CSV 또는 Excel 가격 파일을 선택해 주세요.')
const validationState = ref('idle')
const allowedExtensions = ['csv', 'xls', 'xlsx']
const maxFileBytes = 10 * 1024 * 1024

const fileSizeLabel = computed(() => {
  if (!selectedFile.value) return ''
  return selectedFile.value.size < 1024 * 1024
    ? `${Math.max(1, Math.round(selectedFile.value.size / 1024))}KB`
    : `${(selectedFile.value.size / 1024 / 1024).toFixed(1)}MB`
})

function onFileChange(event) {
  selectedFile.value = event.target.files?.[0] || null
  showSelectedFileMessage()
}

function showSelectedFileMessage() {
  validationState.value = 'idle'
  validationMessage.value = selectedFile.value ? '파일을 선택했습니다. 형식 검증을 진행해 주세요.' : 'CSV 또는 Excel 가격 파일을 선택해 주세요.'
}

function onFileDrop(event) {
  selectedFile.value = event.dataTransfer?.files?.[0] || null
  showSelectedFileMessage()
}

function validateFile() {
  const file = selectedFile.value
  if (!file) {
    validationState.value = 'error'
    validationMessage.value = '먼저 파일을 선택해 주세요.'
    return
  }

  const extension = file.name.split('.').pop()?.toLowerCase()
  if (!allowedExtensions.includes(extension)) {
    validationState.value = 'error'
    validationMessage.value = 'CSV, XLS, XLSX 파일만 선택할 수 있어요.'
    return
  }
  if (file.size > maxFileBytes) {
    validationState.value = 'error'
    validationMessage.value = '파일 크기가 10MB를 초과합니다.'
    return
  }
  if (file.size === 0) {
    validationState.value = 'error'
    validationMessage.value = '빈 파일은 검증할 수 없습니다.'
    return
  }

  validationState.value = 'success'
  validationMessage.value = '확장자와 파일 크기를 확인했습니다. 실제 데이터 행 검증 및 DB 적재 API는 연결 전입니다.'
}

function clearFile() {
  selectedFile.value = null
  validationState.value = 'idle'
  validationMessage.value = 'CSV 또는 Excel 가격 파일을 선택해 주세요.'
}
</script>

<template>
  <div class="flow-screen admin-screen">
    <header class="flow-topbar">
      <div class="flow-topbar-inner">
        <button class="flow-back" type="button" @click="emit('back')"><svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M16 10H4m5 5-5-5 5-5" /></svg> 서비스 화면으로</button>
        <a class="brand" href="#main-top" aria-label="한끼절약"><span class="brand-mark" aria-hidden="true"><svg viewBox="0 0 32 32" fill="none"><path d="M7 17.1c0-5 4-9.1 9-9.1s9 4.1 9 9.1c0 4.1-4 7.4-9 7.4s-9-3.3-9-7.4Z" /><path d="M16 7.4c.2-2.1 1.7-3.6 4.4-4.4-.1 2.8-1.5 4.6-4.4 5.2M10 18.5c2.1 1.6 4.1 2.3 6 2.3s3.9-.7 6-2.3" /></svg></span><span class="brand-name">한끼절약</span></a>
        <span class="flow-demo-badge"><i></i> 운영자 미리보기</span>
      </div>
    </header>

    <main class="flow-layout admin-layout">
      <div class="flow-heading admin-heading">
        <div><p class="flow-eyebrow">PRICE DATA · ADMIN</p><h1>참가격 파일을<br class="mobile-break" /> 확인하고 적재해요<span>.</span></h1><p>가격 기준일과 메뉴·재료 가격을 관리하는 운영자 화면 구성이에요.</p></div>
        <span class="admin-heading-icon" aria-hidden="true">⇧</span>
      </div>

      <div class="admin-grid">
        <section class="admin-upload-card">
          <div class="admin-card-title"><span class="admin-step-number">01</span><div><h2>가격 파일 선택</h2><p>참가격 수집 배치에서 만든 CSV 또는 Excel 파일</p></div></div>
          <label class="admin-dropzone" :class="{ 'admin-dropzone-selected': selectedFile }" @dragover.prevent @drop.prevent="onFileDrop">
            <input type="file" accept=".csv,.xls,.xlsx,text/csv,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" @click="$event.target.value = ''" @change="onFileChange" />
            <span class="upload-icon" aria-hidden="true">↑</span>
            <strong>{{ selectedFile ? selectedFile.name : '파일을 선택하거나 여기에 놓으세요' }}</strong>
            <small>{{ selectedFile ? `${fileSizeLabel} · 최대 10MB` : 'CSV, XLS, XLSX · 최대 10MB' }}</small>
          </label>

          <div v-if="selectedFile" class="selected-file-row"><span class="file-type-badge">{{ selectedFile.name.split('.').pop().toUpperCase() }}</span><span>{{ selectedFile.name }}</span><button type="button" aria-label="선택 파일 제거" @click="clearFile">×</button></div>

          <div class="admin-validation" :class="`validation-${validationState}`" role="status" aria-live="polite"><span aria-hidden="true">{{ validationState === 'success' ? '✓' : validationState === 'error' ? '!' : 'i' }}</span>{{ validationMessage }}</div>
          <button class="flow-primary-button admin-validate-button" type="button" @click="validateFile">파일 형식 검증하기 <svg viewBox="0 0 20 20" fill="none" aria-hidden="true"><path d="M4 10h11m-5-5 5 5-5 5" /></svg></button>
          <p class="admin-security-note">미리보기에서는 파일 내용을 읽거나 서버로 전송하지 않아요. 업로드와 DB 반영은 API 연결 후 제공됩니다.</p>
        </section>

        <aside class="admin-schema-card">
          <div class="admin-card-title"><span class="admin-step-number muted-step">02</span><div><h2>적재 기준</h2><p>실제 연동 시 검증할 데이터 항목</p></div></div>
          <div class="schema-list">
            <div><span>가격 기준일</span><b>기준일자</b></div>
            <div><span>메뉴 식별자</span><b>메뉴명 · 분류</b></div>
            <div><span>재료 가격</span><b>재료명 · 단위 · 가격</b></div>
            <div><span>출처</span><b>참가격 · 수집시각</b></div>
          </div>
          <div class="admin-pipeline"><span>수집</span><i>→</i><span>형식 검증</span><i>→</i><span>이력 저장</span></div>
          <p class="admin-data-note">현재는 샘플 화면입니다. 실제 참가격 원본 파일의 컬럼을 확인한 뒤 적재 스키마를 확정해야 합니다.</p>
        </aside>
      </div>

      <section class="admin-history-card">
        <div class="admin-history-heading"><div><h2>최근 적재 이력</h2><p>운영 화면 구성 예시 · 실제 기록 아님</p></div><span class="sample-history-badge">예시 데이터</span></div>
        <div class="admin-history-table" role="table" aria-label="가격 파일 적재 이력 예시">
          <div class="admin-history-row admin-table-header" role="row"><span>파일명</span><span>가격 기준일</span><span>행 수</span><span>상태</span></div>
          <div class="admin-history-row" role="row"><span>참가격_외식가격_2026-11.csv</span><span>2026.11.01</span><span>245</span><b class="import-status-complete">완료</b></div>
          <div class="admin-history-row" role="row"><span>참가격_재료가격_2026-10.xlsx</span><span>2026.10.01</span><span>1,082</span><b class="import-status-complete">완료</b></div>
        </div>
      </section>
    </main>
  </div>
</template>
