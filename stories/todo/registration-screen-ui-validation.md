# Registration Screen – UI & Validation

Business Context:
New users need a simple, clear way to register for Slack. Registration should be easy, with immediate feedback for missing or invalid fields.

Story:
As a new user,
I want to see a registration screen with fields for email, password, and display name,
So that I can create an account and join Slack.

Acceptance Criteria:
- Given I am on the registration screen,
  When I enter an invalid email,
  Then I see an error message (“Please enter a valid email”).
- Given I am on the registration screen,
  When I leave the password field empty or enter less than 8 characters,
  Then I see an error message (“Password must be at least 8 characters”).
- Given I am on the registration screen,
  When I leave any required field empty,
  Then I see an error message (“This field is required”).
- Given I enter valid values and submit,
  When the backend responds with an error (e.g., email already exists),
  Then I see the specific error message from the backend.
- Given I enter valid values and submit,
  When registration succeeds,
  Then I am automatically logged in and redirected to the main screen.

Out of Scope:
- Accessibility features
- Social login
- Email verification

Dependencies:
- Backend registration API

Assumptions:
- Backend handles advanced validation and error messaging
- Registration auto-logs in user
