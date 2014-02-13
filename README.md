uncc2014watsonsim [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

Deep Question Answering System

## Quick start
- [Download the zipfile](https://googledrive.com/host/0B8wOEC5-v5lXUUllV2stSGRRYTA/watsonsim-quickstart-0.1.1.zip)i
- Dependencies
  - Java >= 7
  - git
  - gradle (included, binaries are in gradle-1.10/bin)
  - Lucene Wikipedia Index (you must already have indexed it; the code will not index it for you)
  - Indri Wikipedia Index (same)
  - Internet Connection (for Google - it's possible to comment it out)
- Setup
  - Install dependencies: `gradle-1.10/bin/gradle assemble`
  - Setup indri library path
    - The easiest way is `gradle-1.10/bin/gradle cleanEclipse eclipse` (leaving out the path if you already have gradle)
    - libindri-jni.so is included in lib/ but it may not be appropriate for your platform. If you get errors about `indri_jni`, find `libindri-jni.so` or `libindri-jni.dll` and copy it to lib/.
    - Alternately, you can edit the following line in .classpath (but you may have to edit it again periodically)
    <attribute name="org.eclipse.jdt.launching.CLASSPATH_ATTR_LIBRARY_PATH_ENTRY" value="FILL_IN_YOUR_LIBRARY_DIRECTORY"/>
    - Can't find libindri-jni? Make sure you enabled Java and SWIG and had the right dependencies when compiling Indri.
  - Change the lucene and indri index paths to match your needs in src/main/java/uncc2014watsonsim/watsonsim
    - This will probably soon be a preference
- Getting started
  - Make sure you are in the branch you want. Use (or google) `git branch` and `git checkout`
  - `git pull` to get the latest code _before_ writing any code.
  - Consider making a branch before making major changes (it's tougher to move the changes later)
  - Gradle is included under `gradle-1.10/bin`.
    - `gradle assemble` -> update dependencies
    - `gradle test` -> run tests
    - `gradle run` -> run watsonsim (it will ask you for questions, give you results)
    - Configuration is in build.gradle


## Alternative start
- Do the same as the quick start, except where the quick start used a zipfile:
  - Install [gradle](http://gradle.org) yourself
  - Find `indri_jni` yourself
Then run (on Linux or compatible):
  - `git clone http://github.com/SeanTater/uncc2014watsonsim.git`
  - `cd uncc2014watsonsim`
  - `gradle assemble`
  - `gradle test` if you want to run the tests
  - `gradle eclipse` if you want to setup eclipse classpaths
    - `gradle cleanEclipse eclipse` to start over with Eclipse if classpaths are broken
  - `gradle run` to start asking questions

Feel free to contribute instructions for other systems.

### Architecture {stub}
The general stages are:

- Query multiple search engines given the question, retrieving the results with scores when possible. These are compiled into a single JSON dataset for the machine learning group to test against.
- Compile aggregate scores based on the original scores given with the search results.
- Choose the top result's title as the question's answer


### Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
