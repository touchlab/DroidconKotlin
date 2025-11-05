# API Summary Format

## Purpose

This document defines a standardized format for creating API summaries. These summaries are designed to:

1. Provide essential information about an API in a concise format
2. Minimize context usage when working with Claude or other LLMs
3. Capture key usage patterns and gotchas without requiring full documentation
4. Serve as a quick reference for developers

## Format Structure

```markdown
# API Summary: [API Name]

## Overview
[2-3 sentences describing what the API does and its primary purpose]

## Key Classes/Components

### [Class/Component Name]
- **Purpose**: [One-line description of what this class/component does]
- **Key Properties**:
  - `propertyName: Type` - [Brief description]
  - `propertyName2: Type` - [Brief description]
- **Key Methods**:
  - `methodName(param1: Type, param2: Type): ReturnType` - [Brief description]
  - `methodName2(...)` - [Description]
- **Usage Pattern**: [Common usage pattern in 1-2 lines]
- **Example**:
  ```kotlin
  // Short, focused example showing typical usage
  ```

## Common Patterns
- **[Pattern Name]**: [Brief explanation of a common usage pattern]
- **[Pattern Name]**: [Another common pattern]

## Gotchas/Special Considerations
- [Important thing to know that isn't obvious]
- [Common pitfall to avoid]

## Dependencies
- [Key dependency or relationship with other APIs]
- [Another dependency]
```

## Guidelines for Creating API Summaries

1. **Be concise**: Focus on the essential information needed to use the API.
2. **Include signatures**: Method signatures with parameter types and return types are critical.
3. **Highlight patterns**: Include common usage patterns that illustrate how various parts work together.
4. **Note gotchas**: Mention non-obvious behaviors, edge cases, or common pitfalls.
5. **Keep examples minimal**: Include minimal examples that demonstrate the core usage patterns.
6. **Prefer readability**: Use clear language and formatting for easy scanning.

## When to Create API Summaries

API summaries are particularly valuable for:

- External libraries and SDKs
- Complex internal APIs with many components
- APIs that would otherwise require significant documentation to understand
- Code that is frequently referenced but has a complex surface area

## Example Usage

Store API summaries in a dedicated directory (e.g., `/api-summaries/`) and reference them when working with Claude:

"Please refer to the Firebase Firestore API summary at `/api-summaries/firebase-firestore.md` when implementing this feature."

This allows Claude to work with the API without loading the full SDK documentation or implementation details into its context.