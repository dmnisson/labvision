# labvision
A tool for comparing students' laboratory results in a classroom with accepted or theoretical values, and analyzing error statistics of groups and individuals in groups.

Students will be able to:

* Report their measurement results and uncertainties to their instructors
* See the results of previous experiments
* Analyze the errors in past experiments and compare their results with accepted values
* Submit detailed lab reports in PDF or Word format to instructors for scoring
* See the scores of past lab reports for each course

Instructors will be able to:

* Design experiments with parameters to be adjusted and measurements to be obtained, and optionally set accepted values for obtained results
* Compute results from students' measurements using an entered formula and compare it to students' obtained results
* See results of all students and analyze deviations from accepted values classroom-wide
* Retrieve submitted lab reports and score them using either a numeric system of points or a letter grade

# Setup
Although many of the pages in this project have not been completed, developers are welcome to test the current state on their system and contribute ideas for what has not yet been implemented. If you would like to do so, then all you need is Apache Maven 3.6.2 and JDK 1.8 or later.

The quickest way to set up LabVision on your local system is to clone the repository, and change to the directory where your clone is located. LabVision is configured to accept connections only on HTTPS, so you will need to generate a self-signed certficate so the server can be used for testing purposes. The easiest way to do this is using the JDK keytool. Change to the directory `src/main/resources/keystore` and then run:

```
keytool -genkeypair -alias labvision -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore labvision.p12 -validity 3650
```

Follow the on-screen directions. After the certificate is created, the password needs to be set in the application configuration using the `server.ssl.key-store-password` property in the `src/main/resources/application.properties` file.

Once you have the certificate and keys, you can run the command

```
mvn spring-boot:run
```

and then visit https://localhost:8443 in your browser to reach the home page. The first time you do this, the `DevInitializingBean` will automatically create a seed database with some default users that can allow you to test the system on your local machine.

The default user accounts are:

| Username      | Password       |
|---------------|----------------|
| `student1`    | `Password123`  |
| `instructor1` | `Password1234` |
| `admin1`      | `Password123`  |

# Password Validation
LabVision requires passwords to be at least 8 characters in length. When users choose passwords, LabVision can read from a text file to match the user's chosen password against a list of commonly used passwords, to protect the user against brute-force and "credential stuffing" attacks. The repository configuration uses a copy of the list of 10,000 most commonly used passwords from https://github.com/danielmiessler/SecLists/blob/master/Passwords/Common-Credentials/10-million-password-list-top-10000.txt located in `src/main/resources/auth`; however, a different file can be used instead by setting the `app.auth.password-blacklist-file` property.
