"use client";
import { createTheme } from "@mui/material/styles";
const theme = createTheme({ cssVariables: true, palette: { mode: "light", primary: { main: "#173d2f" }, secondary: { main: "#d9ff57" }, background: { default: "#f7f5ef", paper: "#fff" } }, typography: { fontFamily: "var(--font-geist-sans), Arial, sans-serif" }, shape: { borderRadius: 12 } });
export default theme;
