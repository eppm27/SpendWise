import React, { useState, useEffect } from 'react';

import { FontContext } from './useFont';

export function FontProvider({ children }) {
  // 从localStorage中检查是否启用了无障碍字体
  const [dyslexiaFont, setDyslexiaFont] = useState(() => {
    const saved = localStorage.getItem('dyslexiaFont');
    return saved ? JSON.parse(saved) : false;
  });

  // 当设置变更时，保存到localStorage
  useEffect(() => {
    localStorage.setItem('dyslexiaFont', JSON.stringify(dyslexiaFont));
    // 应用字体到document.body
    if (dyslexiaFont) {
      document.body.classList.add('dyslexia-friendly');
    } else {
      document.body.classList.remove('dyslexia-friendly');
    }
  }, [dyslexiaFont]);

  return (
    <FontContext.Provider value={{ dyslexiaFont, setDyslexiaFont }}>
      {children}
    </FontContext.Provider>
  );
}

