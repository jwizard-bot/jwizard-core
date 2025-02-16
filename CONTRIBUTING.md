# Contributing

Thank you for your interest in contributing to our project! Below, you’ll find instructions on how
to get involved, what guidelines to follow, and how our contribution process works.

## How can I help?

There are several ways you can contribute to the project:

1. **Reporting Bugs**: If you find a bug, please report it
   in [info@jwizard.pl](mailto:info@jwizard.pl).
2. **Feature Requests**: If you have an idea for a new feature, create an issue and describe your
   proposal.
3. **Creating Pull Requests**: You can also contribute by adding new features or fixing existing
   code.

## Reporting bugs

1. Review the existing issues to ensure that the bug hasn’t already been reported.
2. If it’s a new bug, open a new issue and include the following details:

- A clear description of the issue.
- Steps to reproduce the bug.
- Your environment (OS version, browser version, etc.).
- Expected and actual behavior.

## Coding guidelines

Please follow these coding standards:

1. Follow the rules defined in the `.editorconfig` file.
2. Use `PascalCase` for naming classes, `camelCase` for variables and methods, and
   `SCREAMING_SNAKE_CASE` for constants.
3. Each Kotlin class should reside in a separate file to avoid clutter in the project.
4. Avoid mutable variables (`var`); whenever possible, use immutable collections or thread-safe
   mutable collections
   (ex. `ConcurrentHashMap` instead of `HashMap`).
5. Always use `const` for primitive constants.
6. Avoid explicitly specifying types when unnecessary (ex. in `get()` constructs).
7. Use braces on the same line.
8. Do not start interface names with the *I* prefix.
9. For two or more properties in a class’s primary constructor, place each property on a new line.
10. Always add a trailing comma for collections with a variable number of elements.

If a convention is not mentioned above, please refer to the official Kotlin coding
conventions: [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).

Failure to follow these conventions may result in your pull request being rejected with a request to
revise the added/modified code.

If you have any questions regarding other aspects of coding conventions, feel free to reach out
at [info@jwizard.pl](mailto:info@jwizard.pl).

## Commit message guidelines

Please follow these commit message conventions:

- **feat**: For adding a new feature.
- **fix**: For fixing a bug.
- **style**: For changes that don’t affect the code logic (ex. formatting).
- **refactor**: For refactoring existing code without changing functionality.
- **test**: For adding or changing tests.
- **chore**: For configuration changes or maintenance tasks.
