import React, { useState, useEffect } from 'react';
import { 
  PieChart, 
  Calculator, 
  AlertTriangle, 
  Zap, 
  Layers, 
  ListChecks 
} from 'lucide-react';
import { fetchDashboardSummary } from '../services/api';

export function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadStats = async () => {
      try {
        setLoading(true);
        const data = await fetchDashboardSummary();
        setStats(data);
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadStats();
  }, []);

  if (loading) {
    return <div className="dashboard-container"><p style={{ textAlign: 'center' }}>Зареждане на данни...</p></div>;
  }

  if (error) {
    return <div className="dashboard-container"><p style={{ textAlign: 'center', color: 'var(--status-critical)' }}>Грешка: {error}</p></div>;
  }

  const total = stats?.totalAnalyzed || 1;
  const criticalPct = Math.round(((stats?.criticalBreachCount || 0) / total) * 100);
  const weakPct = Math.round(((stats?.weakCount || 0) / total) * 100);
  const moderatePct = Math.round(((stats?.moderateCount || 0) / total) * 100);
  const strongPct = Math.round(((stats?.strongCount || 0) / total) * 100);

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div className="dashboard-title">
          <h1>
            <PieChart size={28} style={{ color: 'var(--primary)' }} />
            Password Analytics Dashboard
          </h1>
          <p>Обобщена статистика на анализираните пароли в базата данни</p>
        </div>
      </div>

      <div className="stats-grid">
        <div className="kpi-card">
          <div className="kpi-info">
            <div className="kpi-label">Общо анализирани</div>
            <div className="kpi-value">{stats?.totalAnalyzed ?? 0}</div>
          </div>
          <div className="kpi-icon icon-total">
            <Calculator size={24} />
          </div>
        </div>

        <div className="kpi-card">
          <div className="kpi-info">
            <div className="kpi-label">Компрометирани (HIBP)</div>
            <div className="kpi-value">{stats?.totalBreached ?? 0}</div>
          </div>
          <div className="kpi-icon icon-breach">
            <AlertTriangle size={24} />
          </div>
        </div>

        <div className="kpi-card">
          <div className="kpi-info">
            <div className="kpi-label">Средна Ентропия</div>
            <div className="kpi-value">{stats?.averageEntropy ?? 0} bit</div>
          </div>
          <div className="kpi-icon icon-entropy">
            <Zap size={24} />
          </div>
        </div>
      </div>

      <div className="breakdown-card">
        <div className="section-title">
          <Layers size={20} />
          Разпределение по категории
        </div>

        <div className="progress-multi">
          <div className="progress-segment segment-critical" style={{ width: `${criticalPct}%` }} title={`Critical Breach: ${criticalPct}%`} />
          <div className="progress-segment segment-weak" style={{ width: `${weakPct}%` }} title={`Weak: ${weakPct}%`} />
          <div className="progress-segment segment-moderate" style={{ width: `${moderatePct}%` }} title={`Moderate: ${moderatePct}%`} />
          <div className="progress-segment segment-strong" style={{ width: `${strongPct}%` }} title={`Strong: ${strongPct}%`} />
        </div>

        <div className="legend-grid">
          <div className="legend-item">
            <span className="legend-dot segment-critical"></span>
            <span>Critical Breach: <strong>{stats?.criticalBreachCount ?? 0}</strong></span>
          </div>
          <div className="legend-item">
            <span className="legend-dot segment-weak"></span>
            <span>Weak: <strong>{stats?.weakCount ?? 0}</strong></span>
          </div>
          <div className="legend-item">
            <span className="legend-dot segment-moderate"></span>
            <span>Moderate: <strong>{stats?.moderateCount ?? 0}</strong></span>
          </div>
          <div className="legend-item">
            <span className="legend-dot segment-strong"></span>
            <span>Strong: <strong>{stats?.strongCount ?? 0}</strong></span>
          </div>
        </div>
      </div>
    </div>
  );
}