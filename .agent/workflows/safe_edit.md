---
description: Safely modify code with mandatory user approval steps.
---

# Safe Code Modification Workflow

This workflow enforces a strict protocol for modifying code, ensuring that NO changes are made without explicit user approval.

1. **Analyze and Plan**
   - Read the relevant file(s) to understand the current state.
   - Formulate a specific modification plan.

2. **Propose Changes (Diff)**
   - Present the proposed changes to the user.
   - Use a clear format (e.g., `Diff` or "I will change X to Y").
   - **CRITICAL**: Do NOT call any write tools (`write_to_file`, `replace_file_content`, `multi_replace_file_content`) in this step.

3. **Wait for Approval (STOP)**
   - Explicitly ask: "Do you approve these changes?" or "Shall I proceed?".
   - **STOP** and wait for the user's response.
   - If the user says "No" or suggests changes, go back to Step 1.
   - **Proceed to Step 4 ONLY if the user says "Yes", "Proceed", "Fix it", etc.**

4. **Execute Modification**
   - Call the appropriate tool (`write_to_file`, `replace_file_content`, etc.) to apply the changes.

5. **Verify and Format**
   - If the project has a linter/formatter (e.g., `ktlint`), run it to ensure code quality.
   - Confirm the change has been applied correctly.
