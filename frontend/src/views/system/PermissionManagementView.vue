<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Pencil, Trash2, Search } from 'lucide-vue-next'
import { api } from '../../api'

const permissions = ref([])
const error = ref('')
const showForm = ref(false)
const editingId = ref(null)
const searchKeyword = ref('')

const form = reactive({ permissionCode: '', permissionName: '', module: '', url: '', description: '' })

const modules = ['CONTRACT', 'CUSTOMER', 'SYSTEM', 'LOG']

async function loadPermissions() {
  error.value = ''
  try {
    const res = await api.get('/permissions')
    permissions.value = res.data
  } catch (err) {
    error.value = err.message
  }
}

const filteredPermissions = computed(() => {
  if (!searchKeyword.value) return permissions.value
  const kw = searchKeyword.value.toLowerCase()
  return permissions.value.filter(p =>
    p.permissionCode.toLowerCase().includes(kw) ||
    p.permissionName.toLowerCase().includes(kw) ||
    p.module.toLowerCase().includes(kw)
  )
})

function openCreate() {
  editingId.value = null
  Object.keys(form).forEach(k => form[k] = '')
  showForm.value = true
}

function openEdit(p) {
  editingId.value = p.id
  form.permissionCode = p.permissionCode
  form.permissionName = p.permissionName
  form.module = p.module
  form.url = p.url || ''
  form.description = p.description || ''
  showForm.value = true
}

async function save() {
  if (!form.permissionCode || !form.permissionName || !form.module) {
    error.value = '权限编码、名称和模块不能为空'
    return
  }
  try {
    if (editingId.value) {
      await api.put(`/permissions/${editingId.value}`, form)
    } else {
      await api.post('/permissions', form)
    }
    showForm.value = false
    loadPermissions()
  } catch (err) {
    error.value = err.message
  }
}

async function remove(id) {
  if (!confirm('确认删除该权限？')) return
  try {
    await api.delete(`/permissions/${id}`)
    loadPermissions()
  } catch (err) {
    error.value = err.message
  }
}

onMounted(loadPermissions)
</script>

<template>
  <div>
    <div class="section-title">
      <h2>权限管理</h2>
      <div class="actions">
        <div class="input" style="max-width:220px">
          <Search :size="16" />
          <input v-model="searchKeyword" placeholder="搜索权限编码/名称" />
        </div>
        <button class="primary" @click="openCreate"><Plus :size="16" /> 新增权限</button>
      </div>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="showForm" class="panel narrow" style="margin-bottom:18px">
      <h2>{{ editingId ? '编辑权限' : '新增权限' }}</h2>
      <div class="form-grid single" style="margin-top:14px">
        <label>权限编码 *<input v-model="form.permissionCode" :disabled="!!editingId" placeholder="如 contract:create" /></label>
        <label>权限名称 *<input v-model="form.permissionName" placeholder="如 起草合同" /></label>
        <label>模块 *
          <select v-model="form.module">
            <option v-for="m in modules" :key="m" :value="m">{{ m }}</option>
          </select>
        </label>
        <label>URL<input v-model="form.url" placeholder="可选" /></label>
        <label>描述<input v-model="form.description" placeholder="可选" /></label>
      </div>
      <div class="row-actions" style="margin-top:14px">
        <button class="primary" @click="save">保存</button>
        <button class="secondary" @click="showForm = false">取消</button>
      </div>
    </div>

    <div class="panel">
      <table>
        <thead><tr><th>权限编码</th><th>名称</th><th>模块</th><th>描述</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="p in filteredPermissions" :key="p.id">
            <td><code>{{ p.permissionCode }}</code></td>
            <td>{{ p.permissionName }}</td>
            <td><span class="status" style="font-size:12px">{{ p.module }}</span></td>
            <td class="muted">{{ p.description || '-' }}</td>
            <td class="row-actions">
              <button @click="openEdit(p)"><Pencil :size="14" /> 编辑</button>
              <button @click="remove(p.id)"><Trash2 :size="14" /> 删除</button>
            </td>
          </tr>
          <tr v-if="filteredPermissions.length === 0"><td colspan="5" class="muted" style="text-align:center">暂无权限条目</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
