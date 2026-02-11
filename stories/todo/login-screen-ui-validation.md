# Login Screen – UI & Validation

Business Context:
Returning users need a fast, clear way to log in and access their channels and DMs.

Story:
As a returning user,
I want to see a login screen with fields for email and password,
So that I can access my Slack account.

Acceptance Criteria:
- Given I am on the login screen,
  When I leave any field empty,
  Then I see an error message (“This field is required”).
- Given I enter invalid credentials,
  When I submit the form,
  Then I see the specific error message from the backend (e.g., “Invalid email or password”).
- Given I enter valid credentials,
  When login succeeds,
  Then I am redirected to the main screen.

Out of Scope:
- Accessibility features
- “Forgot password” flow
- Social login

Dependencies:
- Backend login API

Assumptions:
- Backend returns specific error messages for failed login
