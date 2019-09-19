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

The quickest way to set up LabVision on your local system is to clone the repository, and change to the directory where your clone is located. LabVision is configured to accept connections only on HTTPS, so you will need to generate a public and private key and put it into a keystore that you can install for testing

Once you have the certificate and keys, you can run the command

```
mvn jetty:run
```

and then visit https://localhost:8443 in your browser to reach the home page.
