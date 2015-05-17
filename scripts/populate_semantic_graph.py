import sqlite3
import leveldb
ldb = leveldb.LevelDB("data/edges-leveldb")
sdb = sqlite3.connect("sources.db")
block=[]
for k, v in ldb.RangeIter():
    block.append(k.decode("utf8").split("\t", 2) + [int(v.decode("utf8"))])
    if len(block) > 1000000:
        s = sdb.executemany("INSERT INTO semantic_graph(source, tag, target, count) VALUES (?, ?, ?, ?);", block);
        print('.', end='')
        block=[]
        sdb.commit()