# Roadmap

MetroBoard is a fork of [FlorisBoard](https://github.com/florisboard/florisboard), rebranded and adapted as the default keyboard for Metro OS. This document outlines what is planned for MetroBoard in the foreseeable future. There are no ETAs — experience has shown they don't hold anyway.

The current release line is **0.6** (`0.6.0-alpha`). MetroBoard inherits FlorisBoard's milestone structure; items already merged upstream and present in this tree are marked accordingly.

## Relationship to FlorisBoard

MetroBoard periodically rebases its non-branding changes onto upstream FlorisBoard. Feature work that is not MetroBoard-specific (the keyboard core, NLP, theming engine) is expected to stay close to upstream so those rebases remain practical. MetroBoard-specific work is the Metro OS design language, branding, and Metro OS integration.

## 0.5 — done (inherited)

- [x] Theme rework II / Snygg v2 — the `lib/snygg` module and the in-app theme editor
- [x] Android 13 / 14 feature support
- [x] Minimum supported Android raised from 7 (SDK 24) to 8 (SDK 26)
- [x] Time-based theme switching

## 0.6 — in development

- [ ] Predictive text / spell checking
  - NLP scaffolding is in the tree (`ime/nlp/` — `NlpManager`, latin/han providers, `SpellingResult`, `MetroboardSpellCheckerService`); the on-device suggestion core is the remaining work
- [ ] Language Pack extension type — bundles the locale-relevant data (predictive base model, emoji suggestion data, …) into one dynamically importable extension file (`ime/nlp/LanguagePackExtension.kt` is the entry point)
- [ ] Proper physical keyboard support
- [ ] Cache manager rework (`lib/cache`)

## k3lp — parallel, version-independent

k3lp development is not tied to a MetroBoard version and happens upstream at [codeberg.org/k3lp/k3lp](https://codeberg.org/k3lp/k3lp).

- [ ] New keyboard layout engine + file syntax based on the upcoming Unicode Keyboard v3 standard
- [ ] Tablet mode / landscape input optimizations built on the new layout engine

## 0.7+

From 0.7 onward the plan is smaller, more frequent stable releases with at most one large feature each.

- [ ] Floating keyboard mode — the `ime/window/` module (`ImeWindowMode`, `ImeWindowController`, resize/move handles) is the foundation; the UI and preference surface are the remaining work
- [ ] New text processing logic
- [ ] Emoji panel rework — search, fully scrollable list with soft category borders, swipe-between-categories, more granular theming, layout customization, possible move to emoji2
- [ ] Glide typing reimplemented on the new layout engine and predictive core (`ime/text/gestures/` has the current statistical classifier)
- [ ] Prepare the repository and store presence for a public beta on Google Play
- [ ] Rework branding images and copy for the app stores
- [ ] Stability and experience pass across the app and keyboard
- [ ] Android 15 / 16 feature support (compile/target SDK is already 36)

## Metro OS integration (MetroBoard-specific)

- [ ] Metro UI design language parity — tiles, accent theming, motion (`MetroboardAnimation.kt`, `metroTurnstile*`)
- [ ] Ship the Metro OS default theme set as a bundled theme extension
- [ ] Follow the Metro OS system accent colour

## Backlog / planned (unassigned)

Features that **may** be added — even in the versions above — or dropped.

- Full on-board layout editor (create layouts without writing JSON)
- Voice-to-text via an open-source voice provider
- Text translation
- Stickers / GIFs
- Kaomoji panel
- Additional layouts
- Tasker / Automate / MacroDroid plugin support
- Wear OS / smartwatch support
- Handwriting input
