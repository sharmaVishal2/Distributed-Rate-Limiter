import axios from 'axios'

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const client = axios.create({ baseURL: apiBaseUrl, timeout: 10000 })

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('rate-limiter-token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export const api = {
  login: (credentials) => client.post('/api/auth/login', credentials),
  check: (payload) => client.post('/api/check', payload),
  getRules: () => client.get('/api/rules'),
  createRule: (payload) => client.post('/api/rules', payload),
  updateRule: (id, payload) => client.put(`/api/rules/${id}`, payload),
  deleteRule: (id) => client.delete(`/api/rules/${id}`),
  metrics: () => client.get('/api/metrics'),
  health: () => client.get('/actuator/health'),
}

export const getApiError = (error) => {
  if (!error.response) return { status: 0, message: 'Backend unavailable. Please check the server.', body: null }
  return { status: error.response.status, message: error.response.data?.message || error.response.statusText || 'Request failed.', body: error.response.data }
}
