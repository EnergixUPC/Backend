package com.backendsems.helpcenter.interfaces.rest.transform;

import com.backendsems.helpcenter.domain.model.commands.CreateArticleCommand;
import com.backendsems.helpcenter.interfaces.rest.resources.CreateArticleResource;

public class CreateArticleCommandFromResourceAssembler {
    public static CreateArticleCommand toCommandFromResource(CreateArticleResource resource) {
        return new CreateArticleCommand(resource.title(), resource.content());
    }
}
