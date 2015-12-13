------------------------------------------------------------------------------------------------------------------------
-- USERS AND CREDENTIALS
------------------------------------------------------------------------------------------------------------------------

CREATE SEQUENCE users_seq;
CREATE TABLE users (
  id INTEGER NOT NULL DEFAULT NEXTVAL('users_seq'),
  name VARCHAR(50) NOT NULL,
  key VARCHAR(256) NOT NULL,
  language VARCHAR(2) NOT NULL,
  timezone VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);


------------------------------------------------------------------------------------------------------------------------
-- NOTES HIERARCHY
------------------------------------------------------------------------------------------------------------------------

--
-- Тека (категорія, простір).
-- Призначена для впорядкування ланцюжків.
-- Один ланцюжок може належати одночасно до різних тек.
-- Назва теки є унікальною — користувач не може створити кілька тек з одним іменем.
--

CREATE SEQUENCE space_seq;
CREATE TABLE spaces (
  id INTEGER NOT NULL DEFAULT NEXTVAL('space_seq'),
  user_id INTEGER NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id, name),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
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
  user_id INTEGER NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


--
-- Прив’язка ланцюжків до тек.
--

CREATE TABLE chain_bindings (
  user_id INTEGER NOT NULL,
  space_id INTEGER NOT NULL,
  chain_id INTEGER NOT NULL,
  UNIQUE (user_id, space_id, chain_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (space_id) REFERENCES spaces(id) ON DELETE CASCADE,
  FOREIGN KEY (chain_id) REFERENCES chains(id) ON DELETE CASCADE
);


--
-- Власне, нотатка.
-- Обов’язково має бути прив’язана до ланцюжка (навіть якщо той ланцюжок містить лише цю єдину нотатку).
--

CREATE SEQUENCE note_seq;
CREATE TABLE notes (
  id INTEGER NOT NULL DEFAULT NEXTVAL('note_seq'),
  user_id INTEGER NOT NULL,
  chain_id INTEGER NOT NULL,
  text TEXT NOT NULL,
  idea_time TIMESTAMPTZ NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (chain_id) REFERENCES chains(id) ON DELETE CASCADE
);


--
-- Посилання з нотаток одна на одну.
-- Зазвичай посилання має зворотню хронологію (з пізнішої нотатки на більш ранню), але це не обов’язково.
--

CREATE TABLE note_relations (
  user_id INTEGER NOT NULL,
  source_id INTEGER NOT NULL,
  target_id INTEGER NOT NULL,
  UNIQUE (user_id, source_id, target_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (source_id) REFERENCES notes(id) ON DELETE CASCADE,
  FOREIGN KEY (target_id) REFERENCES notes(id) ON DELETE CASCADE
);


------------------------------------------------------------------------------------------------------------------------
-- EXTRA DATA
------------------------------------------------------------------------------------------------------------------------

--
-- Інформація про час редагування нотатки.
--

CREATE TABLE note_changes (
  user_id INTEGER NOT NULL,
  note_id INTEGER NOT NULL,
  idea_time TIMESTAMPTZ NOT NULL,
  change_time TIMESTAMPTZ NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE
);


--
-- Вкладення. Прив’язане до нотатки (не до ланцюжка).
-- Має дату, текстове тіло (власне, вкладення) та коментар.
--

CREATE SEQUENCE note_attachment_seq;
CREATE TABLE note_attachments (
  id INTEGER NOT NULL DEFAULT NEXTVAL('note_attachment_seq'),
  user_id INTEGER NOT NULL,
  note_id INTEGER NOT NULL,
  text TEXT NOT NULL,
  comment TEXT NULL,
  time TIMESTAMPTZ NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE
);


--
-- Теги та їх метадані.
--

CREATE SEQUENCE tag_seq;
CREATE TABLE tags (
  id INTEGER NOT NULL DEFAULT NEXTVAL('tag_seq'),
  user_id INTEGER NOT NULL,
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id, name),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


--
-- Прив’язка тегів до ланцюжків.
--

CREATE TABLE tag_bindings (
  user_id INTEGER NOT NULL,
  tag_id INTEGER NOT NULL,
  chain_id INTEGER NOT NULL,
  UNIQUE (user_id, tag_id, chain_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
  FOREIGN KEY (chain_id) REFERENCES chains(id) ON DELETE CASCADE
);


--
-- Ключові слова та їх метадані.
--


CREATE SEQUENCE keyword_seq;
CREATE TABLE keywords (
  id INTEGER NOT NULL DEFAULT NEXTVAL('keyword_seq'),
  user_id INTEGER NOT NULL,
  name VARCHAR(100) NOT NULL,
  creation_time TIMESTAMPTZ NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (user_id, name),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


--
-- Прив’язка ключових слів до ланцюжків.
--

CREATE TABLE keyword_bindings (
  user_id INTEGER NOT NULL,
  keyword_id INTEGER NOT NULL,
  chain_id INTEGER NOT NULL,
  UNIQUE (user_id, keyword_id, chain_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (keyword_id) REFERENCES keywords(id) ON DELETE CASCADE,
  FOREIGN KEY (chain_id) REFERENCES chains(id) ON DELETE CASCADE
);
