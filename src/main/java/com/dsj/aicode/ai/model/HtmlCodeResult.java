package com.dsj.aicode.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * @author dongsijun
 * @date 2026/4/14  09:09
 */
@Data
@Description("生成HTML代码文件的结果")
public class HtmlCodeResult {

    @Description("HTML代码")
    private String htmlCode;

    @Description("生成代码的描述")
    private String description;

}
