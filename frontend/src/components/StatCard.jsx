import { Activity } from 'lucide-react'

export default function StatCard({ label, value, hint, icon: Icon = Activity, tone = 'cyan', loading }) {
  const colors = { cyan: 'text-cyan-300 bg-cyan-400/10', green: 'text-emerald-300 bg-emerald-400/10', red: 'text-rose-300 bg-rose-400/10', blue: 'text-blue-300 bg-blue-400/10' }
  return <div className="panel p-5"><div className="flex items-start justify-between gap-3"><div><p className="text-sm text-slate-400">{label}</p>{loading ? <div className="mt-3 h-8 w-24 animate-pulse rounded bg-slate-800" /> : <p className="mt-1 text-3xl font-bold tracking-tight text-white">{value}</p>}</div><span className={`rounded-xl p-2.5 ${colors[tone]}`}><Icon size={20}/></span></div><p className="mt-3 text-xs text-slate-500">{hint}</p></div>
}
