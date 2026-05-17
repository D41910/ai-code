package com.dsj.aicode.ai;

import com.dsj.aicode.ai.model.HtmlCodeResult;
import com.dsj.aicode.ai.model.MultiFileCodeResult;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * @author dongsijun
 * @date 2026/4/11  14:48
 */
public interface AiCodeGeneratorService {

    /**
     * 生成HTML代码
     *
     * @param userMessage 用户消息
     * @param memoryId 会话Id
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(@MemoryId long memoryId,@UserMessage String userMessage);

    /**
     * 生成对文件代码
     *
     * @param userMessage 用户消息
     * @param memoryId 会话Id
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileCodeResult generateMultiFileCode(@MemoryId long memoryId,@UserMessage String userMessage);


    /**
     * 生成HTML代码(流式)
     *
     * @param userMessage 用户消息
     * @param memoryId 会话Id
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(@MemoryId long memoryId, @UserMessage String userMessage);

    /**
     * 生成对文件代码(流式)
     *
     * @param userMessage 用户消息
     * @param memoryId 会话Id
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(@MemoryId long memoryId,@UserMessage String userMessage);

}
