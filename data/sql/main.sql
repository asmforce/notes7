------------------------------------------------------------------------------------------------------------------------
-- USERS AND CREDENTIALS
------------------------------------------------------------------------------------------------------------------------

CREATE SEQUENCE users_seq;
CREATE TABLE users (
  id INTEGER NOT NULL DEFAULT NEXTVAL('users_seq'),
  name VARCHAR(50) UNIQUE NOT NULL,
  key VARCHAR(256) NOT NULL,
  language VARCHAR(2) NOT NULL,
  timezone VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);


------------------------------------------------------------------------------------------------------------------------
-- NOTES HIERARCHY
------------------------------------------------------------------------------------------------------------------------

--
-- Тека (категорія, простір).
-- Призначена для впорядкування ланцюжків.
-- Один ланцюжок може належати одночасно до різних тек.
--

CREATE SEQUENCE space_seq;
CREATE TABLE spaces (
  id INTEGER NOT NULL DEFAULT NEXTVAL('space_seq'),
  user_id INTEGER NOT NULL REFERENCES users(id),
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE (user_id, name)
);


--
-- Ланцюжок нотаток.
-- Може бути прив’язаний до теки чи кількох. Може не бути прив’язаним.
-- Теги та ключові слова прив’язані саме до ланцюжка, а не до окремої нотатки.
-- Нотатки всередині ланцюжка можуть бути впорядковані лише за ознаками їх власної хронології (наприклад, в порядку додавання нотаток до ланцюжка).
--

CREATE SEQUENCE chain_seq;
CREATE TABLE chains (
  id INTEGER NOT NULL DEFAULT NEXTVAL('chain_seq'),
  user_id INTEGER NOT NULL REFERENCES users(id),
  PRIMARY KEY (id)
);


--
-- Прив’язка ланцюжків до тек.
--

CREATE TABLE chain_bindings (
  user_id INTEGER NOT NULL REFERENCES users(id),
  space_id INTEGER NOT NULL REFERENCES spaces(id),
  chain_id INTEGER NOT NULL REFERENCES chains(id),
  UNIQUE (user_id, space_id, chain_id)
);


--
-- Власне, нотатка.
-- Обов’язково має бути прив’язана до ланцюжка (навіть якщо той ланцюжок містить лише цю єдину нотатку).
--

CREATE SEQUENCE note_seq;
CREATE TABLE notes (
  id INTEGER NOT NULL DEFAULT NEXTVAL('note_seq'),
  user_id INTEGER NOT NULL REFERENCES users(id),
  chain_id INTEGER NOT NULL REFERENCES chains(id),
  text TEXT NOT NULL,
  idea_time TIMESTAMPTZ NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id)
);


--
-- Посилання з нотаток одна на одну.
-- Зазвичай посилання має зворотню хронологію (з пізнішої нотатки на більш ранню), але це не обов’язково.
--

CREATE TABLE note_relations (
  user_id INTEGER NOT NULL REFERENCES users(id),
  source_id INTEGER NOT NULL REFERENCES notes(id),
  target_id INTEGER NOT NULL REFERENCES notes(id),
  UNIQUE (user_id, source_id, target_id)
);


--
-- Вкладення. Прив’язане до нотатки (не до ланцюжка).
-- Має дату, текстове тіло (власне, вкладення) та коментар.
--

CREATE SEQUENCE note_attachment_seq;
CREATE TABLE note_attachments (
  id INTEGER NOT NULL DEFAULT NEXTVAL('note_attachment_seq'),
  user_id INTEGER NOT NULL REFERENCES users(id),
  note_id INTEGER NOT NULL REFERENCES notes(id),
  text TEXT NOT NULL,
  comment TEXT NULL,
  PRIMARY KEY (id)
);


------------------------------------------------------------------------------------------------------------------------
-- NOTES METADATA
------------------------------------------------------------------------------------------------------------------------

--
-- Інформація про час редагування нотатки.
--

CREATE SEQUENCE note_change_seq;
CREATE TABLE note_changes (
  id INTEGER NOT NULL DEFAULT NEXTVAL('note_change_seq'),
  user_id INTEGER NOT NULL REFERENCES users(id),
  note_id INTEGER NOT NULL REFERENCES notes(id),
  idea_time TIMESTAMPTZ NOT NULL,
  change_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id)
);


--
-- Теги до ланцюжків.
--

CREATE SEQUENCE tag_seq;
CREATE TABLE tags (
  id INTEGER NOT NULL DEFAULT NEXTVAL('tag_seq'),
  user_id INTEGER NOT NULL REFERENCES users(id),
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id, name)
);


CREATE TABLE tag_bindings (
  user_id INTEGER NOT NULL REFERENCES users(id),
  tag_id INTEGER NOT NULL REFERENCES tags(id),
  note_id INTEGER NOT NULL REFERENCES notes(id),
  UNIQUE (user_id, tag_id, note_id)
);


--
-- Ключові слова до ланцюжків.
--


CREATE SEQUENCE keyword_seq;
CREATE TABLE keywords (
  id INTEGER NOT NULL DEFAULT NEXTVAL('keyword_seq'),
  user_id INTEGER NOT NULL REFERENCES users(id),
  name VARCHAR(100) NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id, name)
);

CREATE TABLE keyword_bindings (
  user_id INTEGER NOT NULL REFERENCES users(id),
  keyword_id INTEGER NOT NULL REFERENCES keywords(id),
  note_id INTEGER NOT NULL REFERENCES notes(id),
  UNIQUE (user_id, keyword_id, note_id)
);
