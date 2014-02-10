uncc2014watsonsim [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

Deep Question Answering System

## Build
You need:

- Java >= 7
- [Gradle](http://www.gradle.org)
 - You may not need to install it, as long as you can run the `gradle` binary.
- An internet connection (to download dependencies)

Then run (on Linux or compatible):
  - `git clone http://github.com/SeanTater/uncc2014watsonsim.git`
  - `cd uncc2014watsonsim`
  - `gradle assemble`
  - `gradle test` if you want to run the tests
  - `gradle eclipse` if you want to setup eclipse classpaths

Feel free to contribute instructions for other systems.

## Develop

### Getting started

- Build it first
- Make your changes
 - Make and run tests on your changes (tests are JUnit4, and go in src/tests/java/uncc2014watsonsim/)
- Submit changes
 - Either [make a pull request](https://help.github.com/articles/using-pull-requests) for which there are great how-to's
 - or, send [me](mailto:stgallag@gmail.com) a request to become a contributer, then push your own changes.

### Architecture
The general stages are:

- Query multiple search engines given the question, retrieving the results with scores when possible. These are compiled into a single JSON dataset for the machine learning group to test against.
- Compile aggregate scores based on the original scores given with the search results.
- Choose the top result's title as the question's answer


### Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
