#!/usr/bin/env python3
# Before you start, run:
# -> pip install clint
import argparse
import sqlite3
from clint.textui import progress
import multiprocessing
from multiprocessing.pool import Pool
from lxml.etree import HTML
parser = argparse.ArgumentParser(description="Import TREC data into sqlite3")
parser.add_argument("-t", "--table", default="documents", help="SQL table to dump into")
parser.add_argument("db", help="SQLite database")
parser.add_argument("source", help="Source tag [e.g. wikipedia,wikiquotes,shakespeare ...]")
parser.add_argument("trec", nargs="+", help="Input TREC files")
args = parser.parse_args()

db = sqlite3.connect(args.db)
db.executescript("""
  pragma journal_mode = WAL;
  pragma synchronous = OFF;""")


for i, fname in progress.bar(enumerate(args.trec), "Importing TREC data..", 50, expected_size=len(args.trec)):
  with open(fname) as f:
    b = HTML(f.read()).findall("*doc")
    entries = [
      [d.findtext("docno"), d.findtext("title"), d.findtext("text")]
      for d in b]

  db.executemany("insert or replace into %s (docno, title, text, source) values (?,?,?,'%s');" %(args.table, args.source), entries)
  if not (i % 250):
      db.execute("insert into search_{table}(search_{table}) values ('merge=200,8');".format(table=args.table)) # Clean search trees a bit
      db.commit()

# Clean the tree the last time. 
#db.execute("insert into search_{table}(search_{table}) values ('optimize');".format(table=args.table))
db.commit()
db.close()
