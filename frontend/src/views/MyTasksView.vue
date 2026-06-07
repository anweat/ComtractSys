<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { RefreshCcw, CheckCircle, XCircle, FileEdit, PenLine, UserCheck, FileText } from 'lucide-vue-next'
import { api } from '../api'

const router = useRouter()
const tasks = ref([])
const error = ref('')

function taskLabel(t) {
  const map = { COUNTERSIGN:'会签', APPROVAL:'审批', SIGN:'签订', ASSIGN:'分配人员', FINALIZE:'定稿' }
  return map[t] || t
}

const groupedTasks = computed(() => {
  const map = {}
  for (const t of tasks.value) {
    if (!map[t.contractId]) map[t.contractId] = { contractName: t.contractName, contractId: t.contractId, tasks: [] }
    map[t.contractId].tasks.push(t)
  }
  return Object.values(map)
})

async function loadTasks() {
  error.value = ''
  try {
    const res = await api.get('/tasks/my')
    tasks.value = res.data || []
  } catch (err) {
    error.value = err.message
  }
}

async function handleCountersign(task) {
  const opinion = prompt('会签意见:')
  if (!opinion) return
  try {
    await api.post(`/contracts/${task.contractId}/countersign`, { opinion })
    loadTasks()
  } catch (err) {
    error.value = err.message
  }
}

async function handleApprove(task, result) {
  const opinion = prompt(result === 'APPROVED' ? '审批通过意见:' : '拒绝原因:')
  if (!opinion) return
  try {
    await api.post(`/contracts/${task.contractId}/approve`, { result, opinion })
    loadTasks()
  } catch (err) {
    error.value = err.message
  }
}

async function handleSign(task) {
  const signInfo = prompt('签订信息:')
  if (!signInfo) return
  try {
    await api.post(`/contracts/${task.contractId}/sign`, { signInfo, signedDate: new Date().toISOString().slice(0,10) })
    loadTasks()
  } catch (err) {
    error.value = err.message
  }
}

function handleAssign(task) {
  router.push(`/contracts/${task.contractId}`)
}

function handleFinalize(task) {
  router.push(`/contracts/${task.contractId}`)
}

onMounted(loadTasks)
</script>

<template>
  <div>
    <div class="section-title">
      <h2>我的待办</h2>
      <button class="secondary" @click="loadTasks"><RefreshCcw :size="16" /> 刷新</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="groupedTasks.length === 0" class="panel">
      <p class="muted" style="text-align:center;padding:32px">暂无待办任务</p>
    </div>

    <div v-else style="display:grid;gap:14px">
      <div v-for="group in groupedTasks" :key="group.contractId" class="panel">
        <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:12px;padding-bottom:10px;border-bottom:1px solid #edf1f5">
          <a @click="router.push(`/contracts/${group.contractId}`)" style="cursor:pointer;color:#126f67;font-weight:700;font-size:16px;text-decoration:none">
            {{ group.contractName }}
          </a>
          <span class="status" style="font-size:12px">{{ group.tasks.length }} 项待办</span>
        </div>
        <table>
          <thead><tr><th>任务类型</th><th>业务提示</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="t in group.tasks" :key="t.id" :style="t.taskType === 'ASSIGN' || t.taskType === 'FINALIZE' ? '' : ''">
              <td>
                <span class="status" style="font-size:12px;font-weight:700" :style="t.taskType === 'ASSIGN' || t.taskType === 'FINALIZE' ? 'background:#fff3e0;color:#b85c00' : ''">{{ taskLabel(t.taskType) }}</span>
              </td>
              <td class="muted" style="font-size:13px">{{ t.opinion || '-' }}</td>
              <td><span class="status" style="font-size:12px">待处理</span></td>
              <td class="row-actions">
                <button v-if="t.taskType === 'COUNTERSIGN'" @click="handleCountersign(t)">
                  <FileEdit :size="14" /> 会签
                </button>
                <button v-if="t.taskType === 'APPROVAL'" @click="handleApprove(t, 'APPROVED')">
                  <CheckCircle :size="14" /> 通过
                </button>
                <button v-if="t.taskType === 'APPROVAL'" @click="handleApprove(t, 'REJECTED')">
                  <XCircle :size="14" /> 拒绝
                </button>
                <button v-if="t.taskType === 'SIGN'" @click="handleSign(t)">
                  <PenLine :size="14" /> 签订
                </button>
                <button v-if="t.taskType === 'ASSIGN'" @click="handleAssign(t)">
                  <UserCheck :size="14" /> 去分配
                </button>
                <button v-if="t.taskType === 'FINALIZE'" @click="handleFinalize(t)">
                  <FileText :size="14" /> 去定稿
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
