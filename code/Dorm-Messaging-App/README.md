# Lecture JavaFX sample

Source: https://github.com/JACorley1/devops-simple-maven-ci-example
Imported from lecture commit 4b18eef, retaining its public-domain license.
This grocery-list GUI is the lab sample for the Dorm Messaging App repository.

Requires JDK 17 and Maven. From this folder:

    mvn clean verify
    mvn javafx:run

The verify lifecycle runs JUnit tests, enforces devops_checks.xml, and requires
90% line coverage per non-GUI package. The JavaFX entry point and controllers
are excluded from the coverage minimum, matching the lecture example.
The GitHub workflow runs on pushes to dev and PRs targeting dev or release.
