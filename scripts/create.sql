/* These two settings result in a 2x to 50x speedup for SQLite
 * If you are concerned, you can use synchronous = NORMAL
 * Remember that btrfs does not actually obey fsync so this has less of an
 * impact with btrfs than others and it will seem pretty fast either way.
 */
PRAGMA journal_mode = WAL;
PRAGMA synchronous = OFF;

/* This is the main document store
 * It may be worthwhile to separate text from the other fields as it is very
 * large and slows down full scans
 */
DROP TABLE IF EXISTS documents;
CREATE TABLE documents (
    docno TEXT PRIMARY KEY,
    title TEXT,
    text TEXT,
    source TEXT
);
CREATE INDEX IF NOT EXISTS documents_source ON documents(source);
CREATE INDEX IF NOT EXISTS documents_title ON documents(title);

DROP TABLE IF EXISTS redirect_documents;
CREATE TABLE redirect_documents (
    source_title TEXT,
    target_docid TEXT,
    target_title TEXT,
    FOREIGN KEY(target_docid) REFERENCES documents(docid)
);
CREATE INDEX IF NOT EXISTS redirect_documents_tdocid ON redirect_documents(target_docid);

-- Used for an experimental scorer. (PhraseTokens)
-- Should it be moved to another DB?
CREATE TABLE relate_words(
    id INTEGER PRIMARY KEY,
    name TEXT UNIQUE,
    count INTEGER
);
CREATE TABLE relate_links(
    id INTEGER PRIMARY KEY,
    source INTEGER,
    dest INTEGER,
    count INTEGER,
    FOREIGN KEY(source) REFERENCES relate_words(id),
    FOREIGN KEY(dest) REFERENCES relate_words(id),
    UNIQUE(source, dest)
);
