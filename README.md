uncc2014watsonsim [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

Deep Question Answering System

## Get started

- Quick Start:
  - [Download the zipfile](https://googledrive.com/host/0B8wOEC5-v5lXUUllV2stSGRRYTA/watsonsim-quickstart-0.1.1.zip)
  - libindri-jni.so is included in uncc2014watsonsim/lib but it may not be appropriate for your platforim. If you get errors about `indri_jni`, find `libindri-jni.so` or `libindri-jni.dll` and copy it to uncc2014watsonsim/lib.
  - Where you use `gradle` later, substitute `gradle-1.10/bin/gradle`
- Slower Start:
  - Install [gradle](http://gradle.org), Java (>=7)
  - Compile Indri
  - Find `libindri-jni.so` or `libindri-jni.dll` and copy it to uncc2014watsonsim/lib.
  - `git clone http://github.com/SeanTater/uncc2014watsonsim.git`
- Then, either way:
  - Index Wikipedia Trec with Lucene and with Indri
  - `gradle assemble` to install dependencies (It's possible but complicated to skip this)
  - `gradle cleanEclipse eclipse` to correct Eclipse classpaths (since it needs absolute paths)
  - Change the lucene and indri index paths to match your needs in src/main/java/uncc2014watsonsim/watsonsim
    - This will probably soon be a preference
  - `gradle run` to get started playing and asking watsonsim questions

## Start developing

- Make sure you are in the branch you want. Use (or google) `git branch` and `git checkout`
- `git pull` to get the latest code _before_ writing any code.
- Consider making a branch before making major changes (it's tougher to move the changes later)
- Get comfortable with gradle. As a 5-second tour:
  - `gradle assemble` -> update dependencies
  - `gradle test` -> run tests
  - `gradle run` -> run watsonsim (it will ask you for questions, give you results)
  - Configuration is in build.gradle
- Write code and documentation!
- [Ask to be added as a contributor](stgallag@gmail.com) or if your code is small, send a patch
- Repeat

### Troubleshoot
- Can't find libindri-jni? Make sure you enabled Java and SWIG and had the right dependencies when compiling Indri.

### Architecture {stub}
The general stages are:

- Query multiple search engines given the question, retrieving the results with scores when possible. These are compiled into a single JSON dataset for the machine learning group to test against.
- Compile aggregate scores based on the original scores given with the search results.
- Choose the top result's title as the question's answer

### Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
