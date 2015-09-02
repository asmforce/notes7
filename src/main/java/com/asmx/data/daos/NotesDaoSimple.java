package com.asmx.data.daos;

import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
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

    private NoteFactory noteFactory;
    private NoteMapper noteMapper = new NoteMapper();

    @Override
    protected Map<String, String> getExpectedSortingCriteriaMap() {
        return new HashMap<String, String>() {{
            put("id", "n.id");
            put("idea_time", "n.idea_time");
            put("creation_time", "n.creation_time");
        }};
    }

    @Override
    public List<Note> getNotes(User user, Pagination pagination, Sorting sorting) {
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
        assert user.getId() > 0;
        assert spaceId >= 0;
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
            logger.error("Unable to get notes (user #" + user.getId() + ", space #" + spaceId + ")");
            throw e;
        }
    }

    @Override
    public List<Note> getFreeSpaceNotes(User user, Pagination pagination, Sorting sorting) {
        assert user.getId() > 0;
        assert pagination != null;

        if (pagination.isEmpty()) {
            return Collections.emptyList();
        }

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                    "SELECT n.* FROM notes n " +
                    "LEFT OUTER JOIN chain_bindings cb ON n.chain_id = cb.chain_id " +
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
        assert user.getId() > 0;
        assert chainId >= 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query("SELECT * FROM notes WHERE user_id = ? AND chain_id = ?", noteMapper, user.getId(), chainId);
        } catch (DataAccessException e) {
            logger.error("Unable to get notes (chain #" + chainId + ", user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public Note getNote(User user, int id) {
        assert user.getId() > 0;
        assert id >= 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Note> notes = template.query("SELECT * FROM notes WHERE user_id = ? AND id = ?", noteMapper, user.getId(), id);
            if (CollectionUtils.isEmpty(notes)) {
                logger.debug("A note #" + id + " (user #" + user.getId() + ") not exists");
            } else {
                if (notes.size() == 1) {
                    return notes.get(0);
                } else {
                    throw new DataIntegrityViolationException("A note #" + id + " (user #" + user.getId() + ") duplicated " + notes.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get a note #" + id + " (user #" + user.getId() + ")");
            throw e;
        }
        return null;
    }

    @Override
    public boolean putNote(User user, Note note) {
        assert user.getId() > 0;
        assert note.getId() >= 0;
        assert note.getChainId() > 0;
        assert note.getText() != null;
        assert note.getIdeaTime() != null;
        assert note.getCreationTime() != null;

        JdbcTemplate template = getJdbcTemplate();
        if (note.getId() == GENERATE_ID) {
            try {
                GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
                template.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO notes (id, user_id, chain_id, text, idea_time, creation_time) VALUES (DEFAULT, ?, ?, ?, ?, ?)",
                            new String[]{"id"}
                    );
                    ps.setInt(1, user.getId());
                    ps.setInt(2, note.getChainId());
                    ps.setString(3, note.getText());
                    ps.setTimestamp(4, asTimestamp(note.getIdeaTime()));
                    ps.setTimestamp(5, asTimestamp(note.getCreationTime()));
                    return ps;
                }, keyHolder);

                Number newId = keyHolder.getKey();
                note.setId(newId.intValue());
                return true;
            } catch (DataAccessException e) {
                logger.error("Unable to insert note (chain #" + note.getChainId() + ", user #" + user.getId() + ")");
                throw e;
            }
        } else {
            try {
                int rows = template.update(
                        "UPDATE notes SET chain_id = ?, text = ?, idea_time = ?, creation_time = ? WHERE id = ? AND user_id = ?",
                        note.getChainId(),
                        note.getText(),
                        note.getIdeaTime(),
                        note.getCreationTime(),
                        note.getId(),
                        user.getId()
                );

                return rows >= 1;
            } catch (DataAccessException e) {
                logger.error("Unable to update note #" + note.getId() + " (chain #" + note.getChainId() + ", user #" + user.getId() + ")");
                throw e;
            }
        }
    }

    @Required
    public void setNoteFactory(NoteFactory noteFactory) {
        this.noteFactory = noteFactory;
    }

    protected class NoteMapper implements RowMapper<Note> {
        @Override
        public Note mapRow(ResultSet row, int index) throws SQLException {
            Note note = noteFactory.create();
            note.setId(row.getInt("id"));
            note.setChainId(row.getInt("chain_id"));
            note.setText(row.getString("text"));
            note.setIdeaTime(asDate(row.getTimestamp("idea_time")));
            note.setCreationTime(asDate(row.getTimestamp("creation_time")));
            return note;
        }
    }
}