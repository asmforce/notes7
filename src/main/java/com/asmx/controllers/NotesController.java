package com.asmx.controllers;

import com.asmx.controllers.data.Authorized;
import com.asmx.controllers.data.entities.GenericResponseJson;
import com.asmx.controllers.data.entities.PaginationJson;
import com.asmx.controllers.data.entities.SortingJson;
import com.asmx.controllers.errors.ForgedRequestException;
import com.asmx.data.Pagination;
import com.asmx.data.Sorting;
import com.asmx.data.entities.Note;
import com.asmx.data.entities.User;
import com.asmx.services.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * User: asmforce
 * Timestamp: 30.08.15 21:41.
**/
@Controller
public class NotesController extends ControllerBase {
    public static final int MAX_PAGINATION_SIZE = 100;

    @Autowired
    private NotesService notesService;

    @RequestMapping("/notes")
    public ModelAndView notes(@Authorized User user) {
        final Pagination pagination = pagination(0, MAX_PAGINATION_SIZE);
        return new ModelAndView("notes", "notes", notesService.getNotes(user, pagination));
    }

    @RequestMapping(value = "/notes", headers = AJAX_HEADER, produces = CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson notesAjax(@Authorized User user, @RequestBody RequestJson request) throws ForgedRequestException {
        final Pagination pagination = pagination(request.getPagination());
        final Sorting sorting = sorting(request.getSorting());
        return new ResponseJson(notesService.getNotes(user, pagination, sorting));
    }

    @RequestMapping("/note/{id:\\d+}")
    public ModelAndView note(@Authorized User user, @PathVariable("id") int id) {
        return new ModelAndView("note", "note", notesService.getNote(user, id));
    }

    @RequestMapping(value = "/note/{id:\\d+}", headers = AJAX_HEADER, produces = CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson noteAjax(@Authorized User user, @PathVariable("id") int id) throws ForgedRequestException {
        return new ResponseJson(notesService.getNote(user, id));
    }

    private static class RequestJson {
        private SortingJson sorting = new SortingJson();
        private PaginationJson pagination = new PaginationJson();

        public SortingJson getSorting() {
            return sorting;
        }

        @SuppressWarnings("unused")
        public void setSorting(SortingJson sorting) {
            this.sorting = sorting;
        }

        public PaginationJson getPagination() {
            return pagination;
        }

        @SuppressWarnings("unused")
        public void setPagination(PaginationJson pagination) {
            this.pagination = pagination;
        }
    }

    private static class ResponseJson extends GenericResponseJson {
        private List<Note> notes;
        private Note note;

        public ResponseJson(List<Note> notes) {
            setStatusCode(STATUS_SUCCESS);
            this.notes = notes;
        }

        public ResponseJson(Note note) {
            setStatusCode(STATUS_SUCCESS);
            this.note = note;
        }

        @SuppressWarnings("unused")
        public List<Note> getNotes() {
            return notes;
        }

        @SuppressWarnings("unused")
        public Note getNote() {
            return note;
        }
    }
}
