/* These two settings result in a 2x to 50x speedup for SQLite
 * If you are concerned, you can use synchronous = NORMAL
 * Remember that btrfs does not actually obey fsync so this has less of an
 * impact with btrfs than others and it will seem pretty fast either way.
 */
PRAGMA journal_mode = WAL;
PRAGMA synchronous = OFF;
PRAGMA foreign_keys = ON;

/*
 * meta and content are separate because the content is very large and makes
 * routine changes slower otherwise.
 */
CREATE TABLE meta (
    id INTEGER PRIMARY KEY,
    title TEXT,
    source TEXT,
    reference TEXT,
    pageviews INTEGER
);

CREATE TABLE content (
    id INTEGER PRIMARY KEY,
    text TEXT,
    FOREIGN KEY(id) REFERENCES meta(id)
);

CREATE INDEX meta_source ON meta(source);
CREATE INDEX meta_title ON meta(title);

CREATE TABLE redirects (
    target_id INTEGER,
    source_title TEXT,
    FOREIGN KEY(target_id) REFERENCES meta(id)
);
CREATE INDEX redirects_id ON redirects(target_id);

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

-- merged from questions.db
CREATE TABLE results (question int, rank int, score double, engine text, title text, fulltext text, correct boolean, reference text);
CREATE TABLE questions (rowid int primary key, question text, answer text, category text);
CREATE INDEX results_fkey_question ON  results(question);
CREATE TABLE cache (
    query TEXT,
    engine TEXT,
    title TEXT,
    fulltext TEXT,
    reference TEXT,
    id BIGINT,
    created_on INTEGER DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX cache_query ON cache(query);
CREATE TABLE cache_scores(passage_id int, name text, value float);
CREATE INDEX cache_scores_passage_id ON cache_scores(passage_id);
CREATE INDEX cache_query_engine ON cache(query, engine);
CREATE INDEX cache_timestamp ON cache(created_on);
