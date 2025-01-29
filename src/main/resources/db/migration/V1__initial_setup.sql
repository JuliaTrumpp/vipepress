CREATE TABLE ARTICLE (
    title VARCHAR(500) PRIMARY KEY,
    author VARCHAR(150),
    description VARCHAR(500),
    published_on DATE,
    content TEXT,
    source VARCHAR(100),
    sentiment_score FLOAT,
    sentiment_magnitude FLOAT
);

CREATE TABLE REDDIT_POST (
    link VARCHAR(500) PRIMARY KEY,
    title VARCHAR(500),
    content TEXT,
    author VARCHAR(50),
    published_on DATE,
    ups INTEGER,
    subreddit VARCHAR(150),
    sentiment_score FLOAT,
    sentiment_magnitude FLOAT
);

CREATE TABLE REDDIT_COMMENT (
    link VARCHAR(255) PRIMARY KEY,
    content TEXT,
    author VARCHAR(50),
    published_on DATE,
    sentiment_score FLOAT,
    sentiment_magnitude FLOAT
);

CREATE TABLE PROJECT_USER (
    name VARCHAR(80) PRIMARY KEY,
    sources VARCHAR(80)[]
);

CREATE TABLE COUNTRY (
    short VARCHAR(20) PRIMARY KEY,
    name VARCHAR(255),
    sources VARCHAR(80)[]
);

CREATE TABLE ARTICLE_REDDIT_POST (
    article_title VARCHAR(500),
    reddit_post_link VARCHAR(255),
    PRIMARY KEY (article_title, reddit_post_link),
    FOREIGN KEY (article_title) REFERENCES ARTICLE(title) ON DELETE CASCADE,
    FOREIGN KEY (reddit_post_link) REFERENCES REDDIT_POST(link) ON DELETE CASCADE
);

CREATE TABLE USER_SAVED_ARTICLE (
    user_name VARCHAR(80),
    article_title VARCHAR(500),
    PRIMARY KEY (user_name, article_title),
    FOREIGN KEY (user_name) REFERENCES PROJECT_USER(name) ON DELETE CASCADE,
    FOREIGN KEY (article_title) REFERENCES ARTICLE(title) ON DELETE CASCADE
);

CREATE TABLE COUNTRY_ARTICLE (
    country_short VARCHAR(80),
    article_title VARCHAR(500),
    PRIMARY KEY (country_short, article_title),
    FOREIGN KEY (country_short) REFERENCES COUNTRY(short) ON DELETE CASCADE,
    FOREIGN KEY (article_title) REFERENCES ARTICLE(title) ON DELETE CASCADE
);

