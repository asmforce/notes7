package com.asmx.controllers;

import com.asmx.Constants;
import com.asmx.controllers.data.Authorized;
import com.asmx.controllers.data.entities.GenericResponseJson;
import com.asmx.controllers.data.entities.SortingJson;
import com.asmx.controllers.errors.NotFoundException;
import com.asmx.data.entities.Space;
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
 * Timestamp: 22.06.15 0:53.
**/
@Controller
public class SpacesController {
    @Autowired
    private NotesService notesService;

    @RequestMapping("/spaces")
    public ModelAndView spaces(@Authorized User user) {
        return new ModelAndView("spaces", "spaces", notesService.getSpaces(user.getId()));
    }

    @RequestMapping(value = "/spaces", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson spacesAjax(@Authorized User user, @RequestBody RequestJson request) {
        SortingJson sorting = request.getSorting();
        return new ResponseJson(notesService.getSpaces(user.getId(), ControllerUtils.sorting(sorting)));
    }

    @RequestMapping("/space/{id:\\d+}")
    public ModelAndView space(@Authorized User user, @PathVariable("id") int id) throws NotFoundException {
        Space space = notesService.getSpace(user.getId(), id);
        if (space == null) {
            throw new NotFoundException();
        }
        return new ModelAndView("space", "space", space);
    }

    @RequestMapping(value = "/space/{id:\\d+}", headers = Constants.AJAX_HEADER, produces = Constants.CONTENT_TYPE_JSON)
    @ResponseBody
    public ResponseJson spaceAjax(@Authorized User user, @PathVariable("id") int id) throws NotFoundException {
        Space space = notesService.getSpace(user.getId(), id);
        if (space == null) {
            throw new NotFoundException();
        }
        return new ResponseJson(space);
    }

    private static class RequestJson {
        private SortingJson sorting = new SortingJson();

        public SortingJson getSorting() {
            return sorting;
        }

        public void setSorting(SortingJson sorting) {
            this.sorting = sorting;
        }
    }

    private static class ResponseJson extends GenericResponseJson {
        private List<Space> spaces;
        private Space space;

        public ResponseJson(List<Space> spaces) {
            setStatusCode(STATUS_SUCCESS);
            this.spaces = spaces;
        }

        public ResponseJson(Space space) {
            setStatusCode(STATUS_SUCCESS);
            this.space = space;
        }

        @SuppressWarnings("unused")
        public List<Space> getSpaces() {
            return spaces;
        }

        @SuppressWarnings("unused")
        public Space getSpace() {
            return space;
        }
    }
}
