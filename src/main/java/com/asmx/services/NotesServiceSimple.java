package com.asmx.services;

import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import com.asmx.data.daos.NotesDao;
import com.asmx.data.daos.SpacesDao;
import com.asmx.data.entities.Note;
import com.asmx.data.entities.Space;
import com.asmx.data.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 22.06.15 1:11.
**/
@Service
public class NotesServiceSimple implements NotesService {
    @Autowired
    private SpacesDao spacesDao;
    @Autowired
    private NotesDao notesDao;

    @Override
    public List<Space> getSpaces(User user) {
        return spacesDao.getSpaces(user, null);
    }

    @Override
    public List<Space> getSpaces(User user, Sorting sorting) {
        return spacesDao.getSpaces(user, sorting);
    }

    @Override
    public Space getSpace(User user, int id) {
        return spacesDao.getSpace(user, id);
    }

    @Override
    public Note getNote(User user, int id) {
        return notesDao.getNote(user, id);
    }

    @Override
    public List<Note> getNotes(User user, Pagination pagination) {
        return notesDao.getNotes(user, pagination, null);
    }

    @Override
    public List<Note> getNotes(User user, Pagination pagination, Sorting sorting) {
        return notesDao.getNotes(user, pagination, sorting);
    }
}
