<div align="center">

  # frcworkflows

Production-grade, all-in-one GitHub Reusable Workflow designed for FIRST Robotics Competition (FRC) Java teams.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Author](https://img.shields.io/badge/Author-Veer%20Bajaj%20%28coder11v%29-orange.svg)](https://github.com/coder11v)

</div>

## Features

- Automated WPILib Year & Java Detection
- Robust Gradle CI
- JUnit Test Artifacts & Annotations
- Local On-Runner AI Code Review
- Sticky PR Comments
- Optional Slack Notifications

---

## Quickstart

To use this reusable workflow in your robot repository, create a caller workflow file such as `.github/workflows/ci.yml`:

```yaml
name: Robot CI

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  workflow_dispatch:

concurrency:
  group: ${{ github.workflow }}-${{ github.ref }}
  cancel-in-progress: ${{ github.ref != 'refs/heads/main' }}

jobs:
  frc-ci:
    name: FRC CI
    uses: coder11v/frcworkflows/.github/workflows/frc-ci.yml@v1.0.0
    permissions:
      contents: read
      checks: write
      pull-requests: write
      issues: write
    with:
      fallback-year: "2026"
      enable-ai-review: true
      slack-thread-ts: "" # Optional Slack thread timestamp
      ollama-model: "qwen2.5-coder:1.5b"
    secrets:
      SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }} # Optional
```

---

## Workflow Inputs & Secrets

### Inputs (`with:`)

| Input | Type | Default | Description |
| :--- | :---: | :---: | :--- |
| `fallback-year` | string | `"2026"` | Fallback WPILib season year if unable to detect from `.wpilib/wpilib_preferences.json` or `build.gradle`. |
| `enable-ai-review` | boolean | `true` | Whether to run on-runner Ollama AI code review and post/update sticky comments on pull requests. |
| `slack-thread-ts` | string | `""` | Slack message thread timestamp (`thread_ts`) if notifications should reply inside an existing thread. |
| `ollama-model` | string | `"qwen2.5-coder:1.5b"` | Ollama model identifier to pull and run for code review. |

### Secrets (`secrets:`)

| Secret | Required | Description |
| :--- | :---: | :--- |
| `SLACK_WEBHOOK_URL` | No | Slack incoming webhook URL for CI notifications. If omitted, Slack notifications are cleanly skipped. |
| `COMMENT_TOKEN` | No | Optional token generated from a custom GitHub App (or bot account) to author sticky PR comments as your bot (e.g. `team-bot[bot]`) instead of `github-actions[bot]`. Defaults to `GITHUB_TOKEN`. |

---

## Customizing Commenter Identity (Using a GitHub App)

By default, comments are posted by `github-actions[bot]`. If you want PR comments to appear from a custom identity (e.g., `5171-bot[bot]` or `frc-reviewer[bot]`) with your custom team logo/avatar:

1. **Create a GitHub App**:
   - Go to **Organization / Account Settings** > **Developer Settings** > **GitHub Apps** > **New GitHub App**.
   - Set the App name (e.g., `Team 5171 Robot Reviewer`) and upload your custom logo/avatar.
   - Disable Webhooks (uncheck "Active").
   - Under **Repository Permissions**, grant:
     - `Pull requests`: **Read & write**
     - `Issues`: **Read & write**
   - Click **Create GitHub App**.
   - Generate and download a **Private Key** (`.pem` file), and note the **App ID**.
2. **Install the App**:
   - In the GitHub App settings, click **Install App** and install it on your target robot repositories.
3. **Add Secrets in Caller Repository**:
   - Add `BOT_APP_ID` (the App ID) and `BOT_APP_PRIVATE_KEY` (the `.pem` private key content) to repository/organization secrets.
4. **Pass the Generated Token to the Workflow**:
   ```yaml
   jobs:
     generate-token:
       runs-on: ubuntu-latest
       outputs:
         token: ${{ steps.app-token.outputs.token }}
       steps:
         - uses: actions/create-github-app-token@v1
           id: app-token
           with:
             app-id: ${{ secrets.BOT_APP_ID }}
             private-key: ${{ secrets.BOT_APP_PRIVATE_KEY }}

     frc-ci:
       needs: [generate-token]
       uses: coder11v/frcworkflows/.github/workflows/frc-ci.yml@v1.0.0
       permissions:
         contents: read
         checks: write
         pull-requests: write
         issues: write
       with:
         fallback-year: "2026"
         enable-ai-review: true
       secrets:
         COMMENT_TOKEN: ${{ needs.generate-token.outputs.token }}
         SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
   ```

---

## Required Caller Permissions

Ensure the calling job or workflow includes the following GitHub token permissions:

```yaml
permissions:
  contents: read          # Check out repository and read commit history
  checks: write           # Publish JUnit test result annotations via Checks API
  pull-requests: write    # Create and update sticky PR review comments
  issues: write           # Required by GitHub API for pull request comment management
```

---

## Attribution & Legal

Created and maintained by **Veer Bajaj** ([@coder11v](https://github.com/coder11v)).

Distributed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.
All derivative works, copies, and downstream consumers must retain the copyright notice and attribution specified in [NOTICE](NOTICE).
