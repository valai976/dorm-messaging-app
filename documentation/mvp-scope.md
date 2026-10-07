# Dorm Messaging App MVP Scope

## Purpose

Build a local JavaFX desktop application using Maven that allows dorm residents to share information, ask questions, and reply to conversations.

 Included Features

Residents will be able to:

 Create an account with a unique username and password.
 Log in and log out.
 Select a dorm community.
 View and post dorm messages showing the author and posting time.
 View and add replies to messages.

The app will reject blank required fields, duplicate usernames, incorrect login credentials, and blank messages or replies. Accounts, messages, and replies will be saved locally between sessions.

 Application Windows

1. Account window: Account creation and login, with validation feedback.
2. Dorm messaging window: Dorm selection, message feed, posting, replies, and logout.

 Excluded Features

Communication between separate computers, message editing and deletion, search, reporting, moderation, and pinned announcements are outside the MVP.

 Completion Criteria

 Included features work through the two JavaFX windows.
 Accounts and conversations remain available after restarting.
 Significant logic is tested and kept outside codebehind classes.
 Compilation, tests, and course Checkstyle checks pass.
 Tests achieve at least 95% code coverage, excluding Main and codebehind classes.
 Development uses feature branches created from and merged into dev.
 A final pull request from dev to release describes the implemented scope.
 Required screenshots and submission archives are prepared.
