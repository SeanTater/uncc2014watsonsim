watson
======

Watson clone Deep Question Answering System


Building `watson`
-----------------
You need:

- Java >= 7
- [Gradle](http://www.gradle.org)
 - You may not need to install it, as long as you can run the `gradle` binary.
- An internet connection (to download dependencies)
- The Search Engine group's dataset (comb.json) if you plan to run the tests.

Then run (on Linux or compatible):
  - `git clone http://github.com/SeanTater/watson.git`
  - `cd watson`
  - `gradle compileJava` or `gradle compileTestJava` (for running tests automatically)

Feel free to contribute instructions for other systems.


Architecture
------------
The general stages are:

- Query multiple search engines given the question, retrieving the results with scores when possible. These are compiled into a single JSON dataset for the machine learning group to test against.
- Compile aggregate scores based on the original scores given with the search results.
- Choose the top result's title as the question's answer
