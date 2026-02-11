# Loading Indicators for Registration and Login

Business Context:
Users should know when their registration or login request is being processed, to avoid confusion or repeated submissions.

Story:
As a user,
I want to see a loading indicator when I submit the registration or login form,
So that I know my request is being processed.

Acceptance Criteria:
- Given I submit the registration or login form,
  When the request is in progress,
  Then the submit button is disabled and a spinner or loading indicator is shown.
- Given the request completes,
  When I am redirected or see an error,
  Then the loading indicator disappears and the button is enabled.

Out of Scope:
- Accessibility features
- Custom animations

Dependencies:
- Registration and login screens

Assumptions:
- Loading indicator is visible only during network requests
