import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { AppRouterCacheProvider } from "@mui/material-nextjs/v16-appRouter";
import { ThemeProvider } from "@mui/material/styles";
import theme from "../theme";
import "./globals.css";

const geistSans = Geist({ variable: "--font-geist-sans", subsets: ["latin"] });
const geistMono = Geist_Mono({ variable: "--font-geist-mono", subsets: ["latin"] });
export const metadata: Metadata = { title: "Tranz PGN — One PGN. One AST. Every platform.", description: "Standards-focused Portable Game Notation infrastructure for Java and every platform.", metadataBase: new URL("https://pgn.tranztechnologies.com"), openGraph: { title: "Tranz PGN", description: "One PGN. One AST. Every platform.", type: "website" } };

export default function RootLayout({ children }: LayoutProps<"/">) {
  return <html lang="en" className={`${geistSans.variable} ${geistMono.variable}`}><body><AppRouterCacheProvider><ThemeProvider theme={theme}>{children}</ThemeProvider></AppRouterCacheProvider></body></html>;
}
