package com.dsj.aicode.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.dsj.aicode.Exception.BusinessException;
import com.dsj.aicode.Exception.ErrorCode;
import com.dsj.aicode.ai.model.HtmlCodeResult;
import com.dsj.aicode.ai.model.MultiFileCodeResult;
import com.dsj.aicode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * @author dongsijun
 * @date 2026/4/15  11:02
 */
public abstract class CodeFileSaverTemplate<T> {
    /**
     * 文件保存根目录
     */
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";


    /**
     * 模版方法:保存代码的标准流程
     *
     * @param result 代码结果对象
     * @return 保存的目录
     */
    public final File saveCode(T result) {
        //输入校验
        validateInput(result);
        //构建唯一目录
        String baseDirPath = buildUniqueDir();
        //保存文件(具体实现由子类提供)
        saveFiles(result, baseDirPath);
        //返回文件目录文件对象
        return new File(baseDirPath);
    }

    /**
     * 验证输入参数(可由子嘞覆盖)
     *
     * @param result
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
        }
    }

    /**
     * 构建唯一目录路径: tmp/code_output/bizType_雪花ID
     *
     * @return 保存路径
     */
    protected final String buildUniqueDir() {
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}/{}", codeType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     *
     * @param dirPath  写入路径
     * @param fileName 文件名
     * @param content  内容
     */
    protected final void writeToFile(String dirPath, String fileName, String content) {
        String filePath = dirPath + "/" + fileName;
        FileUtil.writeString(content, filePath, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 获取代码类型(由子类实现)
     *
     * @return 代码生成类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件的具体实现(由子类是想)
     *
     * @param result      代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFiles(T result, String baseDirPath);

}
