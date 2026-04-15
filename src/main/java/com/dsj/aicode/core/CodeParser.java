package com.dsj.aicode.core;

import com.dsj.aicode.ai.model.HtmlCodeResult;
import com.dsj.aicode.ai.model.MultiFileCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码解析器
 * 提供静态方法解析不同类型的代码内容
 *
 * @author dongsijun
 * @date 2026/4/15  09:11
 */
public class CodeParser {

    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);


    public static HtmlCodeResult parseHtmlCode(String codeContent) {
        HtmlCodeResult htmlCodeResult = new HtmlCodeResult();
        String htmlCode = extractHtmlCode(codeContent);
        if(htmlCode != null && !htmlCode.trim().isEmpty()) {
            htmlCodeResult.setHtmlCode(htmlCode);
        }else{
            htmlCodeResult.setHtmlCode(codeContent.trim());
        }
        return htmlCodeResult;
    }

    /**
     * 解析多文件代码(HTML + CSS + JS)
     * @param codeContent
     * @return
     */
    public static MultiFileCodeResult parseMultiFileCode(String codeContent){
        MultiFileCodeResult multiFileCodeResult = new MultiFileCodeResult();
        //提取各类代码
        String htmlCode = extractCodeByPattern(codeContent, HTML_CODE_PATTERN);
        String cssCode = extractCodeByPattern(codeContent, CSS_CODE_PATTERN);
        String jsCode = extractCodeByPattern(codeContent, JS_CODE_PATTERN);
        //设置Html代码
        if(htmlCode != null && !htmlCode.trim().isEmpty()){
            multiFileCodeResult.setHtmlCode(htmlCode);
        }
        if(cssCode != null && !cssCode.trim().isEmpty()){
            multiFileCodeResult.setCssCode(cssCode);
        }
        if(jsCode != null && !jsCode.trim().isEmpty()){
            multiFileCodeResult.setJsCode(jsCode);
        }
        return multiFileCodeResult;
    }

    /**
     * 提取Html代码的内容
     * @param code 原始内容
     * @return HTML代码
     */
    private static String extractHtmlCode(String code){
        Matcher matcher = HTML_CODE_PATTERN.matcher(code);
        if (matcher.find()){
            return matcher.group(1);
        }
        return null;
    }


    /**
     * 根据正则表达式提取代码
     * @param content 原始内容
     * @param pattern 正则模式
     * @return 提取的代码
     */
    private static String extractCodeByPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
