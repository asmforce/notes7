package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Tag;
import com.asmx.data.entities.User;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 19.12.15 21:58.
**/
public interface TagsDao {
    boolean checkTagExists(User user, int id);
    boolean checkNameInUse(User user, String name);
    boolean checkTagInUse(User user, int id);
    int createTag(User user, Tag tag);
    void changeTag(User user, int id, String name, String description);
    boolean deleteTag(User user, int id);
    List<Tag> getTags(User user, Sorting sorting);
    Tag getTag(User user, int id);
}
