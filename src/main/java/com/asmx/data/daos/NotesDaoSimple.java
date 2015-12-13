package com.asmx.data.daos;

import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import com.asmx.data.daos.errors.DataManagementException;
import com.asmx.data.entities.ChangeRecord;
import com.asmx.data.entities.ChangeRecordFactory;
import com.asmx.data.entities.Note;
import com.asmx.data.entities.NoteFactory;
import com.asmx.data.entities.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: asmforce
 * Timestamp: 01.08.15 14:58.
**/
public class NotesDaoSimple extends Dao implements NotesDao {
    private static final Logger logger = Logger.getLogger(NotesDaoSimple.class);

    private static final Sorting DEFAULT_SORTING = Sorting.sorted("id", false);
    private static final Sorting DEFAULT_CHAIN_SORTING = Sorting.sorted("idea_time", true);

    private NoteFactory noteFactory;
    private NoteMapper noteMapper = new NoteMapper();

    private ChangeRecordFactory changeRecordFactory;
    private ChangeMapper changeMapper = new ChangeMapper();

    @Override
    protected Map<String, String> getExpectedSortingCriteriaMap() {
        return new HashMap<String, String>() {{
            put("id", "n.id");
            put("idea_time", "n.idea_time");
            put("creation_time", "n.creation_time");
        }};
    }

