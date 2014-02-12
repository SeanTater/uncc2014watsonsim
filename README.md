uncc2014watsonsim [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

Deep Question Answering System

## Quick start
- [Download the zipfile](https://googledrive.com/host/0B8wOEC5-v5lXUUllV2stSGRRYTA/watsonsim-quickstart-0.1.1.zip)
- Be sure to:
  - Make sure you are in the branch you want. Use (or google) `git branch` and `git checkout`
  - `git pull` to get the latest code _before_ writing any code. Our project is changing fast, don't rely on yesterday's version!
  - Change the indri library path in .classpath to match the directory where your indri .dll or .so files are stored. (libindri-jni.so specifically) Otherwise you will see `Exception in thread "main" java.lang.UnsatisfiedLinkError: no indri_jni in java.library.path`
    - Can't find libindri-jni? Make sure you enabled SWIG and had the right dependencies when compiling Indri.
  - Change the lucene and indri index paths to match your needs in src/main/java/uncc2014watsonsim/watsonsim
- Remember that:
  - A git repository is included.
  - Consider making a branch before making major changes (it's tougher to move the changes later)
  - Gradle is included under `gradle-1.10/bin`.
    - use `gradle-1.10/bin/gradle assemble` to get the latest dependencies
    - add the appropriate line (or ask for it to be added) to build.gradle when you need another library


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
  - or `gradle test` (optional)

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
