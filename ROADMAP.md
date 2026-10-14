# Roadmap

**Password Vault Desktop** is the successor of my learning project
[password-vault-cli](https://github.com/m3xXmo0/password-vault-cli).
The goal is to turn the command-line vault into a desktop password manager
with a graphical interface, a stronger security model and an easy installation.

This roadmap shows what is planned and what is currently being worked on.
It will change as the project evolves.

> **Note:** This roadmap was drafted together with Claude as a planning
> assistant and then reviewed and adapted by me. The implementation is my own work.

**Status:** ✅ done · 🚧 in progress · 📋 planned

---

## Overview

| Milestone | Version | Status |
|---|---|---|
| Command-line vault (learning project) | [v1.0.0](https://github.com/m3xXmo0/password-vault-cli/releases/tag/v1.0.0) | ✅ |
| Project setup & tooling | – | 🚧 |
| Secure core | v2.0.0-alpha | 📋 |
| First graphical interface | v2.0.0-beta | 📋 |
| Polished interface & convenience security | v2.0.0-rc1 | 📋 |
| Installers & first stable release | v2.0.0 | 📋 |
| Optional: SQLite storage | v2.1.0 | 📋 |

---

## ✅ v1.0.0: Command-line vault

Released as [password-vault-cli](https://github.com/m3xXmo0/password-vault-cli).

- [x] Add, view and delete entries (service, username, password)
- [x] AES-256-GCM encrypted vault file
- [x] PBKDF2 key derivation (600,000 iterations, random salt)
- [x] Detection of wrong master password and tampered files
- [x] Password generator based on `SecureRandom`
- [x] Change master password
- [x] Unit tests with JUnit 5 and GitHub Actions CI

## 🚧 Project setup & tooling

- [ ] Maven Wrapper (build without installing Maven)
- [ ] Dependabot for dependency updates
- [ ] CodeQL code scanning
- [ ] Clean-up of constants and formatting

## 📋 v2.0.0-alpha: Secure core

Strengthening the foundation before building the GUI.

- [ ] Atomic file writes to prevent data loss on crashes
- [ ] Automatic backup of the previous vault file
- [ ] Versioned binary file format with authenticated header (AES-GCM associated data)
- [ ] Automatic migration of v1 vault files
- [ ] Argon2id key derivation (replacing PBKDF2 for new vaults)
- [ ] Random data key wrapped by the master key (fast master password changes)
- [ ] Wiping passwords and key material from memory after use
- [ ] Minimum length for the master password
- [ ] Updated threat model

## 📋 v2.0.0-beta: First graphical interface

A JavaFX interface covering all existing features.

- [ ] Login screen with hidden password input
- [ ] Unlocking in the background without freezing the window
- [ ] Table view of all entries
- [ ] Dialogs to add and delete entries
- [ ] Copy password to clipboard
- [ ] Dialog to change the master password
- [ ] Replacing the command-line interface

## 📋 v2.0.0-rc1: Polished interface & convenience security

- [ ] Modern look with light and dark mode (AtlantaFX)
- [ ] Vault stored in the user's data directory
- [ ] Live search
- [ ] Clipboard is cleared automatically after 30 seconds
- [ ] Auto-lock after inactivity
- [ ] Show/hide password
- [ ] Edit entries
- [ ] Additional fields: URL, notes, timestamps
- [ ] Configurable password generator with strength indicator
- [ ] Keyboard shortcuts and application icon

## 📋 v2.0.0: Installers & first stable release

- [ ] Windows installer via `jpackage` (Java included, no separate installation needed)
- [ ] Automated release builds with GitHub Actions
- [ ] SHA-256 checksums for downloads
- [ ] Installation guide in the README (installer and `git clone`)
- [ ] Changelog and security policy

## 📋 v2.1.0 (optional): SQLite storage

- [ ] Storage layer behind an interface
- [ ] SQLite backend with every entry encrypted individually (including service names)
- [ ] Migration from the file-based vault

---

## Ideas for later

- Import and export (CSV)
- Check passwords against known data breaches (Have I Been Pwned, k-anonymity)
- Highlight weak or reused passwords
- Categories and favourites
- TOTP codes for two-factor authentication
- Key file as an additional factor

## Out of scope

- **Cloud sync or user accounts on a server.** This stays a local, offline tool.
  The encrypted vault file can be synced with any cloud folder if needed.
- **Browser extension / autofill.**
- **Custom cryptography.** Only well-established algorithms from reviewed libraries are used.

---

## Security status

This project has **not** been externally audited.
Until the milestones above are completed and reviewed, it is **not intended for real passwords**.
Please use an established password manager such as [KeePassXC](https://keepassxc.org)
or [Bitwarden](https://bitwarden.com) for your actual credentials.
