uncc2014watsonsim [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

Deep Question Answering System

## Get started

- Quick Start:
  - [Download the zipfile](https://googledrive.com/host/0B8wOEC5-v5lXUUllV2stSGRRYTA/watsonsim-0.1.2.zip)
  - libindri-jni.so is included in watsonsim-0.1.1/lib but it may not be appropriate for your platforim. If you get errors about `indri_jni`, find `libindri-jni.so` or `libindri-jni.dll` and copy it to uncc2014watsonsim/lib.
  - Where you use `gradle` later, substitute `gradle-1.11/bin/gradle`
- Slower Start:
  - Install [gradle](http://gradle.org), Java (>=7)
  - Compile Indri
  - Find `libindri-jni.so` or `libindri-jni.dll` and copy it to uncc2014watsonsim/lib.
  - `git clone https://github.com/SeanTater/uncc2014watsonsim.git`
- Then, either way:
  - Setup Google Search (in src/main/.../PrivateGoogleCredentials.java)
    - Make your own Google cloud app in the [Google console](https://cloud.google.com/console/).
      - Put the name into the source.
      - Enable the Custom Search API
      - Create a server Public API Key, put it in the source.
    - Make your own custom search engine in the [Custom Search Console](https://www.google.com/cse/create/new)
      - Search any site (but you have to pick a domain, maybe wikipedia.org would be good)
      - Edit the custom search you just made. In "Sites to search", change "Search only included sites" to "Search the entire web but emphasize included sites"
      - Get the search engine ID, put it in the source.
  - Setup local search engines
    - Index Wikipedia Trec with Lucene and with Indri (otherwise you must disable them)
    - Edit the paths in WatsonSim.java to match your index locations
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
- Write code and [documentation](http://seantater.github.io/uncc2014watsonsim/)!
- [Ask to be added as a contributor](mailto:stgallag@gmail.com) or if your code is small, send a patch
- Repeat

## Troubleshoot
- Can't find libindri-jni? Make sure you enabled Java and SWIG and had the right dependencies when compiling Indri.

## Architecture {stub}
Testing setup:
- A large database of questions is run against predefined search engines.
- The results are recorded as a large JSON file, saved, and later reopened.
- The results are rescored (by an average or using hand built Logistic Regression)
- The top result becomes the candidate answer, and statistics are generated

Classes:
- Question: Holds ResultSet's, collates similar results together (using Levenshtein distance)
- ResultSet: Holds one candidate answer text (as title), and 1+ Engines.
- Engine: Represents one search result, has a rank, a score, and an engine name.

### Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Examine the reference documentation](http://seantater.github.io/uncc2014watsonsim/)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
