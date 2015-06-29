package com.asmx.services;

import com.asmx.data.daos.SpacesDao;
import com.asmx.data.entities.Space;
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

    @Override
    public List<Space> getSpaces(int userId) {
        return spacesDao.getSpaces(userId);
    }

    @Override
    public Space getSpace(int userId, int id) {
        return spacesDao.getSpace(userId, id);
    }
}
