# Structured Instruction Formats for Claude

This document outlines structured formats for providing instructions to Claude that maximize clarity, reliability, and successful execution, especially for complex technical tasks.

## 1. Phased Task Execution

```
## PHASE: Analysis [NO CODE CHANGES]
TASK: Analyze all custom types in file X
REQUIREMENTS:
- List each type name
- For each type, identify where it's used
- For each type, find if equivalent exists in reference code
OUTPUT FORMAT:
| Type | Used In | Equivalent In Reference | Notes |

## PHASE: Planning [NO CODE CHANGES]
TASK: Create plan based on analysis
REQUIREMENTS:
- For each type, define specific action (keep/remove/modify)
- Include reasoning for each decision
WAIT FOR APPROVAL: Yes

## PHASE: Implementation
TASK: Execute approved plan
REQUIREMENTS:
- Process ONE type at a time
- Show diff after each change
- Verify code compiles after each change
```

## 2. Explicit Analysis Requests

```
## ANALYSIS: Custom Schema Types
FOCUS: File path/to/CustomTypes.kt
COLLECT:
1. List all type names in the file
2. For each type:
   - Identify all references in the codebase
   - Check if equivalent exists in Swift counterpart at path/to/SwiftTypes.swift

FORMAT OUTPUT AS:
| Type Name | References in Kotlin | Equivalent in Swift | Action Needed |
```

## 3. Step-Gated Execution

```
## STEP 1: Examine file X
EXECUTE: Read file X
ANALYZE: Identify all methods that match pattern Y
VERIFY: Show output before proceeding

## STEP 2: Cross-reference with reference implementation
EXECUTE: For each method identified in Step 1
ANALYZE: Find matching method in reference file
VERIFY: Show match status before proceeding

## STEP 3: Implement changes
EXECUTE: Only after Step 2 verification
CHANGE: Apply specific modifications based on analysis
VERIFY: Test each change before proceeding to next
```

## 4. Decision-Tree Instructions

```
## TASK: Fix Schema Type Mismatches
FOR EACH type in CustomTypes.kt:
  IF type exists in SwiftTypes.swift:
     IF signatures match exactly:
        ACTION: Keep as is
     ELSE:
        ACTION: Modify to match Swift signature
  ELSE IF type is used in Kotlin implementation:
     IF matching functionality exists in Swift:
        ACTION: Rename and restructure to match Swift pattern
     ELSE:
        ACTION: Keep but mark as Kotlin-specific extension
  ELSE:
        ACTION: Remove unused type
```

## 5. Progress Tracking Format

```
## TASK: Update API Implementation
STATUS: [IN PROGRESS | COMPLETE | BLOCKED]

COMPLETED:
- ✅ Step 1: Analysis of types
- ✅ Step 2: Identified mismatches

IN PROGRESS:
- 🔄 Step 3: Fixing parameter types in Method X

PENDING:
- ⏱️ Step 4: Fix return types
- ⏱️ Step 5: Final verification

BLOCKERS:
- 🛑 Need clarification on Type X implementation
```

## 6. Validation Testing Format

```
## VALIDATION: API Implementation
TEST CASES:
1. Function X with parameter Y
   - Expected: Z
   - Actual: [PASS|FAIL] with details
2. Function A with parameter B
   - Expected: C
   - Actual: [PASS|FAIL] with details

ISSUES FOUND:
1. Method D fails when...
   - Root cause: ...
   - Fix plan: ...
```

## Guidelines for Effective Instructions

1. **Explicit Verification Points**: Always include points where Claude should pause and verify before proceeding
2. **Single Responsibility**: Each instruction should do one thing clearly
3. **Clear Input/Output**: Specify exact format for output
4. **Measurable Completion**: Define what "done" looks like
5. **Context Preservation**: Reference specific lines/files
6. **Checkpointing**: Save progress regularly
7. **Iterative Approach**: Break complex tasks into smaller, verifiable units

## Special Note About Task Lists

When using task lists:

```
## TASK LIST: API Implementation [CURRENT STATUS: 2/5 COMPLETE]

- [x] Analyze schema types
- [x] Create mapping document
- [ ] UPDATE NEXT: Implement parameter type fixes
- [ ] Fix return types
- [ ] Remove unused methods
```

1. Always mark the NEXT task to be executed with "UPDATE NEXT" to clearly signal which task should be worked on
2. Always update the status count in the heading
3. Check off tasks as they are completed
4. Only proceed to the next task when the current one is fully completed and verified 