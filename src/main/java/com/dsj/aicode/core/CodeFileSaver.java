package com.dsj.aicode.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.dsj.aicode.ai.model.HtmlCodeResult;
import com.dsj.aicode.ai.model.MultiFileCodeResult;
import com.dsj.aicode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * @author dongsijun
 * @date 2026/4/14  09:30
 */
public class CodeFileSaver {
    /**
     * 文件保存根目录
     */
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 保存 HtmlCodeResult
     * @param htmlCodeResult 生成结果
     * @return 保存目录
     */
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath,"index.html",htmlCodeResult.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存 MultiFileCodeResult
     * @param multiFileCodeResult 生成结果
     * @return 保存目录
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToFile(baseDirPath,"index.html", multiFileCodeResult.getHtmlCode());
        writeToFile(baseDirPath,"style.css", multiFileCodeResult.getCssCode());
        writeToFile(baseDirPath,"script.js", multiFileCodeResult.getJsCode());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一目录路径: tmp/code_output/bizType_雪花ID
     * @param bizType 保存类型
     * @return 保存路径
     */
    private static String buildUniqueDir(String bizType){
        String uniqueDirName = StrUtil.format("{}/{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     * @param dirPath 写入路径
     * @param fileName 文件名
     * @param content 内容
     */
    private static void writeToFile(String dirPath, String fileName, String content) {
        String filePath = dirPath + "/" + fileName;
        FileUtil.writeString(content, filePath, CharsetUtil.CHARSET_UTF_8);
    }

}
