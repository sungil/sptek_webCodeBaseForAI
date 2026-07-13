package com._sptek._webFrameworkExample.aiExample.feature.argument;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.web.binding.Enable_ArgumentResolver_At_Param;
import com._sptek._webFrameworkExample.aiExample.common.dto.AiExampleUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = "/api/ai-example/argument", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "AI Example - Argument", description = "ArgumentResolver sample")
public class AiExampleArgumentApiController {

    @GetMapping("/current-user")
    @Operation(summary = "Resolve a sample user from request parameters")
    public Object currentUser(@Enable_ArgumentResolver_At_Param AiExampleUserDto userDto) {
        return userDto;
    }
}
