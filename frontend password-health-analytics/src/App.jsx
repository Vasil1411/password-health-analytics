import React, { useState } from 'react';
import { Navbar } from './components/Navbar';
import { PasswordChecker } from './components/PasswordChecker';
import { Dashboard } from './components/Dashboard';

export default function App() {
  const [activeTab, setActiveTab] = useState('checker');

  return (
    <div className="app-wrapper">
      <Navbar activeTab={activeTab} setActiveTab={setActiveTab} />
      <main className="main-content">
        {activeTab === 'checker' ? <PasswordChecker /> : <Dashboard />}
      </main>
    </div>
  );
}