import React from 'react';

export function StatCard({ icon: Icon, label, value }) {
  return (
    <div className="stat-card">
      <div className="stat-label">
        <Icon size={16} />
        {label}
      </div>
      <div className="stat-value">{value}</div>
    </div>
  );
}