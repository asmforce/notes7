package com.asmx.data.daos;

import com.asmx.data.entities.Attachment;
import com.asmx.data.entities.User;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 29.11.15 15:10.
**/
public interface AttachmentsDao {
    boolean checkAttachmentExists(User user, int noteId, int id);
    int createAttachment(User user, int noteId, Attachment attachment);
    boolean changeAttachment(User user, int noteId, Attachment attachment);
    Attachment getAttachment(User user, int noteId, int id);
    List<Attachment> getNoteAttachments(User user, int noteId);
}
