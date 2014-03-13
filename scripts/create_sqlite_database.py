#!/usr/bin/env python3
import sqlite3
import argparse

parser = argparse.ArgumentParser(description="Create SQLite source database")
parser.add_argument("-t", "--table", default="documents", help="SQL table to dump into")
parser.add_argument("db", help="SQLite database")
args = parser.parse_args()

db = sqlite3.connect(args.db)
db.executescript("""
  pragma journal_mode = WAL;
  pragma synchronous = OFF;

  drop table if exists {table};
  create table {table} (
    docno text primary key,
    title text,
    text text,
    source text
  );
  create index if not exists documents_source on documents(source);

  drop table if exists excluded_{table};
  create table excluded_{table} (
    docno text primary key,
    title text,
    text text,
    source text
  );

  drop table if exists redirect_{table};
  create table redirect_{table} (
    source_title text,
    target_docid text,
    target_title text,
    foreign key(target_docid) references {table}(docid)
  );


  drop table if exists search_{table};
  create virtual table search_{table} using fts4 (
    content="{table}",
    tokenize="porter", 
    docno text primary key,
    title text,
    text text,
    source text);

  drop trigger if exists {table}_bu;
  create trigger {table}_bu before update on {table} begin
    delete from search_{table} where docid=old.rowid;
  end;
  drop trigger if exists {table}_bd;
  create trigger {table}_bd before delete on {table} begin
    delete from search_{table} where docid=old.rowid;
  end;

  drop trigger if exists {table}_au;
  create trigger {table}_au after update on {table} begin
    insert into search_{table}(docid, docno, title, text, source) values(new.rowid, new.docno, new.title, new.text, new.source);
  end;
  drop trigger if exists {table}_ai;
  create trigger {table}_ai after insert on {table} begin
    insert into search_{table}(docid, docno, title, text, source) values(new.rowid, new.docno, new.title, new.text, new.source);
  end;
    """.format(table=args.table))
