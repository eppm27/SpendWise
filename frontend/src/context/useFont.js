import { createContext, useContext } from 'react';
export const FontContext = createContext();

export function useFont() {
  return useContext(FontContext);
}