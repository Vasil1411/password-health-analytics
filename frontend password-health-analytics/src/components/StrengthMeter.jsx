import React from 'react';
import { getMeterStyle } from '../utils/strengthMeterStyles';

export function StrengthMeter({ category}) {
  const style = getMeterStyle(category);

  return (
    <div className="meter-container">
      <div className="meter-bar-bg">
        <div
          className="meter-bar-fill"
          style={{ width: style.width, backgroundColor: style.color }}
        />
      </div>
      <div className="meter-text">
        <span>СТАТУС:</span>
        <span className="category-badge" style={{ color: style.color }}>
          {style.label}
        </span>
      </div>
    </div>
  );
}