#!/usr/bin/env python3
# Before you start, run:
# -> pip install clint
import argparse
import sqlite3
import bs4
from clint.textui import progress
from multiprocessing.pool import Pool

parser = argparse.ArgumentParser(description="Import TREC data into sqlite3")
parser.add_argument("-c", "--create", action="store_true", help="Clear/Init Database")
parser.add_argument("-t", "--table", default="documents", help="SQL table to dump into")
parser.add_argument("db", help="SQLite database")
parser.add_argument("trec", nargs="+", help="Input TREC files")
args = parser.parse_args()

db = sqlite3.connect(args.db)
if args.create:
  db.execute("drop table if exists %s;" %args.table)
  db.execute("create table %s (jid text primary key, title text, text text);" %args.table)

def parse_one(fname):
    with open(fname) as f:
      b = bs4.BeautifulSoup(f)
      # find("text") is there because .text is ambiguous
      # the if d.find... is there because many entries are missing text
      return [
        [d.docno.text, d.title.text, d.find("text") and d.find("text").text or None]
        for d in b.find_all("doc")]

p = Pool(maxtasksperchild=100)
for entries in progress.bar(p.imap_unordered(parse_one, args.trec, 10), "Importing TREC data..", 50, expected_size=len(args.trec)):
  db.executemany("insert into %s (jid, title, text) values (?,?,?);" %args.table, entries)
  db.commit()
p.close()
p.join()
db.close()