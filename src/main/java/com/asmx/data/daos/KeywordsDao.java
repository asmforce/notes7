package com.asmx.data.daos;

import com.asmx.data.Sorting;
import com.asmx.data.entities.Keyword;
import com.asmx.data.entities.User;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 20.12.15 0:10.
**/
public interface KeywordsDao {
    boolean checkKeywordExists(User user, int id);
    boolean checkNameInUse(User user, String name);
    boolean checkKeywordInUse(User user, int id);
    int createKeyword(User user, Keyword tag);
    void changeKeyword(User user, int id, String name);
    boolean deleteKeyword(User user, int id);
    List<Keyword> getKeywords(User user, Sorting sorting);
    Keyword getKeyword(User user, int id);
}
