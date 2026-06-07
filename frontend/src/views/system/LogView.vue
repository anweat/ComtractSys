<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, RefreshCcw, Download } from 'lucide-vue-next'
import { api } from '../../api'

const activeTab = ref('contract')
const logs = ref([])
const operations = ref([])
const keyword = ref('')
const error = ref('')
const page = ref(1)
const total = ref(0)
const pageSize = 10

async function loadLogs() {
  error.value = ''
  try {
    const params = { page: page.value, size: pageSize }
    if (keyword.value) params.keyword = keyword.value
    const res = await api.get('/logs', { params })
    logs.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    error.value = err.message
  }
}

async function loadOperations() {
  error.value = ''
  try {
    const params = { page: page.value, size: pageSize }
    if (keyword.value) params.keyword = keyword.value
    const res = await api.get('/operations', { params })
    operations.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    error.value = err.message
  }
}

function search() {
  page.value = 1
  if (activeTab.value === 'contract') loadLogs()
  else loadOperations()
}

async function exportLogs() {
  try {
    const params = {}
    if (keyword.value) params.keyword = keyword.value
    const res = await api.get('/logs/export', { params, responseType: 'blob' })
    const url = URL.createObjectURL(res.data)
    const a = document.createElement('a')
    a.href = url
    a.download = `logs_${new Date().toISOString().slice(0,10)}.csv`
    a.click()
    URL.revokeObjectURL(url)
  } catch (err) {
    error.value = err.message
  }
}

function goPage(p) {
  page.value = p
  if (activeTab.value === 'contract') loadLogs()
  else loadOperations()
}

function switchTab(tab) {
  activeTab.value = tab
  page.value = 1
  if (tab === 'contract') loadLogs()
  else loadOperations()
}

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

function statusLabel(s) {
  const map = { DRAFT:'待分配', ASSIGNED:'待会签', COUNTERSIGNED:'会签完成', FINALIZED:'已定稿', APPROVED:'已审批', SIGNED:'已签订', REJECTED:'已拒绝', CANCELLED:'已取消' }
  return map[s] || s
}

onMounted(loadLogs)
</script>

<template>
  <div>
    <div class="section-title">
      <h2>操作日志</h2>
      <div class="actions">
        <div class="input" style="max-width:220px">
          <Search :size="16" />
          <input v-model="keyword" placeholder="搜索关键字" @keyup.enter="search" />
        </div>
        <button class="secondary" @click="activeTab === 'contract' ? loadLogs() : loadOperations()"><RefreshCcw :size="16" /></button>
        <button v-if="activeTab === 'contract'" class="primary" @click="exportLogs"><Download :size="16" /> 导出CSV</button>
      </div>
    </div>

    <div class="tabs" style="margin-bottom:14px">
      <button :class="{ selected: activeTab === 'contract' }" @click="switchTab('contract')">合同日志</button>
      <button :class="{ selected: activeTab === 'operation' }" @click="switchTab('operation')">系统操作日志</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <!-- 合同日志 -->
    <div v-if="activeTab === 'contract'" class="panel">
      <table>
        <thead>
          <tr>
            <th>时间</th>
            <th>合同</th>
            <th>操作人</th>
            <th>状态变更</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="h in logs" :key="h.id">
            <td>{{ h.createdAt?.slice(0, 16) }}</td>
            <td>{{ h.contract?.name || '-' }}</td>
            <td>{{ h.operator?.displayName || '-' }}</td>
            <td>
              <span v-if="h.fromStatus" class="status" style="margin-right:4px">{{ statusLabel(h.fromStatus) }}</span>
              <span v-if="h.fromStatus">→</span>
              <span class="status" style="margin-left:4px">{{ statusLabel(h.toStatus) }}</span>
            </td>
            <td>{{ h.remark || '-' }}</td>
          </tr>
          <tr v-if="logs.length === 0">
            <td colspan="5" class="muted" style="text-align:center;padding:24px">暂无操作记录</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 系统操作日志 -->
    <div v-if="activeTab === 'operation'" class="panel">
      <table>
        <thead>
          <tr>
            <th>时间</th>
            <th>操作人</th>
            <th>操作</th>
            <th>详情</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in operations" :key="o.id">
            <td>{{ o.createdAt?.slice(0, 16) }}</td>
            <td>{{ o.operatorName }}</td>
            <td><span class="status" style="font-size:12px">{{ o.operation }}</span></td>
            <td>{{ o.detail || '-' }}</td>
          </tr>
          <tr v-if="operations.length === 0">
            <td colspan="4" class="muted" style="text-align:center;padding:24px">暂无操作记录</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <button :disabled="page === 1" @click="goPage(page - 1)">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="goPage(page + 1)">下一页</button>
    </div>
  </div>
</template>
