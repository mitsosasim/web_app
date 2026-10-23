# Setup and implementation notes

## Local demo

Use JDK 17 and Maven 3.9 or newer from the repository root:

```sh
mvn verify
mvn -Pdemo test-compile exec:java
```

Open `http://127.0.0.1:8080/pet-care/`. Press **Search** to browse keepers or **Login** to use an account listed in the README. The keeper account has a requested booking. The administrator sees one owner, three keepers, two pets, and a keeper share of 25.5 from a finished booking priced at 30. That share follows the source's 85% calculation.

The demo binds only to the loopback address, creates an in-memory H2 database in MySQL compatibility mode, and loads `sql/demo-data.sql`. Restarting resets the data. It does not create or delete any MySQL database. `OPENAI_API_KEY` must be unset when launching the demo. The assistant then responds with an explicit not-configured status.

To choose another port:

```sh
mvn -Pdemo -Ddemo.port=8081 test-compile exec:java
```

## Build and checks

`mvn verify` produces `target/pet-care.war`. Eight integration tests start the real servlet application on a temporary loopback port and send HTTP requests backed by the JDBC database. They check the visitor directory, owner login/session/logout, rejected login, pending booking details, unoccupied keepers, admin counts and share, foreign-key rejection, and unavailable maintenance/assistant routes.

The browser capture also checks that accepting a requested booking removes it from the keeper's pending query. It captures three UI states without mocked responses and reports JavaScript errors or unexpected external requests. The fixture uses fictional contact details and public demo passwords.

For screenshot reproduction, start the demo, then use a second terminal:

```sh
npm install --no-save --package-lock=false playwright@1.62.1
npx playwright install chromium --only-shell
node tools/capture-screenshots.cjs
```

The script writes the three PNGs in `docs/assets`. Set `PETCARE_BASE_URL` when using another local port. Screenshots reflect the prototype UI and synthetic data, not a deployed service.

## MySQL and Tomcat deployment

The application uses the `javax.servlet` namespace and can be packaged for Tomcat 9. Create a dedicated local MySQL database and load the schema before deployment:

```sh
mysql -u root -p -e 'CREATE DATABASE HY359_2023;'
mysql -u root -p HY359_2023 < sql/schema.sql
mysql -u root -p HY359_2023 < sql/demo-data.sql
```

Configure the Tomcat process with `DB_URL`, `DB_USER`, and `DB_PASSWORD`. The default URL is `jdbc:mysql://localhost:3306/HY359_2023`. `DB_SERVER_URL` and `DB_NAME` configure the legacy initialization helpers. Matching JVM system properties take precedence over environment values. Use your own local database account and keep credentials outside the repository.

Copy `target/pet-care.war` to Tomcat's `webapps` directory. The automated checks establish H2 behavior, not a verified MySQL deployment. The legacy database initializer/deleter source remains available, but its servlet routes are excluded from the deployed descriptor.

## Implementation boundaries

The schema connects pets to owners, bookings to owners/pets/keepers, messages to bookings, and reviews to owners/keepers. Availability queries exclude keepers with requested or accepted bookings across all dates. They do not implement interval scheduling, and a keeper's pending query returns only its first requested booking.

The project uses concatenated SQL and plain passwords, returns broad account records to the client, and relies heavily on client-side role controls. Server-side authorization and session-bound identity checks are incomplete. Booking status changes are not a fully constrained state machine. Do not expose this prototype or its public administrator account as a production service.

Several UI paths remain partial. Pet registration assumes a single pet lookup per owner, some validation and markup are inconsistent, the footer clear control targets a missing element, and logout does not clear all previously rendered role content. Messaging and review tables/helpers exist, but the current interface does not provide a complete messaging/review workflow.

The map code is unfinished and has no configured geocoding service. The optional assistant servlet uses server-side `OPENAI_API_KEY` configuration and the legacy API integration from the project. External APIs were not invoked or verified. Maps, assistant output, route distance calculations, and full registration/profile-edit workflows are not established by the passing tests. Original reports are retained as historical documentation of the two implementations.
