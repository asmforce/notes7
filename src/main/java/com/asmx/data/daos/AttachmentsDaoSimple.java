package com.asmx.data.daos;

import com.asmx.data.entities.Attachment;
import com.asmx.data.entities.AttachmentFactory;
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
import java.util.List;

/**
 * User: asmforce
 * Timestamp: 29.11.15 15:10.
**/
public class AttachmentsDaoSimple extends Dao implements AttachmentsDao {
    private static final Logger logger = Logger.getLogger(AttachmentsDaoSimple.class);

    private AttachmentFactory attachmentFactory;
    private AttachmentMapper attachmentMapper = new AttachmentMapper();

    @Override
    public boolean checkAttachmentExists(User user, int noteId, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.queryForObject(
                "SELECT COUNT(*) > 0 FROM note_attachments WHERE user_id = ? AND note_id = ? AND id = ?",
                Boolean.class,
                user.getId(), noteId, id
            );
        } catch (DataAccessException e) {
            logger.error("Unable to check an attachment #" + id + " for note #" + noteId + " for existence (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public int createAttachment(User user, int noteId, Attachment attachment) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;
        assert attachment != null;
        assert attachment.getText() != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO note_attachments (id, user_id, note_id, text, comment, time) VALUES (DEFAULT, ?, ?, ?, ?, ?)",
                    new String[]{"id"}
                );
                ps.setInt(1, user.getId());
                ps.setInt(2, noteId);
                ps.setString(3, attachment.getText());
                ps.setString(4, attachment.getComment());
                ps.setTimestamp(5, DaoUtils.asTimestamp(attachment.getTime()));
                return ps;
            }, keyHolder);

            Number newId = keyHolder.getKey();
            return newId.intValue();
        } catch (DataAccessException e) {
            logger.error("Unable to create an attachment for note #" + noteId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public boolean changeAttachment(User user, int noteId, Attachment attachment) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;
        assert attachment != null;
        assert attachment.getId() > 0;
        assert attachment.getText() != null;

        JdbcTemplate template = getJdbcTemplate();
        try {
            int rows = template.update(
                "UPDATE note_attachments SET text = ?, comment = ?, time = ? WHERE user_id = ? AND note_id = ? AND id = ?",
                attachment.getText(), attachment.getComment(), attachment.getTime(),
                user.getId(), noteId, attachment.getId()
            );

            if (rows > 1) {
                throw new DataIntegrityViolationException("Multiple rows updated using a unique id");
            } else {
                return rows == 1;
            }
        } catch (DataAccessException e) {
            logger.error("Unable to change an attachment #" + attachment.getId() + " for note #" + noteId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    @Override
    public Attachment getAttachment(User user, int noteId, int id) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;
        assert id > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            List<Attachment> attachments = template.query(
                "SELECT * FROM note_attachments WHERE user_id = ? AND note_id = ? AND id = ?",
                attachmentMapper, user.getId(), noteId, id
            );

            if (CollectionUtils.isEmpty(attachments)) {
                logger.debug("An attachment #" + id + " for note #" + noteId + " (user #" + user.getId() + ") not exists");
            } else {
                if (attachments.size() == 1) {
                    return attachments.get(0);
                } else {
                    throw new DataIntegrityViolationException("An attachment #" + id + " for note #" + noteId + " (user #" + user.getId() + ") duplicated " + attachments.size() + " time(s)");
                }
            }
        } catch (DataAccessException e) {
            logger.error("Unable to get an attachment #" + id + " for note #" + noteId + " (user #" + user.getId() + ")");
            throw e;
        }
        return null;
    }

    @Override
    public List<Attachment> getNoteAttachments(User user, int noteId) {
        assert user != null;
        assert user.getId() > 0;
        assert noteId > 0;

        JdbcTemplate template = getJdbcTemplate();
        try {
            return template.query(
                "SELECT * FROM note_attachments WHERE user_id = ? AND note_id = ? ORDER BY id",
                attachmentMapper, user.getId(), noteId
            );
        } catch (DataAccessException e) {
            logger.error("Unable to get an attachments for note #" + noteId + " (user #" + user.getId() + ")");
            throw e;
        }
    }

    protected class AttachmentMapper implements RowMapper<Attachment> {
        @Override
        public Attachment mapRow(ResultSet row, int index) throws SQLException {
            Attachment attachment = attachmentFactory.create();
            attachment.setId(row.getInt("id"));
            attachment.setText(row.getString("text"));
            attachment.setComment(row.getString("comment"));
            attachment.setTime(DaoUtils.asDate(row.getTimestamp("time")));
            return attachment;
        }
    }

    @Required
    public void setAttachmentFactory(AttachmentFactory attachmentFactory) {
        this.attachmentFactory = attachmentFactory;
    }
}
