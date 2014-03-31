#!/usr/bin/env python3
# Before you start, run:
# -> pip install clint
import argparse
import sqlite3
import bz2
import code
from threading import Thread
from clint.textui import progress
from lxml import etree
parser = argparse.ArgumentParser(description="Import TREC data into sqlite3")
parser.add_argument("-t", "--table", default="documents", help="SQL table to dump into")
parser.add_argument("db", help="SQLite database")
parser.add_argument("xmlbz2", help="Input bzipped wikipedia xml file")
args = parser.parse_args()



def ns(tag):
  # Convenience method to tack on the namespace.
  # TODO: Find a better way
  return "{http://www.mediawiki.org/xml/export-0.8/}" + tag

# This can be one line but it gets really long...
gen = bz2.BZ2File(args.xmlbz2)
gen = etree.iterparse(gen, tag=ns("page"))
gen = enumerate(gen)
gen = progress.bar(gen, label="Importing Wikipedia..", width=50, expected_size=4465000)

pages = []
redirects = []
background = None

def commit(page_index, pages, redirects):
  db = sqlite3.connect(args.db)
  db.executescript("""
    -- Finish commits faster: truncate instead of delete.
    pragma journal_mode = WAL;
    -- Don't wait for the disk
    pragma synchronous = OFF;
    -- Enabling foreign keys means we would have to pend many redirects
    -- That would take too much memory
    pragma foreign_keys = OFF;
  """)
  db.executemany("insert or replace into redirect_{table}(source_title, target_docid, target_title) values (?, ?, ?);".format(table=args.table), redirects)
  #db.executemany("insert into {table} (docno, title, text, source) values (?,?,?,'wikipedia');".format(table=args.table), pages)
  #if not (page_index % 100000):
  #  db.execute("insert into search_{table}(search_{table}) values ('merge=200,8');".format(table=args.table)) # Clean search trees a bit
  db.commit()
  db.close()

for page_index, (event, page) in gen:
  redirect = page.xpath("*[local-name()='redirect']/@title")
  title = page.findtext(ns("title")).strip()
  if redirect:
    redirect = redirect[0].strip()
    redirects.append([
      title,   # Source title
      # Target docid
      "wikipedia-full-text-{target_title}".format(target_title=redirect),
      redirect # Target title
    ])
  #else: 
  #  pages.append([
  #    "wikipedia-full-text-{title}".format(title=title), # docno
  #    title,
  #    page.findtext(ns("revision") + "/" + ns("text")) # text
  #  ])
  if not (page_index % 10000):
    if background:
      background.join()
    background = Thread(target=commit, args=(page_index,pages,redirects))
    background.start()
    pages = []
    redirects = []
  page.clear()
  del page

# Clean the tree the last time.
# It takes a long time and we don't do that many searches anyway.
# db.execute("insert into search_{table}(search_{table}) values ('optimize');".format(table=args.table))
