package com.backendsems.news.interfaces.rest.transform;

import com.backendsems.news.domain.model.commands.CreateNewsItemCommand;
import com.backendsems.news.interfaces.rest.resources.CreateNewsItemResource;

public class CreateNewsItemCommandFromResourceAssembler {
    public static CreateNewsItemCommand toCommandFromResource(CreateNewsItemResource resource) {
        return new CreateNewsItemCommand(resource.title(), resource.content(), resource.isTip());
    }
}
