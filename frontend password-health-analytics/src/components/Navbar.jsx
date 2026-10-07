import React from 'react';
import { ShieldCheck, LayoutDashboard, KeyRound } from 'lucide-react';

export function Navbar({ activeTab, setActiveTab }) {
  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <ShieldCheck size={24} className="brand-icon" />
        <span>Password Analytics</span>
      </div>
      <div className="navbar-links">
        <button
          className={`nav-btn ${activeTab === 'checker' ? 'active' : ''}`}
          onClick={() => setActiveTab('checker')}
        >
          <KeyRound size={18} />
          <span>Проверка</span>
        </button>
        <button
          className={`nav-btn ${activeTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setActiveTab('dashboard')}
        >
          <LayoutDashboard size={18} />
          <span>Дашборд</span>
        </button>
      </div>
    </nav>
  );
}