package com.dsj.aicode.core;

import cn.hutool.core.util.ObjUtil;
import com.dsj.aicode.exception.BusinessException;
import com.dsj.aicode.exception.ErrorCode;
import com.dsj.aicode.ai.AiCodeGeneratorService;
import com.dsj.aicode.ai.model.HtmlCodeResult;
import com.dsj.aicode.ai.model.MultiFileCodeResult;
import com.dsj.aicode.core.parser.CodeParseExecutor;
import com.dsj.aicode.core.saver.CodeFileSaverExecutor;
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
     * 通过流式代码处理方法
     *
     * @param codeStream      代码流
     * @param codeGenTypeEnum 代码生成类型
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
//                .doOnNext(codeBuilder::append)
                .doOnNext(code -> {
                    codeBuilder.append(code);
                })
                .doOnComplete(() -> {
                    try {
                        String completeCode = codeBuilder.toString();
                        Object parsedResult = CodeParseExecutor.executeParser(completeCode, codeGenTypeEnum);
                        File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenTypeEnum, appId);
                        log.info("保存成功,路径为:" + savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败:{}", e.getMessage());
                    }
                });
    }


    /**
     * 统一入口: 根据类型生成并保存代码(流式)
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 流式返回
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型错误");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> flux = aiCodeGeneratorService.generateHtmlCodeStream(appId, userMessage);
                yield processCodeStream(flux, codeGenTypeEnum, appId);
            }
            case MULTI_FILE -> {
                Flux<String> flux = aiCodeGeneratorService.generateMultiFileCodeStream(appId, userMessage);
                yield processCodeStream(flux, codeGenTypeEnum, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型: " + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }


//    /**
//     * 生成多文件模式的代码并保存(流式)
//     *
//     * @param userMessage 用户提示词
//     * @return 返回流式
//     */
//    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
//        Flux<String> flux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
//        StringBuilder codeBuilder = new StringBuilder();
//        return flux
////                .doOnNext(codeBuilder::append)
//                .doOnNext(chunk -> {
//                    codeBuilder.append(chunk);
//                })
//                .doOnComplete(() -> {
//                    try {
//                        String completeMultiFileCode = codeBuilder.toString();
//                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeMultiFileCode);
//                        File file = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
//                        log.info("保存成功,路径为: " + file.getAbsolutePath());
//                    } catch (Exception e) {
//                        log.error("保存失败:{}", e.getMessage());
//                    }
//                });
//    }


//    /**
//     * 生成HTML模式的代码并保存(流式)
//     *
//     * @param userMessage 用户提示词
//     * @return 返回流式
//     */
//    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
//        Flux<String> flux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
//        StringBuilder codeBuilder = new StringBuilder();
//        return flux
//                .doOnNext(codeBuilder::append)//实时收集代码片段
//                .doOnComplete(() -> {
//                    try {
//                        String completeHtmlCode = codeBuilder.toString();
//                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
//                        File file = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//                        log.info("保存成功,路径为: " + file.getAbsolutePath());
//                    } catch (Exception e) {
//                        log.error("保存失败:{}", e.getMessage());
//                    }
//                });
//    }


    /**
     * 统一入口:根据类型生成代码并保存
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (ObjUtil.isEmpty(codeGenTypeEnum)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(appId,userMessage);
                yield CodeFileSaverExecutor.executeSaver(htmlCodeResult, codeGenTypeEnum, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(appId,userMessage);
                yield CodeFileSaverExecutor.executeSaver(multiFileCodeResult, codeGenTypeEnum, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型: " + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }

//    /**
//     * 生成HTML模式的代码并保存
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    private File generateAndSaveHtmlCode(String userMessage) {
//        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
//        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//    }

//    /**
//     * 生成多文件模式的代码并保存
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    private File generateAndSaveMultiFile(String userMessage) {
//        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
//        return CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
//    }
}
