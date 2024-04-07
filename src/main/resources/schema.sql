CREATE SCHEMA IF NOT EXISTS "public";

CREATE TABLE IF NOT EXISTS "public".authors (
    author_id serial NOT NULL,
    author_name varchar(255) NOT NULL,
    CONSTRAINT authors_pkey PRIMARY KEY (author_id)
);

CREATE TABLE IF NOT EXISTS "public".books (
    book_id serial NOT NULL,
    book_title varchar(255) NOT NULL,
    author_id integer NOT NULL,
    CONSTRAINT books_pkey PRIMARY KEY (book_id),
    CONSTRAINT author_id FOREIGN KEY (author_id) REFERENCES "public".authors(author_id)
);

CREATE TABLE IF NOT EXISTS "public".users (
    user_id serial NOT NULL,
    user_name varchar(255) NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS "public".comments (
    comment_id serial NOT NULL,
    user_id integer NOT NULL,
    book_id integer NOT NULL,
    text varchar(500) NOT NULL,
    CONSTRAINT comments_pkey PRIMARY KEY (comment_id),
    CONSTRAINT user_id FOREIGN KEY (user_id) REFERENCES "public".users(user_id),
    CONSTRAINT book_id FOREIGN KEY (book_id) REFERENCES "public".books(book_id)
);

CREATE TABLE IF NOT EXISTS "public".user_books (
    user_id integer NOT NULL,
    book_id integer NOT NULL,
    CONSTRAINT user_book PRIMARY KEY (user_id, book_id),
    CONSTRAINT user_id FOREIGN KEY (user_id) REFERENCES "public".users(user_id),
    CONSTRAINT book_id FOREIGN KEY (book_id) REFERENCES "public".books(book_id)
);