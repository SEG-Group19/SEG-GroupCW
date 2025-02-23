# Project Documentation

Welcome to our Maven + JavaFX project! This README provides an overview of our **version control strategy**, **testing pipeline**, **branching conventions**, and **versioning scheme**.

---

## Table of Contents
1. [Overview](#overview)
2. [Version Control & Branch Protection](#version-control--branch-protection)
3. [Branching Strategy](#branching-strategy)
4. [Branch Naming Conventions](#branch-naming-conventions)
5. [Testing](#testing)
6. [Continuous Integration (CI/CD) Setup](#continuous-integration-cicd-setup)
7. [Versioning Scheme](#versioning-scheme)

---

## Overview
We use **GitHub** to host our repository and **Git** for version control. The project is set up with:
- **Maven** for build and dependency management.
- **JavaFX** for the GUI.
- **JUnit** and **TestFX** for testing (including GUI tests).
- **GitHub Actions** for our CI/CD pipeline.

We’ve also configured:
1. **Branch protection** on `main`, disallowing direct pushes.
2. **At least one reviewer** required on pull requests.
3. **CI pipeline** must pass before merging.
4. **Discord webhook** to get notifications about the repo status.

---

## Version Control & Branch Protection

### No Direct Pushes to `main`
- Developers must create a separate branch for each feature or fix.
- Changes are merged via **pull requests** (PRs) only.
- The **CI pipeline** must pass and at least one team member must approve the PR before merge.

### No Force Pushes
- Force pushing is disabled on `main` to protect commit history.
- If a branch requires rewriting, do it locally, but **never** force push to `main`.

### Workflow must pass before merging to `main`
- The CI pipeline must pass before merging a PR to `main`.
- This ensures that the code compiles and all tests pass.

---

## Branching Strategy
We follow a **feature-branching** model:
1. Create a new branch from `main` for each feature, bugfix, or other task.
2. Commit changes to that branch.
3. Open a pull request (PR) into `main`.
4. Wait for CI checks to pass and request a review from another team member.
5. Once approved, merge your branch into `main`.

> **Example**: If you’re adding a login UI, create `feature/login-ui`. Develop there, then open a PR to `main` when ready.

This ensures we always have a stable `main` branch and that all changes are reviewed before merging.

---

## Branch Naming Conventions
We use the format:
```
<category>/<short-description>
```
Where **category** can be:
- `feature` — New feature or enhancement
- `bugfix` — Bug fix
- `hotfix` — Critical production fix
- `refactor` — Code cleanup, restructuring, or optimisation
- `docs` — Documentation updates
- `test` — Changes related to testing or test automation
- `chore` — Maintenance tasks, dependency updates, CI/CD changes

**Examples**:
- `feature/login-ui`
- `bugfix/dashboard-null-pointer`
- `docs/update-readme`
- `chore/update-dependencies`

---

## Testing

### Running Tests Locally
- We use **JUnit** for standard logic tests and **TestFX** for JavaFX (GUI) tests.
- Run all tests:
  ```bash
  mvn clean test
  ```
- If you only want to run a specific test:
  ```bash
  mvn clean test -Dtest=ClassNameTest
  ```

### TestFX & Headless Mode
- On CI, we often run GUI tests with `xvfb-run` on Linux to simulate a display:
  ```bash
  xvfb-run -a mvn clean test
  ```
- Locally, you can run them in a normal window by simply using `mvn test`.

---

## Continuous Integration (CI/CD) Setup
We use **GitHub Actions** with the following steps:

1. **Check out the code**  
   Uses [`actions/checkout@v4`](https://github.com/actions/checkout).

2. **Set up Java**  
   Installs Java 17 using [`actions/setup-java@v4`](https://github.com/actions/setup-java).

3. **Cache Maven dependencies** (optional)  
   Speeds up builds by caching `~/.m2/repository`.

4. **Build and Test**  
   Runs:
   ```bash
   mvn clean package
   ```
   Fails if build fails or if any of tests do not pass.

5. **Branch Protection**  
   The workflow must pass before merging to `main`. No direct pushes to `main` are allowed.

---

## Versioning Scheme

### Semantic Versioning (SemVer)
We adopt **SemVer** (`MAJOR.MINOR.PATCH`):
- **MAJOR**: Breaking changes or not backward-compatible updates.
- **MINOR**: Backward-compatible new features.
- **PATCH**: Backward-compatible bug fixes or improvements.

### Initial Version
- **`v1.0.0`** will be our first stable version.
- Before that, we may use `v0.x.y` to indicate a pre-1.0 version (less stable, possibly frequent breaking changes).
- Each release is **tagged** in Git (e.g. `git tag v1.0.0` then `git push origin v1.0.0`).

> **Example usage**: Assume you fix a bug in the dashboard UI and we're on v1.1.2. This would be a PATCH release. After merging the PR, tag the commit with `v1.1.3` (`git tag v1.1.3`) and push the tag (`git push origin v1.1.3`).

### Examples
- `1.0.3 → 1.0.4` (PATCH): Minor bug fix or small improvement.
- `1.0.2 → 1.1.0` (MINOR): Adds new features but keeps old code working.
- `1.1.4 → 2.0.0` (MAJOR): Breaking changes requiring users to modify code.

---

## Summary
- **Version Control**: Feature branching, pull requests, no direct pushes to `main`.
- **Testing**: `mvn test`, uses JUnit + TestFX, runs automatically in CI.
- **CI/CD**: GitHub Actions checks compilation + tests; must pass before merge.
- **SemVer**: Use `MAJOR.MINOR.PATCH`; tag releases.
- **Branch Naming**: `<category>/<short-description>`, e.g. `feature/login-ui`.

By following these practices, our codebase remains maintainable, consistent, and thoroughly tested. If you have any questions or suggestions, please bring them up at the next meeting or on Discord.

