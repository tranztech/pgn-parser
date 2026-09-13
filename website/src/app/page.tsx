"use client";

import { Box, Button, Chip, Container, Divider, Stack, Typography } from "@mui/material";
import ArrowForwardRounded from "@mui/icons-material/ArrowForwardRounded";
import BoltRounded from "@mui/icons-material/BoltRounded";
import CheckCircleRounded from "@mui/icons-material/CheckCircleRounded";
import CodeRounded from "@mui/icons-material/CodeRounded";
import DataObjectRounded from "@mui/icons-material/DataObjectRounded";
import GitHub from "@mui/icons-material/GitHub";
import HubRounded from "@mui/icons-material/HubRounded";
import SecurityRounded from "@mui/icons-material/SecurityRounded";
import TerminalRounded from "@mui/icons-material/TerminalRounded";
import styles from "./page.module.css";

const install = `<dependency>
  <groupId>com.tranztechnologies</groupId>
  <artifactId>pgn-parser</artifactId>
  <version>1.0.0</version>
</dependency>`;
const parse = `PgnDocument document = Pgn.parse(source,
    PgnOptions.builder()
        .mode(PgnMode.STRICT)
        .build());

document.games().getFirst().moves()
    .forEach(move -> System.out.println(move.san()));`;
const features = [
  { icon: <DataObjectRounded />, title: "Canonical AST", text: "A stable model for tags, moves, comments, NAGs, variations, and diagnostics." },
  { icon: <SecurityRounded />, title: "Defensive by default", text: "Strict and tolerant modes, structured diagnostics, input limits, and bounded variation depth." },
  { icon: <BoltRounded />, title: "Built for real PGN", text: "Multiple games, Unicode, BOMs, brace and semicolon comments, symbolic NAGs, and nested RAVs." },
  { icon: <HubRounded />, title: "One conformance suite", text: "Shared fixtures keep every implementation aligned around semantics—not internals." },
];

function BrandMark() { return <Box className={styles.brandMark} aria-hidden="true"><span>♞</span></Box>; }

export default function Home() {
  return (
    <Box component="main" className={styles.page}>
      <Container maxWidth="lg">
        <Box component="nav" className={styles.nav}>
          <Stack direction="row" sx={{ alignItems: "center" }} spacing={1.4}><BrandMark /><Typography className={styles.brand}>Tranz PGN</Typography></Stack>
          <Stack direction="row" sx={{ alignItems: "center" }} spacing={{ xs: 0.5, sm: 2 }}>
            <Button className={styles.navLink} href="#features">Features</Button>
            <Button className={styles.navLink} href="#quickstart">Quick start</Button>
            <Button className={styles.githubButton} href="https://github.com/tranztech/pgn-parser" target="_blank" rel="noreferrer" startIcon={<GitHub />}>GitHub</Button>
          </Stack>
        </Box>

        <Box className={styles.hero}>
          <Box className={styles.heroCopy}>
            <Chip className={styles.releaseChip} icon={<CheckCircleRounded />} label="Java 1.0 available" />
            <Typography component="h1" className={styles.title}>One PGN.<br />One <Box component="span">AST.</Box><br />Every platform.</Typography>
            <Typography className={styles.lede}>Standards-focused PGN infrastructure for chess developers. Parse complex game notation into clean Java objects with deterministic output and actionable diagnostics.</Typography>
            <Stack direction={{ xs: "column", sm: "row" }} spacing={1.5}>
              <Button className={styles.primaryButton} href="#quickstart" endIcon={<ArrowForwardRounded />}>Start parsing</Button>
              <Button className={styles.secondaryButton} href="https://github.com/tranztech/pgn-parser" target="_blank" rel="noreferrer" startIcon={<GitHub />}>View source</Button>
            </Stack>
            <Stack className={styles.signalRow} direction="row" divider={<Divider orientation="vertical" flexItem />}>
              <Box><strong>Java 21</strong><span>Modern runtime</span></Box><Box><strong>AST 1.0</strong><span>Shared contract</span></Box><Box><strong>6 fixtures</strong><span>Conformance tested</span></Box>
            </Stack>
          </Box>
          <Box className={styles.visual} aria-label="PGN transformed into a structured move tree">
            <Box className={styles.boardGlow} /><Box className={styles.chessCard}>
              <Box className={styles.cardTop}><span>LIVE PARSE</span><i /></Box>
              <Box className={styles.board}>
                {Array.from({ length: 64 }, (_, i) => <Box key={i} className={(Math.floor(i / 8) + i) % 2 ? styles.darkSquare : styles.lightSquare} />)}
                <span className={`${styles.piece} ${styles.king}`}>♔</span><span className={`${styles.piece} ${styles.knight}`}>♞</span><span className={`${styles.piece} ${styles.pawn}`}>♙</span>
              </Box>
              <Box className={styles.moveLine}><span>01</span><b>e4</b><b>e5</b><em>main line</em></Box><Box className={styles.moveLine}><span>02</span><b>Nf3</b><b>Nc6</b><em>variation ready</em></Box><Box className={styles.treeLine}><i /><i /><i /><i /></Box>
            </Box>
          </Box>
        </Box>
      </Container>

      <Box id="features" className={styles.featureBand}><Container maxWidth="lg">
        <Stack className={styles.eyebrow} direction="row" sx={{ alignItems: "center" }} spacing={1}><CodeRounded /><span>ENGINEERED FOR CONSISTENCY</span></Stack>
        <Typography component="h2" className={styles.sectionTitle}>PGN parsing you can build on.</Typography>
        <Box className={styles.featureGrid}>{features.map((f) => <Box className={styles.featureCard} key={f.title}><Box className={styles.featureIcon}>{f.icon}</Box><Typography component="h3">{f.title}</Typography><Typography>{f.text}</Typography></Box>)}</Box>
      </Container></Box>

      <Container id="quickstart" maxWidth="lg" className={styles.quickstart}>
        <Box><Stack className={styles.eyebrow} direction="row" sx={{ alignItems: "center" }} spacing={1}><TerminalRounded /><span>QUICK START</span></Stack><Typography component="h2" className={styles.sectionTitle}>From dependency to move tree in minutes.</Typography><Typography className={styles.sectionBody}>Install the Maven package, pass in a PGN string, and work with typed Java records. Legacy entry points remain available while the modern API evolves.</Typography>
          <Stack spacing={1.5} className={styles.checkList}>{["Ordinary Java objects—no JSON tree model", "Strict and tolerant parser modes", "Deterministic canonical JSON when you need it"].map((item) => <Stack key={item} direction="row" spacing={1}><CheckCircleRounded /><span>{item}</span></Stack>)}</Stack>
        </Box>
        <Box className={styles.codeWindow}><Box className={styles.codeTabs}><span className={styles.activeTab}>Maven</span><span>Java</span><i /><b>1.0.0</b></Box><pre><code>{install}</code></pre><Divider /><pre><code>{parse}</code></pre></Box>
      </Container>

      <Box component="footer" className={styles.footer}><Container maxWidth="lg"><Stack direction={{ xs: "column", sm: "row" }} sx={{ justifyContent: "space-between", alignItems: { xs: "flex-start", sm: "center" } }} spacing={2}><Stack direction="row" sx={{ alignItems: "center" }} spacing={1.2}><BrandMark /><Box><strong>Tranz PGN</strong><span>Built by Tranz Technologies</span></Box></Stack><Typography>Same PGN. Same semantics. Every platform.</Typography></Stack></Container></Box>
    </Box>
  );
}
