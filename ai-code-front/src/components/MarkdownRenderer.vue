<script setup lang="ts">
import { computed } from 'vue'
import { Marked } from 'marked'
import { markedHighlight } from 'marked-highlight'
import hljs from 'highlight.js/lib/core'
// 引入需要支持的语言
import javascript from 'highlight.js/lib/languages/javascript'
import html from 'highlight.js/lib/languages/xml'
import css from 'highlight.js/lib/languages/css'
import typescript from 'highlight.js/lib/languages/typescript'

// 注册语言
hljs.registerLanguage('javascript', javascript)
hljs.registerLanguage('html', html)
hljs.registerLanguage('css', css)
hljs.registerLanguage('xml', html)
hljs.registerLanguage('typescript', typescript)

// 创建 marked 实例并配置 highlight
const markedInstance = new Marked(
  markedHighlight({
    langPrefix: 'hljs language-',
    highlight(code: string, lang: string) {
      const language = hljs.getLanguage(lang) ? lang : 'plaintext'
      try {
        return hljs.highlight(code, { language }).value
      } catch {
        return code
      }
    },
  })
)

markedInstance.setOptions({
  breaks: true,
  gfm: true,
})

interface Props {
  content: string
}

const props = defineProps<Props>()

const renderedHtml = computed(() => {
  if (!props.content) return ''
  try {
    return markedInstance.parse(props.content) as string
  } catch {
    return props.content
  }
})
</script>

<template>
  <div class="markdown-body" v-html="renderedHtml"></div>
</template>

<style>
.markdown-body {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  word-wrap: break-word;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-body h1 {
  font-size: 2em;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 0.3em;
}

.markdown-body h2 {
  font-size: 1.5em;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 0.3em;
}

.markdown-body h3 {
  font-size: 1.25em;
}

.markdown-body h4 {
  font-size: 1em;
}

.markdown-body p {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 2em;
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body li {
  margin-bottom: 4px;
}

.markdown-body li > p {
  margin-top: 16px;
}

.markdown-body blockquote {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin: 0 0 16px 0;
}

.markdown-body blockquote > :first-child {
  margin-top: 0;
}

.markdown-body blockquote > :last-child {
  margin-bottom: 0;
}

.markdown-body code {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 6px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
}

.markdown-body pre {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
  margin-bottom: 16px;
}

.markdown-body pre code {
  padding: 0;
  margin: 0;
  background-color: transparent;
  border: 0;
  font-size: 100%;
}

.markdown-body table {
  border-collapse: collapse;
  margin-bottom: 16px;
  width: 100%;
}

.markdown-body table th,
.markdown-body table td {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-body table th {
  font-weight: 600;
  background-color: #f6f8fa;
}

.markdown-body table tr:nth-child(2n) {
  background-color: #f6f8fa;
}

.markdown-body hr {
  height: 0.25em;
  padding: 0;
  margin: 24px 0;
  background-color: #e1e4e8;
  border: 0;
}

.markdown-body a {
  color: #0366d6;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

.markdown-body img {
  max-width: 100%;
  box-sizing: content-box;
  display: block;
}

/* 代码高亮样式 - GitHub 风格 */
.hljs {
  color: #24292e;
  background: #f6f8fa;
}

.hljs-comment,
.hljs-quote {
  color: #6a737d;
  font-style: italic;
}

.hljs-keyword,
.hljs-selector-tag {
  color: #d73a49;
}

.hljs-string,
.hljs-addition {
  color: #032f62;
}

.hljs-number {
  color: #005cc5;
}

.hljs-built_in,
.hljs-type {
  color: #e36209;
}

.hljs-function,
.hljs-title {
  color: #6f42c1;
}

.hljs-attr,
.hljs-variable {
  color: #005cc5;
}

.hljs-tag {
  color: #22863a;
}

.hljs-name {
  color: #22863a;
}

.hljs-attribute {
  color: #6f42c1;
}

.hljs-meta {
  color: #6a737d;
}

.hljs-selector-class,
.hljs-selector-id {
  color: #6f42c1;
}
</style>
