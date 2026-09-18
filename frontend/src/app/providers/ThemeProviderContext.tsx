import { createContext } from "react";
import type { ThemeProviderState } from "./ThemeProvider";

export const ThemeProviderContext = createContext<ThemeProviderState>({
  theme: "system",
  setTheme: () => null,
});
