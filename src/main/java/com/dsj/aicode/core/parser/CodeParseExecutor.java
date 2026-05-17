package com.dsj.aicode.core.parser;

import com.dsj.aicode.exception.BusinessException;
import com.dsj.aicode.exception.ErrorCode;
import com.dsj.aicode.model.enums.CodeGenTypeEnum;

/**
 * @author dongsijun
 * @date 2026/4/15  10:51
 */
public class CodeParseExecutor {

    public static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();

    public static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    /**
     * 执行代码解析
     *
     * @param codeContent 代码内容
     * @param codeGenType 代码生成类型
     * @return 解析结果
     */
    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> htmlCodeParser.parseCode(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        };
    }

}
