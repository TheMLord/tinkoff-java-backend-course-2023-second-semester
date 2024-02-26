package edu.java.bot.controller;

import api.UpdatesApi;
import model.LinkUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController implements UpdatesApi {

    @Override
    public ResponseEntity<Void> updatesPost(LinkUpdate linkUpdate) {
        return UpdatesApi.super.updatesPost(linkUpdate);
    }
}
