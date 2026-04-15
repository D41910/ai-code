package com.dsj.aicode.core;

import cn.hutool.core.util.ObjUtil;
import com.dsj.aicode.Exception.BusinessException;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.Exception.ThrowUtils;
import com.dsj.aicode.ai.AiCodeGeneratorService;
import com.dsj.aicode.ai.model.HtmlCodeResult;
import com.dsj.aicode.ai.model.MultiFileCodeResult;
import com.dsj.aicode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * @author dongsijun
 * @date 2026/4/14  16:13
 */
@Slf4j
@Service
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;


    /**
     * 统一入口: 根据类型生成并保存代码(流式)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 流式返回
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型错误");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCodeStream(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage);
            default -> {
                String errorMessage = "不支持的生成类型: " + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }


    /**
     * 生成多文件模式的代码并保存(流式)
     *
     * @param userMessage 用户提示词
     * @return 返回流式
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        Flux<String> flux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return flux
//                .doOnNext(codeBuilder::append)
                .doOnNext(chunk -> {
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    try {
                        String completeMultiFileCode = codeBuilder.toString();
                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeMultiFileCode);
                        File file = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
                        log.info("保存成功,路径为: " + file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败:{}", e.getMessage());
                    }
                });
    }


    /**
     * 生成HTML模式的代码并保存(流式)
     *
     * @param userMessage 用户提示词
     * @return 返回流式
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        Flux<String> flux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return flux
                .doOnNext(codeBuilder::append)//实时收集代码片段
                .doOnComplete(() -> {
                    try {
                        String completeHtmlCode = codeBuilder.toString();
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
                        File file = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
                        log.info("保存成功,路径为: " + file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败:{}", e.getMessage());
                    }
                });
    }


    /**
     * 统一入口:根据类型生成代码并保存
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (ObjUtil.isEmpty(codeGenTypeEnum)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFile(userMessage);
            default -> {
                String errorMessage = "不支持的生成类型: " + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

    /**
     * 生成HTML模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveMultiFile(String userMessage) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
    }
}