    @Override
    public boolean checkChainExists(User user, int chainId) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM chains WHERE user_id = ? AND id = ?",
                Boolean.class,
                user.getId(), chainId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check a chain #" + chainId + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public int createChain(User user) {
        assert user != null;
        assert user.getId() > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO chains (id, user_id) VALUES (DEFAULT , ?)",
                    new String[]{"id"}
                );
                ps.setInt(1, user.getId());
                return ps;
            }, keyHolder);

            Number newId = keyHolder.getKey();
            return newId.intValue();
        } catch (DataAccessException e) {
            logger.error("Unable to create a chain (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkChainBindingExists(User user, int chainId, int spaceId) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;
        assert spaceId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM chain_bindings WHERE user_id = ? AND chain_id = ? AND space_id",
                Boolean.class,
                user.getId(), chainId, spaceId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check a chain #" + chainId + " binding to space #" + spaceId + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public void createChainBinding(User user, int chainId, int spaceId) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;
        assert spaceId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            template.update(
                "INSERT INTO chain_bindings (user_id, space_id, chain_id) VALUES (?, ?, ?)",
                user.getId(), spaceId, chainId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to create a chain #" + chainId + " binding to space #" + spaceId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean checkNoteExists(User user, int noteId) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM notes WHERE user_id = ? AND id = ?",
                Boolean.class,
                user.getId(), noteId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check a note #" + noteId + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Note> getNotes(User user, Pagination pagination, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;
        assert pagination != null;

        if (pagination.isEmpty()) {
            return Collections.emptyList();
        }

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT * FROM notes n WHERE user_id = ? " +
                getSortingClause(sorting, DEFAULT_SORTING) + " " +
                getPaginationClause(pagination),
                noteMapper, user.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get notes (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Note> getSpaceNotes(User user, int spaceId, Pagination pagination, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;
        assert spaceId > 0;
        assert pagination != null;

        if (pagination.isEmpty()) {
            return Collections.emptyList();
        }

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT n.* FROM notes n " +
                "JOIN chain_bindings cb ON n.chain_id = cb.chain_id AND n.user_id = cb.user_id " +
                "WHERE n.user_id = ? AND cb.space_id = ? " +
                getSortingClause(sorting, DEFAULT_SORTING) + " " +
                getPaginationClause(pagination),
                noteMapper, user.getId(), spaceId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get notes (space #" + spaceId + ", user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Note> getFreeSpaceNotes(User user, Pagination pagination, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;
        assert pagination != null;

        if (pagination.isEmpty()) {
            return Collections.emptyList();
        }

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT n.* FROM notes n LEFT OUTER JOIN chain_bindings cb ON n.chain_id = cb.chain_id " +
                "WHERE n.user_id = ? AND cb.space_id IS NULL " +
                getSortingClause(sorting, DEFAULT_SORTING) + " " +
                getPaginationClause(pagination),
                noteMapper, user.getId()
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get notes (user #" + user.getId() + ", not bounded to a space)");
            throw e;
        }
    }

    @Override
    public List<Note> getChainNotes(User user, int chainId) {
        assert user != null;
        assert user.getId() > 0;
        assert chainId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT * FROM notes n WHERE user_id = ? AND chain_id = ? " +
                getSortingClause(DEFAULT_CHAIN_SORTING),
                noteMapper, user.getId(), chainId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get notes (chain #" + chainId + ", user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<Note> getRelatedNotes(User user, RelationType relationType, int id, Pagination pagination, Sorting sorting) {
        assert user != null;
        assert user.getId() > 0;
        assert relationType != null;
        assert id > 0;
        assert pagination != null;

        if (pagination.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            switch (relationType) {
            case SOURCE:
                return getNotesByIds(
                    "SELECT target_id FROM note_relations WHERE source_id = ?",
                    user, pagination, sorting, id
                );

            case TARGET:
                return getNotesByIds(
                    "SELECT source_id FROM note_relations WHERE target_id = ?",
                    user, pagination, sorting, id
                );

            case ANY:
            default:
                return getNotesByIds(
                    "SELECT CASE WHEN source_id = ? THEN target_id ELSE source_id END FROM note_relations " +
                    "WHERE ? IN (source_id, target_id)",
                    user, pagination, sorting, id, id
                );
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get related notes (note #" + id + ", relation " + relationType.name() + ", user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public Note getNote(User user, int noteId) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Note> notes = template.query("SELECT * FROM notes WHERE user_id = ? AND id = ?", noteMapper, user.getId(), noteId);
            if (CollectionUtils.isEmpty(notes)) {
                logger.debug("A note #" + noteId + " (user #" + user.getId() + ") not exists");
            } else {
                if (notes.size() == 1) {
                    return notes.get(0);
                } else {
                    throw new DataIntegrityViolationException("A note #" + noteId + " (user #" + user.getId() + ") duplicated " + notes.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a note #" + noteId + " (user #" + user.getId() + ")");
            throw e;
        }
        return null;
    }

    @Override
    public int createNote(User user, Note note) {
        assert user != null;
        assert user.getId() > 0;
        assert note != null;
        assert note.getChainId() > 0;
        assert note.getText() != null;
        assert note.getIdeaTime() != null;
        assert note.getCreationTime() != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO notes (id, user_id, chain_id, text, idea_time, creation_time) " +
                    "VALUES (DEFAULT, ?, ?, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setInt(1, user.getId());
                ps.setInt(2, note.getChainId());
                ps.setString(3, note.getText());
                ps.setTimestamp(4, DaoUtils.asTimestamp(note.getIdeaTime()));
                ps.setTimestamp(5, DaoUtils.asTimestamp(note.getCreationTime()));
                return ps;
            }, keyHolder);

            int id = keyHolder.getKey().intValue();
            note.setId(id);

            return id;
        } catch (DataAccessException e) {
            logger.error("Unable to create a note (chain #" + note.getChainId() + ", user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public void changeNote(User user, int noteId, String text) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;
        assert text != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "UPDATE notes SET text = ? WHERE user_id = ? AND id = ?",
                text, user.getId(), noteId
            );

            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows updated using a unique id");
            }
            if (rows < 1) {
                throw new DataManagementException("The referenced note does not exist");
            }
        } catch (DataAccessException e) {
            logger.error("Unable to update a note #" + noteId + " (chain #" + noteId + ", user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public void createChangeRecord(User user, ChangeRecord change) {
        assert user != null;
        assert user.getId() > 0;
        assert change != null;
        assert change.getNoteId() > 0;
        assert change.getIdeaTime() != null;
        assert change.getChangeTime() != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO note_changes (user_id, note_id, idea_time, change_time) VALUES (?, ?, ?, ?)",
                    new String[]{"id"}
                );

                ps.setInt(1, user.getId());
                ps.setInt(2, change.getNoteId());
                ps.setTimestamp(3, DaoUtils.asTimestamp(change.getIdeaTime()));
                ps.setTimestamp(4, DaoUtils.asTimestamp(change.getChangeTime()));

                return ps;
            });
        } catch (DataAccessException e) {
            logger.error("Unable to create a change record for note #" + change.getNoteId() + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public List<ChangeRecord> getChangeRecords(User user, int noteId) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT * FROM note_changes WHERE user_id = ? AND note_id = ? " +
                "ORDER BY idea_time DESC",
                changeMapper, user.getId(), noteId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get a change history for note #" + noteId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    protected List<Note> getNotesByIds(String sqlSelectIds, User user, Pagination pagination, Sorting sorting, Object... parameters) {
        return getJdbcTemplate().query(
            "SELECT n.* FROM notes n WHERE user_id = ? AND id IN (" + sqlSelectIds + ") " +
            getSortingClause(sorting, DEFAULT_SORTING) + " " +
            getPaginationClause(pagination),
            noteMapper, user.getId(), parameters
        );
    }

    @Required
    public void setNoteFactory(NoteFactory noteFactory) {
        this.noteFactory = noteFactory;
    }

    @Required
    public void setChangeRecordFactory(ChangeRecordFactory changeRecordFactory) {
        this.changeRecordFactory = changeRecordFactory;
    }

    protected class NoteMapper implements RowMapper<Note> {
        @Override
        public Note mapRow(ResultSet row, int index) throws SQLException {
            Note note = noteFactory.create();
            note.setId(row.getInt("id"));
            note.setChainId(row.getInt("chain_id"));
            note.setText(row.getString("text"));
            note.setIdeaTime(DaoUtils.asDate(row.getTimestamp("idea_time")));
            note.setCreationTime(DaoUtils.asDate(row.getTimestamp("creation_time")));
            return note;
        }
    }

    protected class ChangeMapper implements RowMapper<ChangeRecord> {
        @Override
        public ChangeRecord mapRow(ResultSet row, int index) throws SQLException {
            ChangeRecord change = changeRecordFactory.create();
            change.setNoteId(row.getInt("note_id"));
            change.setIdeaTime(DaoUtils.asDate(row.getTimestamp("idea_time")));
            change.setChangeTime(DaoUtils.asDate(row.getTimestamp("change_time")));
            return change;
        }
    }
}
