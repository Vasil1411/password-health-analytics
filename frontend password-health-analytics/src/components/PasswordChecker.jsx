import React, { useState } from 'react';
import { 
  Shield, 
  Eye, 
  EyeOff, 
  Gauge, 
  Clock, 
  UserX, 
  Boxes, 
  AlertTriangle, 
  Search,
  AlertCircle,
  CheckCircle2,
  Info
} from 'lucide-react';
import { analyzePassword } from '../services/api';
import { StatCard } from './StatCard';
import { StrengthMeter } from './StrengthMeter';

export function PasswordChecker() {
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');

  // Динамични проверки за облака под входното поле
  const checks = {
    length: password.length >= 8 && password.length <= 128,
    upper: /[A-Z]/.test(password),
    lower: /[a-z]/.test(password),
    digit: /\d/.test(password),
    special: /[@$!%*?&]/.test(password),
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!password) {
      setError('Моля, въведете парола за анализ.');
      return;
    }

    setLoading(true);
    try {
  const data = await analyzePassword(password);
  setResult(data);
} catch (err) {
  console.error(err);
  if (err.message?.includes('400')) {
    setError('Моля, уверете се, че паролата изпълнява всички посочени изисквания.');
  } else {
    setError('Възникна проблем с връзката към сървъра. Опитайте отново по-късно.');
  }
}
  };

  return (
    <div className="container">
      <div className="header">
        <div className="header-icon">
          <Shield size={28} />
        </div>
        <h1>Password Health Analytics</h1>
        <p>Анализ на сложността, шаблони и проверка за течове</p>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <div className="input-wrapper">
            <input
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => {
                setPassword(e.target.value);
                if (error) setError('');
              }}
              placeholder="Въведете парола..."
              autoComplete="off"
            />
            <button
              type="button"
              className="toggle-btn"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
        </div>

        {/* 1. Информационен облак за изисквания (показва се докато се пише) */}
        {password.length > 0 && (
          <div className="validation-hint-box">
            <div className="validation-hint-title">
              <Info size={16} /> Изисквания на бекенда:
            </div>
            <ul className="validation-list">
              <li className={`validation-item ${checks.length ? 'valid' : 'invalid'}`}>
                {checks.length ? <CheckCircle2 size={14} /> : <AlertCircle size={14} />}
                Поне 8 символа (до 128)
              </li>
              <li className={`validation-item ${checks.upper ? 'valid' : 'invalid'}`}>
                {checks.upper ? <CheckCircle2 size={14} /> : <AlertCircle size={14} />}
                Поне една главна буква (A-Z)
              </li>
              <li className={`validation-item ${checks.lower ? 'valid' : 'invalid'}`}>
                {checks.lower ? <CheckCircle2 size={14} /> : <AlertCircle size={14} />}
                Поне една малка буква (a-z)
              </li>
              <li className={`validation-item ${checks.digit ? 'valid' : 'invalid'}`}>
                {checks.digit ? <CheckCircle2 size={14} /> : <AlertCircle size={14} />}
                Поне една цифра (0-9)
              </li>
              <li className={`validation-item ${checks.special ? 'valid' : 'invalid'}`}>
                {checks.special ? <CheckCircle2 size={14} /> : <AlertCircle size={14} />}
                Поне един специален символ (@$!%*?&)
              </li>
            </ul>
          </div>
        )}

        {/* 2. Банер за грешки при изпращане */}
        {error && (
          <div className="error-banner">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <StrengthMeter
          category={result?.healthCategory}
          entropy={result?.entropyScore}
        />

        <div className="results-grid">
          <StatCard
            icon={Gauge}
            label="Сложност (ентропия)"
            value={result ? `${result.entropyScore} bit` : '0.0 bit'}
          />
          <StatCard
            icon={Clock}
            label="Време за разбиване"
            value={result ? result.estimatedCrackTime : '-'}
          />
          <StatCard
            icon={UserX}
            label="Изтичала е:"
            value={result ? `${result.breachCount} пъти` : '0 пъти'}
          />
          <StatCard
            icon={Boxes}
            label="Дължина"
            value={`${password.length} символа`}
          />
        </div>

        {result?.warnings && result.warnings.length > 0 && (
          <div className="warnings-box">
            <div className="warnings-title">
              <AlertTriangle size={16} />
              Предупреждения:
            </div>
            <ul className="warnings-list">
              {result.warnings.map((warning, index) => (
                <li key={index}>{warning}</li>
              ))}
            </ul>
          </div>
        )}

        <button type="submit" className="btn-analyze" disabled={loading}>
          <Search size={18} />
          {loading ? 'Анализиране...' : 'Анализирай'}
        </button>
      </form>
    </div>
  );
}