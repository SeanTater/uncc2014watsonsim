uncc2014watsonsim [![Build Status](https://travis-ci.org/SeanTater/uncc2014watsonsim.png?branch=master)](https://travis-ci.org/SeanTater/uncc2014watsonsim)
======

Deep Question Answering System

## [Updates and Progress on Development](http://watsonsim.blogspot.com)

## Get started
- `git clone https://github.com/SeanTater/uncc2014watsonsim.git`
- Download and unzip [gradle](http://gradle.org/downloads) somewhere convenient to call from the command line. (Most Linux distros have a packaged version that may be easier, but may also be out of date.)
- Install Java 7 or newer. Many Macs have only Java 6 by default.
- Install Indri binaries:
  - For Linux 64, pick the right library in uncc2012watsonsim/lib and copy it to libindri-jni.so
  - For Windows 64, do the same but end it in .dll
  - For others, or if the above doesn't work, compile indri on your own and copy `libindri-jni.so` or `libindri-jni.dll` to uncc2014watsonsim/lib.
- Setup UserSpecificConstants (in src/main/java)
  - Make a copy of the file without .sample at the end of the filename
  - Make your own Google cloud app in the [Google console](https://cloud.google.com/console).
    - Put the name into the source.
    - Enable the Custom Search API
    - Create a server Public API Key, put it in the source.
  - Make your own custom search engine in the [Custom Search Console](https://www.google.com/cse/create/new)
    - Search any site (but you have to pick a domain, maybe wikipedia.org would be good)
    - Edit the custom search you just made. In "Sites to search", change "Search only included sites" to "Search the entire web but emphasize included sites"
    - Get the search engine ID, put it in the source.
- Have Gradle setup gobs of other stuff
  - `/where/you/unzipped/gradle/bin/gradle cleanEclipse eclipse assemble`

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

## Get the data
- We parsed [the full Wikipedia](https://www.dropbox.com/s/hpse3kxsi5or5ba/wikipedia-full-paragraphs-trec.xml.xz?dl=0) as of October 2014 into TREC format, which may be very helpful for indexing. Note that we index one paragraph at a time rather than one article at a time, so the titles are not unique. We will submit an article-by-article format as well if that proves to be helpful.
- Work is ongoing to place this in an SQLite database so that you do not need to index it yourself. We are working on using Lucene, Indri and Terrier for this.

## Tools

- [Check to see if your commit broke the code](https://travis-ci.org/SeanTater/uncc2014watsonsim)
- [Examine the reference documentation](http://seantater.github.io/uncc2014watsonsim/)
- [Find out how much better your code works than the last commit](http://watsonsim.herokuapp.com/runs)
