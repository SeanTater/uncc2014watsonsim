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
parser.add_argument("-c", "--create", action="store_true", help="Clear/Init Database")
parser.add_argument("-t", "--table", default="documents", help="SQL table to dump into")
parser.add_argument("db", help="SQLite database")
parser.add_argument("source", help="Source tag [e.g. wikipedia,wikiquotes,shakespeare ...]")
parser.add_argument("trec", nargs="+", help="Input TREC files")
args = parser.parse_args()

db = sqlite3.connect(args.db)
if args.create:
  db.executescript("""
    drop table if exists {table};
    create virtual table {table} using fts4 (
      tokenize="porter", 
      docno text unique,
      title text,
      content text,
      source text);
      """.format(table=args.table))

def parse_one(fname):
    with open(fname) as f:
      b = HTML(f.read()).findall("*doc")
      # find("text") is there because .text is ambiguous
      # the if d.find... is there because many entries are missing text
      return [
        [d.findtext("docno"), d.findtext("title"), d.findtext("text")]
        for d in b]

#p = Pool(multiprocessing.cpu_count()+1, maxtasksperchild=5)
#runner = p.imap_unordered(parse_one, args.trec, 10)
runner = (parse_one(x) for x in args.trec)
for entries in progress.bar(runner, "Importing TREC data..", 50, expected_size=len(args.trec)):
  db.executemany("insert into %s (docno, title, content, source) values (?,?,?,'%s');" %(args.table, args.source), entries)
  db.execute("insert into {table}({table}) values ('merge=200,8');".format(table=args.table)) # Clean search trees a bit
  db.commit()
p.close()
p.join()

# Clean the tree the last time. 
db.execute("insert into {table}({table}) values ('optimize');".format(table=args.table))
db.commit()
db.close()
