uncc2014watsonsim [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

Deep Question Answering System

## [Updates and Progress on Development](http://watsonsim.blogspot.com)

## Setup
Keep in mind that the program may change faster than its documentation. If you are experiencing problems, [contact a developer](mailto:stgallag@gmail.com).

### Overview
- For the program
  - [git](http://git-scm.com/downloads) clone https://github.com/SeanTater/uncc2014watsonsim.git
  - Java 8, either:
    - [Bundled with Eclipse](https://www.eclipse.org/downloads/)
    - Ubuntu utopic+: `sudo apt-get install openjdk-8-jdk`
    - Fedora 20+: `yum install java-1.8.0-openjdk`
    - [Windows, Mac, all others](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
  - [Indri search library (native)](http://www.lemurproject.org/indri.php), needs to be compiled with SWIG and Java, afterward copy the libindri_jni.* into uncc2014watsonsim/lib
  - libSVM machine learning library (native)
    - Ubuntu, Fedora: install `libsvm-java`
    - [Windows](http://www.csie.ntu.edu.tw/~cjlin/libsvm/) ([instructions](http://stackoverflow.com/questions/25060178/which-weka-and-libsvm-jar-files-to-use-in-java-code-for-svm-classification))
  - [Gradle](http://gradle.org/downloads) (just unzip; no install necessary, keep in mind it updates very often)
  - Run `gradle eclipse` in `uncc2014watsonsim/` to download platform-independent dependencies and create an Eclipse project
- For the data:
  - A good internet connection, patience, and about 100GB free space
  - [Postgres](http://www.postgresql.org/download/) (we use 9.3)
  - The latest [Lucene and Indri indexes](https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.tar.xz). Just unzip into the data/ subdirectory.
  - The latest [database snapshot](https://dl.dropboxusercontent.com/u/92563044/watsonsim/data-snapshot.pgdump). Load it into Postgres using `pg_restore -d watsonsim [more options as necessary] data-snapshot.pgdump`.
  - The snapshots are updated weekly via a cron job.
- For Bing web search
  - copy src/main/java/privatedata/UserSpecificConstants.java.sample to src/main/java/privatedata/UserSpecificConstants.java
  - [Create an Azure account and sign up for Bing](https://datamarket.azure.com/dataset/bing/search), put the [API key](https://datamarket.azure.com/account/keys) the right variable in UserSpecificConstants.java 
- For Google web search (which is disabled currently, so this is optional and will not (yet) be used)
  - Make a new [Google cloud app](https://cloud.google.com/console), and put the name in UserSpecificConstants.java
    - Enable the Custom Search API, create a server public API key, and paste it into UserSpecificConstants.java
  - Make your own [custom search engine](https://www.google.com/cse/create/new)
    - Choose "Search any site" (but you have to pick a domain, maybe wikipedia.org would be good)
    - Edit the custom search you just made. In "Sites to search", change "Search only included sites" to "Search the entire web but emphasize included sites"
    - Get the search engine ID, put it in UserSpecificConstants.java
- For the scripts, which you do not need for simple queries:
  - psycopg2, which you can install with `pip install psycopg2`, or as python-psycopg2 in ubuntu and fedora

### Notes:
- Java 7 may suffice, but some of the code is being ported to Java 8 and soon the incompatibilities may merge into master. So beware.
- We once used SQLite but with many connections (200-500+), corruption seems to be a problem. We may make the SQL pluggable to avoid this extra setup step but probably not until someone requests it.
- The data is sizable and growing, especially for statistics reports.
- Can't find libindri-jni? Make sure you enabled Java and SWIG and had the right dependencies when compiling Indri.

## Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Examine the reference documentation](http://seantater.github.io/uncc2014watsonsim/)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
