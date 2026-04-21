package com.dsj.aicode.core.saver;

import com.dsj.aicode.Exception.BusinessException;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.ai.model.HtmlCodeResult;
import com.dsj.aicode.ai.model.MultiFileCodeResult;
import com.dsj.aicode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 *
 * @author dongsijun
 * @date 2026/4/15  14:13
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaverTemplate = new HtmlCodeFileSaverTemplate();

    private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaverTemplate = new MultiFileCodeFileSaverTemplate();


    public static final File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaverTemplate.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> multiFileCodeFileSaverTemplate.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型:" + codeGenType);
        };
    }


}
