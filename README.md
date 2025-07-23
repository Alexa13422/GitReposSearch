# GitHub Repo & Branch Viewer

A simple Spring Boot (Java 21, Spring 3.5) service that lists **non-fork** repositories for a GitHub user—and for each repo, returns its branches with the latest commit SHA.

---

## Prerequisites

- Java 21 SDK  
- Gradle  
- A valid GitHub Personal Access Token (PAT) with at least `public_repo` scope

---

## ⚙️ Configuration

1. **Do not** hard-code your token in source or commit it.  
2. Provide your token via one of these (pick one):

   - **Environment variable**  
     ```bash
     export GITHUB_TOKEN=ghp_yourPersonalAccessTokenHere
     ```
   - **`.env` file** (project root)  
     ```
     GITHUB_TOKEN=ghp_yourPersonalAccessTokenHere
     ```
     _Make sure `.env` is listed in your `.gitignore`._
   - **`application.properties`** (src/main/resources)  
     ```properties
     github.token=${GITHUB_TOKEN}
     ```

---

## ▶️ Running Locally

```bash
./gradlew bootRun
```

### GET `/api/repos/{username}`

**Success – 200 OK**

```json
[
  {
    "name": "my-repo",
    "owner": "octocat",
    "branches": [
      { "name": "main", "commit": "a1b2c3..." },
      { "name": "dev",  "commit": "d4e5f6..." }
    ]
  },
]
```
**User Not Found – 404 Not Found
```JSON
{
  "status": 404,
  "message": "GitHub user not found: {username}"
}
```

